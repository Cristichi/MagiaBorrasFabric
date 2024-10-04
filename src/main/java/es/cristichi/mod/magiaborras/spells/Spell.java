package es.cristichi.mod.magiaborras.spells;


import es.cristichi.mod.magiaborras.MagiaBorras;
import es.cristichi.mod.magiaborras.items.wand.prop.WandProperties;
import es.cristichi.mod.magiaborras.spells.prop.SpellCastType;
import es.cristichi.mod.magiaborras.spells.prop.SpellParticles;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
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
    //  X Incendio (add functionality for furnaces :D)
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
    //  - Ascendio

    // TODO: Unique Spells?
    //  - Tree Chopper Spell
    //  - Redstone Spell
    //  - Spell that marks a place to all players in the area, like a ping

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
        return defaultParticles;
    }

    public abstract Spell.Result cast(ItemStack wand, WandProperties properties,
                                      ServerPlayerEntity magicUser, World world, HitResult hit);

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
