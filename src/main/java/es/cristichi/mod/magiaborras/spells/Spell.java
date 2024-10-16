package es.cristichi.mod.magiaborras.spells;


import es.cristichi.mod.magiaborras.MagiaBorras;
import es.cristichi.mod.magiaborras.items.wand.prop.WandProperties;
import es.cristichi.mod.magiaborras.mixinaccess.EntitySpellsAccess;
import es.cristichi.mod.magiaborras.perdata.PlayerDataPS;
import es.cristichi.mod.magiaborras.spells.net.SpellHitPayload;
import es.cristichi.mod.magiaborras.spells.prop.SpellCastType;
import es.cristichi.mod.magiaborras.spells.prop.SpellParticles;
import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

public abstract class Spell {
    // TODO: Spells in Hogwards Legacy I think I want to add from the old version
    //  X Accio
    //  X ArrestoMomentum
    //  X Avada
    //  - Crucio
    //  X Depulso
    //  X Expelliarmus
    //  - Finite (if I figure out what to do with it)
    //  - Imperio
    //  X Incendio
    //  - Petrificus Totalus
    //  X Wingardium Leviosa

    // TODO: More Spells from H.L. I like that are not in the old version:
    //  X Alohomora (it opens iron doors/trapdoors)
    //  ! Lumos (I need to wait for other people to work dynamic lights on Minecraft 1.21)
    //  X Bombarda
    //  X Diffindo (as a substitute of the "Default" HL spell, since my "Default" does something different)
    //  X Revelio (glow all entities in the area)
    //  X Protego (Probably metadata/persistent data? Different keybind? Time to think!)

    // TODO: Other non H.L. Spells I'd love to implement (do I record the Spell cast .ogg myself?)
    //  - Morsmorde (for my friend, who clearly is not a Death Eater)
    //  - Finite Incantatem (for me to stop the annoying Morsmorde spam)
    //  X Ascendio
    //  X Vermillious

    // TODO: Unique Spells?
    //  - Tree Chopper Spell?
    //  - Redstone Spell?
    //  - Return-to-bed Spell?
    
    public static final double MAX_RANGE = 5000;

    static final Predicate<Entity> LIVING_ENTITIES = (entity -> !entity.isSpectator() && entity.canBeHitByProjectile());
    static final Predicate<Entity> ANY_ENTITY = (entity -> true);
    static final Predicate<Entity> NO_ENTITY = (entity -> false);
    static final Predicate<BlockState> ANY_BLOCK =  (block -> true);
    static final Predicate<BlockState> NO_BLOCK = (block -> false);

    protected String id;
    protected Text name;
    protected List<SpellCastType> castTypes;
    protected Predicate<Entity> affectableEntities;
    protected Predicate<BlockState> affectableBlocks;
    protected SpellParticles defaultParticles;
    protected int baseCooldown;

    protected Spell(String id, Text name, List<SpellCastType> castTypes, Predicate<Entity> affectableEntities,
                    Predicate<BlockState> affectableBlocks, SpellParticles defaultParticles,
                    int baseCooldown) {
        this.id = id;
        this.name = name;
        this.affectableEntities = affectableEntities;
        this.affectableBlocks = affectableBlocks;
        this.castTypes = castTypes;
        this.defaultParticles = defaultParticles;
        this.baseCooldown = baseCooldown;
    }

    public String getId() {
        return id;
    }

    public Text getName() {
        return name;
    }

    public List<SpellCastType> getCastTypes() {
        return castTypes;
    }

    public Predicate<Entity> getAffectableEntities() {
        return affectableEntities;
    }

    public Predicate<BlockState> getAffectableBlocks() {
        return affectableBlocks;
    }

    public SpellParticles getDefaultParticles() {
        return new SpellParticles(defaultParticles);
    }

    /**
     * This is what is called if the Spell can hit. Spells can individually decide to return FAIL on the run to still
     * fail the Spell.
     * @param wand Item bein used
     * @param properties Properties of that Wand already prepared
     * @param magicUser Magic user providing the magic!
     * @param world ServerWorld where the Spell is cast
     * @param hit An EntityHitResult if the hit is an Entity, a BlockHitResult if it was a Block, or an empty HitResult
     *            if the caster right clicked looking at the sky or a block/entity too far away
     * @return a non null Spell.Result that defines whether the Spell was successful and the information needed from the
     * execution of the Spell to draw particles and play sounds
     */
    @NotNull
    protected abstract Spell.Result resolveEffect(ItemStack wand, WandProperties properties,
                                                  ServerPlayerEntity magicUser, ServerWorld world, HitResult hit);

