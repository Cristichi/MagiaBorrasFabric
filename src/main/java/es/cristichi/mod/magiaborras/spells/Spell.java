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

    // This is from an attempt to divide Spells in a server and client side, but I realised I like the server-only more
    // I'm keeping it in case I need to redo it again for some other data
//    public record Result(Spell spell, ActionResult actionResult, Hand hand, int cooldown, List<SoundEvent> sounds) {
//
//        public static final PacketCodec<ByteBuf, Result> PACKET_CODEC = new PacketCodec<ByteBuf, Result>() {
//            public Result decode(ByteBuf byteBuf) {
//                try {
//                    NbtCompound data = PacketByteBuf.readNbt(byteBuf);
//                    if (data != null) {
//                        Spell spell = null;
//                        String spellStr = data.getString("spell");
//                        if (MagiaBorras.SPELLS.containsKey(spellStr)){
//                            spell = MagiaBorras.SPELLS.get(spellStr);
//                        }
//
//                        ActionResult result = ActionResult.values()[data.getInt("result")];
//                        int cd = data.getInt("cd");
//                        Hand hand = Hand.values()[data.getInt("hand")];
//                        String soundsStr = data.getString("sounds");
//                        StringTokenizer soundsTks = new StringTokenizer(soundsStr, ";");
//                        ArrayList<SoundEvent> soundEvents = new ArrayList<>(soundsTks.countTokens());
//                        while (soundsTks.hasMoreTokens()){
//                            String token = soundsTks.nextToken();
//                            String[] idData = token.split(":");
//                            soundEvents.add(SoundEvent.of(Identifier.of(idData[0], idData[1])));
//                        }
//                        return new Result(spell, result, hand, cd, soundEvents);
//                    }
//                } catch (Exception e) {
//                    throw new SpellPacketDecoderException("Error trying to decode a Spell packet.", e);
//                }
//                throw new SpellPacketDecoderException("Error trying to decode a Spell packet.");
//            }
//
//            public void encode(ByteBuf byteBuf, Result result) {
//                NbtCompound data = new NbtCompound();
//                data.putString("spell", result.spell.getId());
//                data.putInt("result", result.actionResult.ordinal());
//                data.putInt("cd", result.cooldown);
//                data.putInt("hand", result.hand.ordinal());
//                StringBuilder sounds = new StringBuilder();
//                boolean first = true;
//                for (SoundEvent sound : result.sounds){
//                    if (first){
//                        first = false;
//                    } else {
//                        sounds.append(";");
//                    }
//                    sounds.append(sound.getId().toString());
//                }
//                data.putString("sounds", sounds.toString());
//                MagiaBorras.LOGGER.info("Tenemos los sonidos: {}", sounds);
//                PacketByteBuf.writeNbt(byteBuf, data);
//            }
//        };
//    }

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
