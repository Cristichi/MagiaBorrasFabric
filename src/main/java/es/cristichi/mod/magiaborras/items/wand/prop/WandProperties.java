package es.cristichi.mod.magiaborras.items.wand.prop;

import com.mojang.serialization.Codec;
import es.cristichi.mod.magiaborras.MagiaBorras;
import es.cristichi.mod.magiaborras.spells.Spell;
import es.cristichi.mod.magiaborras.PlayerDataPS;
import net.minecraft.component.ComponentMapImpl;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.dynamic.Codecs;
import org.jetbrains.annotations.Nullable;

import java.util.function.UnaryOperator;

public class WandProperties {
    public WandCore core;
    public WandWood wood;
    public WandFlexibility flex;
    public WandLength length;
    public Float magicNumber;
    public Spell spell;
    public boolean lumos;

    public WandProperties() {
        this.core = WandCore.getRandomCore(MagiaBorras.RNG);
        this.wood = WandWood.getRandom(MagiaBorras.RNG);
        this.flex = WandFlexibility.getRandom(MagiaBorras.RNG);
        this.length = WandLength.getRandom(MagiaBorras.RNG);
        this.magicNumber = MagiaBorras.RNG.nextFloat();
        this.spell = MagiaBorras.SPELLS.get("");
        this.lumos = false;
    }

    public WandProperties(WandCore core, WandWood wood, WandFlexibility flex, WandLength length, Float magicNumber, Spell spell, boolean lumos) {
        this.core = core;
        this.wood = wood;
        this.flex = flex;
        this.length = length;
        this.magicNumber = magicNumber;
        this.spell = spell;
        this.lumos = lumos;
    }

    public void apply(ItemStack stack) {
        ComponentMapImpl components = new ComponentMapImpl(stack.getComponents());
        components.set(DATA_CORE, core.ordinal());
        components.set(DATA_WOOD, wood.ordinal());
        components.set(DATA_FLEX, flex.ordinal());
        components.set(DATA_LENGTH, length.ordinal());
        components.set(DATA_MAGIC_NUM, magicNumber);
        components.set(DATA_SPELL, spell.getId());
        components.set(DATA_LUMOS, lumos);
        if (spell.getId().equals("")){
            components.remove(DataComponentTypes.CUSTOM_NAME);
            components.set(DataComponentTypes.CUSTOM_NAME, Text.translatable("item.magiaborras.wand"));
        } else {
            components.set(DataComponentTypes.CUSTOM_NAME, Text.translatable("item.magiaborras.wand_spell", spell.getName()));
        }
        stack.applyComponentsFrom(components);
    }

    /**
     * @param stack ItemStack to check
     * @return null if this is not a correct Wand. WandProperties with properties otherwise.
     */
    @Nullable
    public static WandProperties check(ItemStack stack) {
        ComponentMapImpl map = new ComponentMapImpl(stack.getComponents());
        if (map.contains(DATA_CORE) ||
                map.contains(DATA_WOOD) ||
                map.contains(DATA_FLEX) ||
                map.contains(DATA_LENGTH) ||
                map.contains(DATA_MAGIC_NUM) ||
                map.contains(DATA_SPELL) ||
                map.contains(DATA_LUMOS)
        ) {
            try {
                Integer core = map.getOrDefault(DATA_CORE, null);
                Integer wood = map.getOrDefault(DATA_WOOD, null);
                Integer flex = map.getOrDefault(DATA_FLEX, null);
                Integer length = map.getOrDefault(DATA_LENGTH, null);
                Float magNum = map.getOrDefault(DATA_MAGIC_NUM, null);
                String spell = map.getOrDefault(DATA_SPELL, null);
                boolean lumos = map.getOrDefault(DATA_LUMOS, false);

                return new WandProperties(
                        WandCore.values()[core],
                        WandWood.values()[wood],
                        WandFlexibility.values()[flex],
                        WandLength.values()[length],
                        magNum,
                        MagiaBorras.SPELLS.get(spell),
                        lumos
                );
            } catch (NullPointerException e) {
                MagiaBorras.LOGGER.warn("Wand is incomplete. Oh no!", e);
                return null;
            }
        }
        return null;
    }

    public float getPower(PlayerEntity magicUser) {
        PlayerDataPS.PlayerMagicData data = MagiaBorras.playerDataPS.getOrGenerateData(magicUser);
        float pot = 1 - Math.abs(data.getMagicNumber() - magicNumber);
        return Math.max(0, pot);
    }

    private static <T> ComponentType<T> register(String id, UnaryOperator<ComponentType.Builder<T>> builderOperator) {
        return Registry.register(Registries.DATA_COMPONENT_TYPE, id, builderOperator.apply(ComponentType.builder()).build());
    }

    public static ComponentType<Integer> DATA_CORE;
    public static ComponentType<Integer> DATA_WOOD;
    public static ComponentType<Integer> DATA_FLEX;
    public static ComponentType<Integer> DATA_LENGTH;
    public static ComponentType<Float> DATA_MAGIC_NUM;

    public static ComponentType<String> DATA_SPELL;

    public static ComponentType<Boolean> DATA_LUMOS;

    public static void init() {
        DATA_CORE = register("wand_core", builder -> builder.codec(Codecs.NONNEGATIVE_INT).packetCodec(PacketCodecs.VAR_INT));
        DATA_WOOD =
                register("wand_wood", builder -> builder.codec(Codecs.NONNEGATIVE_INT).packetCodec(PacketCodecs.VAR_INT));
        DATA_FLEX =
                register("wand_flex", builder -> builder.codec(Codecs.NONNEGATIVE_INT).packetCodec(PacketCodecs.VAR_INT));
        DATA_LENGTH =
                register("wand_length", builder -> builder.codec(Codecs.NONNEGATIVE_INT).packetCodec(PacketCodecs.VAR_INT));
        DATA_MAGIC_NUM =
                register("wand_magic_num", builder -> builder.codec(Codecs.POSITIVE_FLOAT).packetCodec(PacketCodecs.FLOAT));
        DATA_SPELL =
                register("wand_spell", builder -> builder.codec(Codecs.ESCAPED_STRING).packetCodec(PacketCodecs.STRING));

        DATA_LUMOS =
                register("wand_lumos", builder -> builder.codec(Codec.BOOL).packetCodec(PacketCodecs.BOOL));

    }
}
