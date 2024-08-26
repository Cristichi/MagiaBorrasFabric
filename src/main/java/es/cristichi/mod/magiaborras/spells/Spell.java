package es.cristichi.mod.magiaborras.spells;


import es.cristichi.mod.magiaborras.MagiaBorras;
import es.cristichi.mod.magiaborras.items.wand.prop.WandProperties;
import es.cristichi.mod.magiaborras.spells.prop.SpellCastType;
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
import org.joml.Vector3f;

import java.util.List;
import java.util.function.Predicate;

public abstract class Spell {
    // TODO: Spells in Hogwards Legacy I think I want to add from the old version
    //  X Accio
    //  X ArrestoMomentum
    //  X Avada
    //  - Crucio
    //  X Depulso
    //  - Expelliarmus
    //  - Finite (if I figure out what to do with it)
    //  - Imperio
    //  X Incendio (add functionality for furnaces :D)
    //  - Petrificus Totalus
    //  - Stupefy
    //  X Wingardium Leviosa

    // TODO: More Spells from H.L. I like that are not in the old version:
    //  X Alohomora (it opens iron doors/trapdoors)
    //  ? Lumos (I need to wait for other people to work dynamic lights on Minecraft 1.21)
    //  - Bombarda
    //  X Diffindo (as a substitute of the "Default" HL spell, since my "Default" does something different)

    // TODO: Other Spells I'd love to implement (do I record the Spell cast .ogg myself?)
    //  - Morsmorde (for my friend, who clearly is not a Death Eater)
    //  - Finite Incantatem (for me to stop the annoying Morsmorde spam)

    // TODO: Unique Spells:
    //  ? Tree Chopper Spell
    //  ? Redstone Spell
    //  ?

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
    @Nullable
    protected Vector3f partColor;
    protected int baseCooldown;

    protected Spell(String id, Text name, List<SpellCastType> castTypes, Predicate<Entity> affectableEntities,
                    Predicate<BlockState> affectableBlocks, @Nullable Vector3f partColor, int baseCooldown) {
        this.id = id;
        this.name = name;
        this.affectableEntities = affectableEntities;
        this.affectableBlocks = affectableBlocks;
        this.castTypes = castTypes;
        this.partColor = partColor;
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

    public @Nullable Vector3f getParticlesColor() {
        return partColor;
    }

    public abstract Spell.Result cast(ItemStack wand, WandProperties properties,
                                      ServerPlayerEntity magicUser, World world, HitResult hit);

    public record Result(ActionResult actionResult, int cooldown, List<SoundEvent> sounds) {
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