    /**
     * This is the correct way to cast a Spell as normal. It checks if the caster knows the spell, if the Wand is on
     * cooldown, it calculates what it should hit, etc.
     * Not to be modified.
     * @param world ServerWorld where the Spell is cast
     * @param user User trying to cast the Spell
     * @param wand the Wand being used
     * @param properties Properties of that Wand already prepared
     * @return a TypedActionResult indicating whether the casting of the Spell was successful or not
     */
    public TypedActionResult<ItemStack> cast(ServerWorld world, PlayerEntity user, ItemStack wand, WandProperties properties) {
        if (!user.getItemCooldownManager().isCoolingDown(wand.getItem())){
            PlayerDataPS.PlayerMagicData data = MagiaBorras.playerDataPS.getOrGenerateData(user);
            if (properties != null) {
                if (getCastTypes().contains(SpellCastType.USE)) {
                    if (user.isCreative() || getId().equals("") || data.containsSpell(this)) {

                        HitResult hit;
                        Vec3d userCamPoint = user.getCameraPosVec(0);
                        Vec3d rotation = user.getRotationVec(0);
                        Vec3d furthestHitPossible = userCamPoint.add(rotation.x * MAX_RANGE, rotation.y * MAX_RANGE, rotation.z * MAX_RANGE);

                        if (affectableEntities == Spell.NO_ENTITY){
                            hit = new HitResult(furthestHitPossible) {
                                @Override
                                public Type getType() {
                                    return Type.MISS;
                                }
                            };
                        } else {
                            EntityHitResult entHit = raycast((ServerPlayerEntity) user, this.getAffectableEntities(), userCamPoint, furthestHitPossible, MAX_RANGE);
                            if (entHit.getEntity() == null){
                                hit = new HitResult(furthestHitPossible) {
                                    @Override
                                    public Type getType() {
                                        return Type.MISS;
                                    }
                                };
                            } else {
                                hit = entHit;
                            }
                        }

                        // We always check for block hits since Spells don't go through blocks
                        //HitResult hitBlock = user.raycast(MAX_RANGE, 1, false);
                        HitResult hitBlock = world.raycast(new RaycastContext(userCamPoint, furthestHitPossible,
                                RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, user));
                        if (hitBlock instanceof BlockHitResult blockHitResult
                                && !getAffectableBlocks().test(world.getBlockState(blockHitResult.getBlockPos()))){
                            hitBlock = new HitResult(hitBlock.getPos()) {
                                @Override
                                public Type getType() {
                                    return Type.MISS;
                                }
                            };
                        }

                        double distEHit = hit.getPos()==null?MAX_RANGE:user.getEyePos().squaredDistanceTo(hit.getPos());
                        double distBHit = hitBlock.getPos()==null?MAX_RANGE:user.getEyePos().squaredDistanceTo(hitBlock.getPos());

                        if (distEHit > distBHit){
                            hit = hitBlock;
                        }

                        boolean blocked = false;
                        if (hit instanceof EntityHitResult entityHitResult){
                            if (entityHitResult.getEntity() instanceof EntitySpellsAccess ent){
                                blocked = ent.magiaborras_isProtegoActive();
                                if (!blocked && ent instanceof LivingEntity livEnt){
                                    blocked = livEnt.blockedByShield(user.getDamageSources().playerAttack(user));
                                }
                            }
                        }

                        Spell.Result result;
                        if (blocked){
                            result = new Spell.Result(
                                    ActionResult.SUCCESS, Protego.PUNISH_COOLDOWM, List.of(MagiaBorras.SPELLBLOCKED_SOUNDEVENT));
                        } else {
                            result = resolveEffect(wand, properties, (ServerPlayerEntity) user, world, hit);
                        }
                        // CD of the Spell. Spells can determine a CD based on the outcome, including failing.
                        // For example, the avada gives you some CD on missing while Diffindo allows you to
                        // hold right click no problemo.
                        if (!user.isCreative()) {
                            user.getItemCooldownManager().set(wand.getItem(), result.cooldown());
                        }

                        // If Spell is successfull
                        if (result.actionResult().isAccepted()) {
                            // Sound of the spell
                            for (SoundEvent sound : result.sounds()) {
                                world.playSound(null, user.getBlockPos(), sound, SoundCategory.PLAYERS, 1f, 1f);
                            }

                            // Particles of the Spell
                            SpellParticles particles = (result.particles()==null) ? getDefaultParticles() : result.particles();
                            if (particles.getType() != SpellParticles.SpellParticleType.NO_PARTICLES){
                                Collection<ServerPlayerEntity> players = PlayerLookup.tracking(world, user.getBlockPos());
                                SpellHitPayload packet = new SpellHitPayload(
                                        user.getEyePos().add(0, -0.2, 0), hit.getPos(),
                                        particles);
                                for (ServerPlayerEntity player : players){
                                    ServerPlayNetworking.send(player, packet);
                                }
                            }
                        }
                        return new TypedActionResult<>(result.actionResult(), wand);

                    } else {
                        user.sendMessage(Text.translatable("magiaborras.spell.locked", getName()));
                        return TypedActionResult.fail(wand);
                    }
                }
            }
        }
        return TypedActionResult.fail(wand);
    }

