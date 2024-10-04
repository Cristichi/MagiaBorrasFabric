package es.cristichi.mod.magiaborras.items.wand;

import es.cristichi.mod.magiaborras.MagiaBorras;
import es.cristichi.mod.magiaborras.items.wand.prop.*;
import es.cristichi.mod.magiaborras.perdata.PlayerDataPS;
import es.cristichi.mod.magiaborras.spells.Protego;
import es.cristichi.mod.magiaborras.spells.Spell;
import es.cristichi.mod.magiaborras.spells.net.SpellHitPayload;
import es.cristichi.mod.magiaborras.spells.prop.SpellCastType;
import es.cristichi.mod.magiaborras.spells.prop.SpellParticles;
import es.cristichi.mod.magiaborras.timer.SpellTimersAccess;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class WandItem extends Item {
    public static final double MAX_DISTANCE = 500;

    public WandItem(Settings settings) {
        super(settings.maxCount(1).fireproof().rarity(Rarity.RARE));
    }

    @Override
    public ItemStack getDefaultStack() {
        ItemStack is = super.getDefaultStack();
        WandProperties props = new WandProperties(
                WandCore.NONE,
                WandWood.NONE,
                WandFlexibility.NONE,
                WandLength.NONE,
                0.5f,
                MagiaBorras.SPELLS.get(""),
                false);
        props.apply(is);
        return is;
    }

    @Override
    public void onCraft(ItemStack stack, World world) {
        if (!world.isClient()) {
            try {
                WandProperties props = new WandProperties();
                props.apply(stack);
            } catch (Exception e) {
                MagiaBorras.LOGGER.error("Error when trying to craft Wand.", e);
            }
        }
    }

    @Override
    public void onCraftByPlayer(ItemStack stack, World world, PlayerEntity player) {
        if (world.isClient()) {
            player.playSound(SoundEvents.BLOCK_CONDUIT_ACTIVATE, 1.0F, 1.0F);
        } else {
            try {
                WandProperties props = new WandProperties();
                props.apply(stack);
                ArrayList<Identifier> recipes = new ArrayList<>(MagiaBorras.SPELLS.size());
                // It is intended design that only the recipes "spellbook_spellname" are unlocked.
                // Alternative recipes like spellbook_avada_head are "hidden".
                for (String spell : MagiaBorras.SPELLS.keySet()) {
                    recipes.add(Identifier.of(MagiaBorras.MOD_ID, "spellbook_" + spell));
                }
                player.unlockRecipes(recipes);
            } catch (Exception e) {
                MagiaBorras.LOGGER.error("Error when player trying to craft Wand.", e);
            }
        }
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        WandProperties prop = WandProperties.check(stack);

        if (prop == null) {
            return TypedActionResult.fail(stack);
        }
        return useWithSpell(world, user, stack, prop, prop.spell);
    }
    public TypedActionResult<ItemStack> useWithSpell(World world, PlayerEntity user, ItemStack stack, WandProperties prop, Spell spell) {
        if (!world.isClient() && !user.getItemCooldownManager().isCoolingDown(this)){
            PlayerDataPS.PlayerMagicData data = MagiaBorras.playerDataPS.getOrGenerateData(user);
            if (prop != null) {
                if (spell.getCastTypes().contains(SpellCastType.USE)) {
                    if (user.isCreative() || spell.getId().equals("") || data.containsSpell(spell)) {
                        Vec3d camPos = user.getCameraPosVec(0);
                        Vec3d rotation = user.getRotationVec(0);
                        Vec3d ray = camPos.add(rotation.x * MAX_DISTANCE, rotation.y * MAX_DISTANCE, rotation.z * MAX_DISTANCE);
                        Box box = user.getBoundingBox().stretch(rotation.multiply(MAX_DISTANCE)).expand(1d, 1d, 1d);
                        HitResult hit = WandItem.raycast(user, camPos, ray, box, spell.getAffectableEntities(), MAX_DISTANCE);
                        if (hit instanceof EntityHitResult entityHitResult && entityHitResult.getEntity() == null){
                            hit = new HitResult(hit.getPos()) {
                                @Override
                                public Type getType() {
                                    return Type.MISS;
                                }
                            };
                        }
                        // This is always a block hit
                        HitResult hitBlock = user.raycast(MAX_DISTANCE, 0, false);
                        if (hitBlock instanceof BlockHitResult blockHitResult
                                && !spell.getAffectableBlocks().test(world.getBlockState(blockHitResult.getBlockPos()))){
                            hitBlock = new HitResult(hitBlock.getPos()) {
                                @Override
                                public Type getType() {
                                    return Type.MISS;
                                }
                            };
                        }

                        double distEHit = hit.getPos()==null?Double.MAX_VALUE:user.getEyePos().squaredDistanceTo(hit.getPos());
                        double distBHit = hitBlock.getPos()==null?Double.MAX_VALUE:user.getEyePos().squaredDistanceTo(hitBlock.getPos());

                        if (distEHit > distBHit){
                            hit = hitBlock;
                        }
                        boolean blocked = false;
                        if (hit instanceof EntityHitResult entityHitResult){
                            if (entityHitResult.getEntity() instanceof SpellTimersAccess ent){
                                blocked = ent.magiaborras_isProtegoActive();
                            }
                        }

                        Spell.Result result;
                        if (blocked){
                            result = new Spell.Result(
                                    ActionResult.SUCCESS, Protego.PUNISH_COOLDOWM, List.of(MagiaBorras.SPELLBLOCKED_SOUNDEVENT));
                        } else {
                            result = spell.cast(stack, prop, (ServerPlayerEntity) user, world, hit);
                        }
                        // CD of the Spell. Spells can determine a CD based on the outcome, including failing.
                        // For example, the avada gives you some CD on missing while Diffindo allows you to
                        // hold right click no problemo.
                        if (!user.isCreative()) {
                            user.getItemCooldownManager().set(this, result.cooldown());
                        }

                        // If Spell is successfull
                        if (result.actionResult().isAccepted()) {
                            // Sound of the spell
                            for (SoundEvent sound : result.sounds()) {
                                world.playSound(null, user.getBlockPos(), sound, SoundCategory.PLAYERS, 1f, 1f);
                            }

                            // Particles of the Spell
                            SpellParticles particles = result.particles()==null?spell.getDefaultParticles():result.particles();
                            if (particles.getType() != SpellParticles.SpellParticleType.NO_PARTICLES){
                                Collection<ServerPlayerEntity> players = PlayerLookup.tracking((ServerWorld) world, user.getBlockPos());
                                for (ServerPlayerEntity player : players){
                                    ServerPlayNetworking.send(player, new SpellHitPayload(
                                            user.getEyePos().add(0, -0.2, 0), hit.getPos(),
                                            particles));
                                }
                            }
                        }
                        return new TypedActionResult<>(result.actionResult(), stack);

                    } else {
                        user.sendMessage(Text.translatable("magiaborras.spell.locked"));
                        return TypedActionResult.fail(stack);
                    }
                }
            } else {
                user.sendMessage(Text.translatable("item.magiaborras.wand.broken"));
                prop = new WandProperties();
                prop.apply(stack);
            }
        }
        return TypedActionResult.fail(stack);
    }


    @Override
    public Optional<TooltipData> getTooltipData(ItemStack stack) {
        return super.getTooltipData(stack);
    }

    @Override
    public void appendTooltip(ItemStack itemStack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        WandProperties prop = WandProperties.check(itemStack);
        if (prop != null) {
            tooltip.add(Text.stringifiedTranslatable("item.magiaborras.wand.tooltip_core",
                    prop.core.getName()));
            tooltip.add(Text.stringifiedTranslatable("item.magiaborras.wand.tooltip_wood",
                    prop.wood.getName()));
            tooltip.add(Text.stringifiedTranslatable("item.magiaborras.wand.tooltip_flex",
                    prop.flex.getName()));
            tooltip.add(Text.stringifiedTranslatable("item.magiaborras.wand.tooltip_length",
                    prop.length.getName()));
        }
    }

    private static EntityHitResult raycast(Entity entity, Vec3d min, Vec3d max, Box box, Predicate<Entity> predicate, double maxDistance) {
        World world = entity.getWorld();
        double d = maxDistance;
        Entity entity2 = null;
        Vec3d vec3d = null;

        for (Entity entity3 : world.getOtherEntities(entity, box, predicate)) {
            Box box2 = entity3.getBoundingBox().expand(entity3.getTargetingMargin());
            Optional<Vec3d> optional = box2.raycast(min, max);
            if (box2.contains(min)) {
                if (d >= 0.0) {
                    entity2 = entity3;
                    vec3d = optional.orElse(min);
                    d = 0.0;
                }
            } else if (optional.isPresent()) {
                Vec3d vec3d2 = optional.get();
                double e = min.squaredDistanceTo(vec3d2);
                if (e < d || d == 0.0) {
                    if (entity3.getRootVehicle() == entity.getRootVehicle()) {
                        if (d == 0.0) {
                            entity2 = entity3;
                            vec3d = vec3d2;
                        }
                    } else {
                        entity2 = entity3;
                        vec3d = vec3d2;
                        d = e;
                    }
                }
            }
        }

        return new EntityHitResult(entity2, vec3d);
    }
}
