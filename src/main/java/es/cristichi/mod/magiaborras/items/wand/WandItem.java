package es.cristichi.mod.magiaborras.items.wand;

import es.cristichi.mod.magiaborras.MagiaBorras;
import es.cristichi.mod.magiaborras.PlayerDataPS;
import es.cristichi.mod.magiaborras.items.wand.prop.*;
import es.cristichi.mod.magiaborras.networking.SpellHitPayload;
import es.cristichi.mod.magiaborras.spells.Spell;
import es.cristichi.mod.magiaborras.spells.prop.SpellCastType;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
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
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class WandItem extends Item {
    public WandItem(Settings settings) {
        super(settings.maxCount(1));
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
                MagiaBorras.LOGGER.error("Oh no", e);
            }
        }
    }

    private static final double MAX_DISTANCE = 500;

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {

        if (!world.isClient()){
            ItemStack stack = user.getStackInHand(hand);
            WandProperties prop = WandProperties.check(stack);
            PlayerDataPS.PlayerMagicData data = MagiaBorras.playerDataPS.getOrGenerateData(user);
            if (prop != null) {
                if (prop.spell.getCastTypes().contains(SpellCastType.USE)) {
                    if (user.isCreative() || prop.spell.getId().equals("") || data.containsSpell(prop.spell)) {
                        Vec3d camPos = user.getCameraPosVec(0);
                        Vec3d rotation = user.getRotationVec(0);
                        Vec3d ray = camPos.add(rotation.x * MAX_DISTANCE, rotation.y * MAX_DISTANCE, rotation.z * MAX_DISTANCE);
                        Box box = user.getBoundingBox().stretch(rotation.multiply(MAX_DISTANCE)).expand(1d, 1d, 1d);
                        HitResult hit = ProjectileUtil.raycast(user, camPos, ray, box, prop.spell.getAffectableEntities(), MAX_DISTANCE);

                        if (hit == null) {
                            hit = user.raycast(MAX_DISTANCE, 0, false);
                            if (hit instanceof BlockHitResult blockHitResult){
                                if (!prop.spell.getAffectableBlocks().test(world.getBlockState(blockHitResult.getBlockPos()))){
                                    hit = new HitResult(hit.getPos()) {
                                        @Override
                                        public Type getType() {
                                            return Type.MISS;
                                        }
                                    };
                                }
                            }
                        }

                        Spell.Result result = prop.spell.use(stack, prop, user, world, hit);

                        // CD of the Spell. Spells can determine a CD based on the outcome, including failing.
                        // For example, the avada gives you some CD on missing while Stupefy allows you to
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
                            if (prop.spell.getParticlesColor() != null){
                                Collection<ServerPlayerEntity> players = PlayerLookup.tracking((ServerWorld) world, user.getBlockPos());
                                for (ServerPlayerEntity player : players){
                                    ServerPlayNetworking.send(player, new SpellHitPayload(
                                        user.getEyePos().add(0, -0.2, 0),
                                        hit.getPos(),
                                        prop.spell.getParticlesColor()));
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
            }
        }
        return TypedActionResult.fail(user.getStackInHand(hand));
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

}