    private static EntityHitResult raycast(ServerPlayerEntity user, Predicate<Entity> targettable, Vec3d startPoint, Vec3d endPoint, double maxDistance) {
        ServerWorld world = (ServerWorld) user.getWorld();
        double dist = startPoint.distanceTo(endPoint);

        Entity currentTargetEnt = null;
        Vec3d currentTargetHitPoint = null;
        double currentTargetDistance = maxDistance;

        Collection<Entity> potentialTargets = world.getOtherEntities(user, user.getBoundingBox().expand(MAX_RANGE), targettable);
        if (user.getVehicle() != null){
            potentialTargets.remove(user.getVehicle());
        }

        for (Entity potentialTargetEnt : potentialTargets) {
            Box reasonableHitbox = potentialTargetEnt.getBoundingBox().expand(potentialTargetEnt.getTargetingMargin());
            Optional<Vec3d> pointCrossHitbox = reasonableHitbox.raycast(startPoint, endPoint);

            // If the line between closest and furthest crosses the hitbox of this potential target
            if (pointCrossHitbox.isPresent()) {
                Vec3d potTargetHitPoint = pointCrossHitbox.get();
                double distancePotEnt = startPoint.distanceTo(potTargetHitPoint);
                if (distancePotEnt < currentTargetDistance) {
                    currentTargetEnt = potentialTargetEnt;
                    currentTargetHitPoint = potTargetHitPoint;
                    currentTargetDistance = distancePotEnt;
                }
            }
        }

        return new EntityHitResult(currentTargetEnt, currentTargetHitPoint == null ? endPoint : currentTargetHitPoint);
    }

    public static final class Result {
        private final ActionResult actionResult;
        private final int cooldown;
        private final List<SoundEvent> sounds;
        private final SpellParticles particles;

        public Result(ActionResult actionResult, int cooldown, List<SoundEvent> sounds, SpellParticles particles) {
            this.actionResult = actionResult;
            this.cooldown = cooldown;
            this.sounds = sounds;
            this.particles = particles;
        }

        public Result(ActionResult actionResult, int cooldown, List<SoundEvent> sounds) {
            this.actionResult = actionResult;
            this.cooldown = cooldown;
            this.sounds = sounds;
            this.particles = null;
        }

        public ActionResult actionResult() {
            return actionResult;
        }

        public int cooldown() {
            return cooldown;
        }

        public List<SoundEvent> sounds() {
            return sounds;
        }

        @Nullable
        public SpellParticles particles() {
            return particles;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (Result) obj;
            return Objects.equals(this.actionResult, that.actionResult) &&
                    this.cooldown == that.cooldown &&
                    Objects.equals(this.sounds, that.sounds) &&
                    Objects.equals(this.particles, that.particles);
        }

        @Override
        public int hashCode() {
            return Objects.hash(actionResult, cooldown, sounds, particles);
        }

        @Override
        public String toString() {
            return "Result[" +
                    "actionResult=" + actionResult + ", " +
                    "cooldown=" + cooldown + ", " +
                    "sounds=" + sounds + ", " +
                    "particles=" + particles + ']';
        }

        }

    public static final PacketCodec<ByteBuf, Spell> PACKET_CODEC = new PacketCodec<>() {
        public Spell decode(ByteBuf byteBuf) {
            NbtCompound data = PacketByteBuf.readNbt(byteBuf);
            if (data != null) {
                String spell = data.getString("spell");
                if (MagiaBorras.SPELLS.containsKey(spell)) {
                    return MagiaBorras.SPELLS.get(spell);
                }
            }
            return MagiaBorras.SPELLS.get("");
        }

        public void encode(ByteBuf byteBuf, Spell spell) {
            NbtCompound data = new NbtCompound();
            data.putString("spell", spell.getId());
            PacketByteBuf.writeNbt(byteBuf, data);
        }
    };
}
