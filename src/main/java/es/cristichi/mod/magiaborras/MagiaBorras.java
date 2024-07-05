package es.cristichi.mod.magiaborras;

import es.cristichi.mod.magiaborras.commands.ChangeSpellCommand;
import es.cristichi.mod.magiaborras.items.MoonStone;
import es.cristichi.mod.magiaborras.items.SpellBook;
import es.cristichi.mod.magiaborras.items.wand.WandItem;
import es.cristichi.mod.magiaborras.items.wand.prop.WandProperties;
import es.cristichi.mod.magiaborras.spells.*;
import es.cristichi.mod.magiaborras.util.PlayerDataPS;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.PlacedFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Optional;

// TODO List:
//  1. Moar Spells
//  X. Moonstone from Hogwarts Legacy
//  3. Progression. Craftable books that unlock Spells.
//  4. School Uniforms (Armor) (Dyable)
//  5. Magic HP Potions
//  6. Floo Powder
//  7. Special Enchantments for Wands, School Uniforms/Armor, etc.
//     Perhaps each Spell could be in an Enchantment for use with any tool, or Sticks!
//  8. Banner patterns for each house.
public class MagiaBorras implements ModInitializer {
    public static final String MOD_ID = "magiaborras";
    public static final Random RNG = Random.create();
    public static final Logger LOGGER = LoggerFactory.getLogger("Magia Borras");

    // Items
    public static final WandItem WAND_ITEM = new WandItem(new WandItem.Settings());
    public static final WandItem WAND_2_ITEM = new WandItem(new WandItem.Settings());
    public static final MoonStone MOONSTONE_ITEM = new MoonStone(new Item.Settings());

    // Blocks
    public static final Block MOONSTONE_BLOCK = new Block(Block.Settings.create().strength(3.0f));
    public static final Block MOONSTONE_ORE = new Block(Block.Settings.create().strength(4.0f));
    public static final Block MOONSTONE_ORE_DEEP = new Block(Block.Settings.create().strength(4.1f));

    // World Generation
    public static final RegistryKey<PlacedFeature> MOONSTONE_ORE_PLACED_KEY =
            RegistryKey.of(RegistryKeys.PLACED_FEATURE, Identifier.of(MOD_ID, "moonstone_ore"));

    // Magia Borras Creative Tab
    public static final ItemGroup MAGIC_GROUP = FabricItemGroup.builder()
            .icon(WAND_ITEM::getDefaultStack)
            .displayName(Text.translatable("itemGroup.magic_items_tab"))
            .entries((context, entries) -> Registries.ITEM.getIds().stream()
                    .filter(key -> key.getNamespace().equals(MOD_ID))
                    .map(Registries.ITEM::getOrEmpty)
                    .map(Optional::orElseThrow)
                    .sorted(Comparator.comparing(o -> o.getName().getString()))
                    .forEachOrdered(entries::add)
            )
            .build();

    // Magic Numbers
    public static PlayerDataPS playerDataPS = new PlayerDataPS();
    public static final Identifier PACK_MAGICNUM_SAVESTATE_ID = Identifier.of(MOD_ID, "magic_number");

    // Spells
    public static final HashMap<String, Spell> SPELLS = new HashMap<>(10);

    private static void regSpell(Class<? extends Spell> spellClass) {
        try {
            Constructor<? extends Spell> ctor = spellClass.getConstructor();
            Spell newSpell = ctor.newInstance();
            SPELLS.put(newSpell.getId(), newSpell);

            if (!newSpell.getId().equals("")){
                SpellBook spellbookSpell = new SpellBook(new Item.Settings(), newSpell);
                Registry.register(Registries.ITEM, Identifier.of(MOD_ID, "spellbook_" + newSpell.getId()), spellbookSpell);
            }
        } catch (NoSuchMethodException | InstantiationException |
                 IllegalAccessException | InvocationTargetException error) {
            LOGGER.error("Error when registering Spell \"{}.java\" due to an error by Cristichi (the author).",
                    spellClass.getName(), error);
        }
    }

    // Armor I'll work on this once the tutorials are out, way too hard to figure this out after it changed
//    public static ArmorMaterial CUSTOM_ARMOR_MATERIAL;
//    public static Item CUSTOM_MATERIAL;
//    // If you made a new material, this is where you would note it.
//    public static Item CUSTOM_MATERIAL_HELMET;
//    public static Item CUSTOM_MATERIAL_CHESTPLATE;
//    public static Item CUSTOM_MATERIAL_LEGGINGS;
//    public static Item CUSTOM_MATERIAL_BOOTS;

    // Sounds
    public static final Identifier SOUND_AVADA_ID = Identifier.of(MOD_ID, "avada");
    public static SoundEvent AVADA_CAST = SoundEvent.of(SOUND_AVADA_ID);
    public static final Identifier SOUND_ALOHOMORA_ID = Identifier.of(MOD_ID, "alohomora");
    public static SoundEvent ALOHOMORA_CAST = SoundEvent.of(SOUND_ALOHOMORA_ID);
    public static final Identifier SOUND_ACCIO_ID = Identifier.of(MOD_ID, "accio");
    public static SoundEvent ACCIO_CAST = SoundEvent.of(SOUND_ACCIO_ID);
    public static final Identifier SOUND_DIFFINDO_ID = Identifier.of(MOD_ID, "diffindo");
    public static SoundEvent DIFFINDO_CAST = SoundEvent.of(SOUND_DIFFINDO_ID);
    public static final Identifier SOUND_WING_LEV_ID = Identifier.of(MOD_ID, "wingardium_leviosa");
    public static SoundEvent WING_LEV_CAST = SoundEvent.of(SOUND_WING_LEV_ID);
    public static final Identifier SOUND_LUMOS_ID = Identifier.of(MOD_ID, "lumos");
    public static SoundEvent LUMOS_CAST = SoundEvent.of(SOUND_LUMOS_ID);
    public static final Identifier SOUND_ARRESTO_ID = Identifier.of(MOD_ID, "arresto");
    public static SoundEvent ARRESTO_CAST = SoundEvent.of(SOUND_ARRESTO_ID);

    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.
        LOGGER.info("Loading");

        // Items
        Registry.register(Registries.ITEM, Identifier.of(MOD_ID, "wand"), WAND_ITEM);
        Registry.register(Registries.ITEM, Identifier.of(MOD_ID, "wand_2"), WAND_2_ITEM);
        Registry.register(Registries.ITEM, Identifier.of(MOD_ID, "moonstone"), MOONSTONE_ITEM);

        // Items Creative Tab
        Registry.register(Registries.ITEM_GROUP, Identifier.of(MOD_ID, "magic_items_tab"), MAGIC_GROUP);

        // Wand needs special attention
        WandProperties.init();

        // Blocks
        Registry.register(Registries.BLOCK, Identifier.of(MOD_ID, "moonstone_block"), MOONSTONE_BLOCK);
        Registry.register(Registries.ITEM, Identifier.of(MOD_ID, "moonstone_block"),
                new BlockItem(MOONSTONE_BLOCK, new Item.Settings()));

        Registry.register(Registries.BLOCK, Identifier.of(MOD_ID, "moonstone_ore"), MOONSTONE_ORE);
        Registry.register(Registries.ITEM, Identifier.of(MOD_ID, "moonstone_ore"),
                new BlockItem(MOONSTONE_ORE, new Item.Settings()));
        Registry.register(Registries.BLOCK, Identifier.of(MOD_ID, "deepslate_moonstone_ore"), MOONSTONE_ORE_DEEP);
        Registry.register(Registries.ITEM, Identifier.of(MOD_ID, "deepslate_moonstone_ore"),
                new BlockItem(MOONSTONE_ORE_DEEP, new Item.Settings()));

        // World Generation
        BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(),
                GenerationStep.Feature.UNDERGROUND_ORES, MOONSTONE_ORE_PLACED_KEY);

        // Spells
        regSpell(DefaultSpell.class);
        regSpell(AvadaKedavra.class);
        regSpell(Alohomora.class);
        regSpell(Accio.class);
        regSpell(Diffindo.class);
        regSpell(WingardiumLeviosa.class);
        regSpell(Lumos.class);
        regSpell(ArrestoMomentum.class);

        // Magic numbers are also very special because they are saved in the main overworld
        ServerLifecycleEvents.SERVER_STARTED.register(
                PACK_MAGICNUM_SAVESTATE_ID, server -> playerDataPS = PlayerDataPS.getServerState(server)
        );

        // Commands
        CommandRegistrationCallback.EVENT.register(
                (dispatcher, registryAccess, environment) -> ChangeSpellCommand.register(dispatcher)
        );

        // Sounds
        Registry.register(Registries.SOUND_EVENT, SOUND_AVADA_ID, AVADA_CAST);
        Registry.register(Registries.SOUND_EVENT, SOUND_ALOHOMORA_ID, ALOHOMORA_CAST);
        Registry.register(Registries.SOUND_EVENT, SOUND_ACCIO_ID, ACCIO_CAST);
        Registry.register(Registries.SOUND_EVENT, SOUND_DIFFINDO_ID, DIFFINDO_CAST);
        Registry.register(Registries.SOUND_EVENT, SOUND_WING_LEV_ID, WING_LEV_CAST);
        Registry.register(Registries.SOUND_EVENT, SOUND_LUMOS_ID, LUMOS_CAST);
        Registry.register(Registries.SOUND_EVENT, SOUND_ARRESTO_ID, ARRESTO_CAST);

        // Armor
        // No idea. This changed a lot and is way too different than the tutorial.
        // I'll do School Uniforms when I find an updated tutorial or ask on Discord.
//        CUSTOM_ARMOR_MATERIAL = new ArmorMaterial(
//                new EasyMap<>( //Defense
//                        new EasyMap.EasyEntry<>(ArmorItem.Type.HELMET, 1),
//                        new EasyMap.EasyEntry<>(ArmorItem.Type.CHESTPLATE, 1),
//                        new EasyMap.EasyEntry<>(ArmorItem.Type.LEGGINGS, 1),
//                        new EasyMap.EasyEntry<>(ArmorItem.Type.BOOTS, 1)
//                ),
//                10, //Enchantability. 10 = same as Diamond
//                SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, // Equip Sound
//                null, // Repair Ingredient Supplier<Ingredient>
//                new EasyList<>(new ArmorMaterial.Layer(Identifier.of(MOD_ID, "uniform"), "uniform", true)), // Layers
//                1f, // Toughness
//                0f // Knockback Resistance
//        );
//        RegistryEntry.Reference<ArmorMaterial> regUniform =Registry.registerReference(Registries.ARMOR_MATERIAL,
//                Identifier.of(MOD_ID, "uniform"), CUSTOM_ARMOR_MATERIAL);
//        CUSTOM_MATERIAL_HELMET = new SchoolUniform(regUniform, ArmorItem.Type.HELMET, new Item.Settings());
//        CUSTOM_MATERIAL_CHESTPLATE = new SchoolUniform(regUniform, ArmorItem.Type.CHESTPLATE, new Item.Settings());
//        CUSTOM_MATERIAL_LEGGINGS = new SchoolUniform(regUniform, ArmorItem.Type.LEGGINGS, new Item.Settings());
//        CUSTOM_MATERIAL_BOOTS = new SchoolUniform(regUniform, ArmorItem.Type.BOOTS, new Item.Settings());

        LOGGER.info("Loaded, against all odds.");
    }
}