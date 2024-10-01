package es.cristichi.mod.magiaborras;

import es.cristichi.mod.magiaborras.commands.SpellSetCommand;
import es.cristichi.mod.magiaborras.floo.FlooNetwork;
import es.cristichi.mod.magiaborras.floo.fireplace.FlooFireplaceBlock;
import es.cristichi.mod.magiaborras.floo.fireplace.FlooFireplaceBlockE;
import es.cristichi.mod.magiaborras.floo.fireplace.packets.FlooFireRenamePayload;
import es.cristichi.mod.magiaborras.floo.fireplace.packets.FlooFireTPPayload;
import es.cristichi.mod.magiaborras.floo.fireplace.packets.FlooFiresMenuPayload;
import es.cristichi.mod.magiaborras.items.FlooPowderItem;
import es.cristichi.mod.magiaborras.items.MoonStone;
import es.cristichi.mod.magiaborras.items.SpellBook;
import es.cristichi.mod.magiaborras.items.wand.WandItem;
import es.cristichi.mod.magiaborras.items.wand.prop.WandProperties;
import es.cristichi.mod.magiaborras.perdata.PlayerDataPS;
import es.cristichi.mod.magiaborras.perdata.PlayerDataSyncPayload;
import es.cristichi.mod.magiaborras.spells.*;
import es.cristichi.mod.magiaborras.spells.net.SpellChangeInHandPayload;
import es.cristichi.mod.magiaborras.spells.net.SpellHitPayload;
import es.cristichi.mod.magiaborras.uniform.ModArmorMaterials;
import es.cristichi.mod.magiaborras.uniform.SchoolUniform;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.*;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.PlacedFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Optional;

// TODO List:
//  1. Moar Spells (+ their Spell Book)
//  X. Moonstone from Hogwarts Legacy
//  X. Progression. Craftable books that unlock Spells.
//  X. School Uniforms (Armor)
//  5. Houses Trim? (Instead of dyable armor)
//  6. Magic HP Potions
//  7. Floo Powder
//  8. Special Enchantments for Wands, School Uniforms/Armor, etc.
//     Perhaps each Spell could be in an Enchantment for use with any tool, or Sticks!
//  X. Banner patterns for each house.
//  10. Magic Brooms? (good to keep: https://discord.com/channels/507304429255393322/1281175260217217044/1281301709649350708)
public class MagiaBorras implements ModInitializer {
    public static final String MOD_ID = "magiaborras";
    public static final Random RNG = Random.create();
    public static final Logger LOGGER = LoggerFactory.getLogger("Magia Borras");

    // Items
    public static final WandItem WAND_ITEM = new WandItem(new WandItem.Settings());
    public static final WandItem WAND_2_ITEM = new WandItem(new WandItem.Settings());
    public static final WandItem WAND_SLYTHERIN_ITEM = new WandItem(new WandItem.Settings());
    public static final MoonStone MOONSTONE_ITEM = new MoonStone(new Item.Settings());
    public static final FlooPowderItem FLOO_POWDER_ITEM = new FlooPowderItem(new Item.Settings());

    // Armor Items
    public static final SchoolUniform SCHOOL_HELMET = new SchoolUniform(ModArmorMaterials.UNIFORM_MATERIAL, ArmorItem.Type.HELMET, new Item.Settings());
    public static final SchoolUniform SCHOOL_CHESTPLATE = new SchoolUniform(ModArmorMaterials.UNIFORM_MATERIAL, ArmorItem.Type.CHESTPLATE, new Item.Settings());
    public static final SchoolUniform SCHOOL_LEGGINGS = new SchoolUniform(ModArmorMaterials.UNIFORM_MATERIAL, ArmorItem.Type.LEGGINGS, new Item.Settings());
    public static final SchoolUniform SCHOOL_BOOTS = new SchoolUniform(ModArmorMaterials.UNIFORM_MATERIAL, ArmorItem.Type.BOOTS, new Item.Settings());

    // Blocks
    public static final Block MOONSTONE_BLOCK = new Block(Block.Settings.create().strength(3.0f));
    public static final Block MOONSTONE_ORE_BLOCK = new Block(Block.Settings.create().strength(4.0f));
    public static final Block MOONSTONE_ORE_DEEP_BLOCK = new Block(Block.Settings.create().strength(4.1f));
    public static final FlooFireplaceBlock FLOO_FIREPLACE_BLOCK = new FlooFireplaceBlock(Block.Settings.create().luminance(value -> 15));
    public static final BlockEntityType<FlooFireplaceBlockE> FLOO_FIREPLACE_BLOCK_ENTITY_TYPE = BlockEntityType.Builder
            .create(FlooFireplaceBlockE::new, FLOO_FIREPLACE_BLOCK)
            .build();

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

    // Persistent States
    public static PlayerDataPS playerDataPS = new PlayerDataPS();
    public static final Identifier PLAYER_DATA_PS_ID = Identifier.of(MOD_ID, "saved_player_data");

    public static final Identifier FLOO_NETWORK_ID = Identifier.of(MOD_ID, "floo_network");

    // Spells
    public static final HashMap<String, Spell> SPELLS = new HashMap<>(10);

    private static void initSpell(Class<? extends Spell> spellClass) {
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
            LOGGER.error("Error when registering Spell \"{}.java\" due to Cristichi (the author) forgetting to setup that Spell's Book.",
                    spellClass.getName(), error);
        }
    }

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
    public static final Identifier SOUND_BOMBARDA_ID = Identifier.of(MOD_ID, "bombarda");
    public static SoundEvent BOMBARDA_CAST = SoundEvent.of(SOUND_BOMBARDA_ID);
    public static final Identifier SOUND_DEPULSO_ID = Identifier.of(MOD_ID, "depulso");
    public static SoundEvent DEPULSO_CAST = SoundEvent.of(SOUND_DEPULSO_ID);
    public static final Identifier SOUND_INCENDIO_ID = Identifier.of(MOD_ID, "incendio");
    public static SoundEvent INCENDIO_CAST = SoundEvent.of(SOUND_INCENDIO_ID);
    public static final Identifier SOUND_EXPELLIARMUS_ID = Identifier.of(MOD_ID, "expelliarmus");
    public static SoundEvent EXPELLIARMUS_CAST = SoundEvent.of(SOUND_EXPELLIARMUS_ID);
    public static final Identifier SOUND_REVELIO_ID = Identifier.of(MOD_ID, "revelio");
    public static SoundEvent REVELIO_CAST = SoundEvent.of(SOUND_REVELIO_ID);

    // Networking
    public static final Identifier NET_PLAYER_DATA_SYNC_ID = Identifier.of(MOD_ID, "magia_player_data");

    public static final Identifier NET_CHANGE_SPELL_ID = Identifier.of(MOD_ID, "change_spell");
    public static final Identifier NET_SPELL_HIT_ID = Identifier.of(MOD_ID, "spell_hit");

    public static final Identifier NET_FLOO_RENAME_ID = Identifier.of(MOD_ID, "floo_rename");
    public static final Identifier NET_FLOO_MENU_ID = Identifier.of(MOD_ID, "floo_menu");
    public static final Identifier NET_FLOO_TP_ID = Identifier.of(MOD_ID, "floo_teleport");

    @Override
    public void onInitialize() {
        LOGGER.info("Loading");

        // Items
        Registry.register(Registries.ITEM, Identifier.of(MOD_ID, "wand"), WAND_ITEM);
        Registry.register(Registries.ITEM, Identifier.of(MOD_ID, "wand_2"), WAND_2_ITEM);
        Registry.register(Registries.ITEM, Identifier.of(MOD_ID, "wand_slytherin"), WAND_SLYTHERIN_ITEM);
        Registry.register(Registries.ITEM, Identifier.of(MOD_ID, "moonstone"), MOONSTONE_ITEM);
        Registry.register(Registries.ITEM, Identifier.of(MOD_ID, "floo_powder"), FLOO_POWDER_ITEM);

        // Armor Items
        ModArmorMaterials.initialize();
        Registry.register(Registries.ITEM, Identifier.of(MOD_ID, "school_helmet"), SCHOOL_HELMET);
        Registry.register(Registries.ITEM, Identifier.of(MOD_ID, "school_chestplate"), SCHOOL_CHESTPLATE);
        Registry.register(Registries.ITEM, Identifier.of(MOD_ID, "school_leggings"), SCHOOL_LEGGINGS);
        Registry.register(Registries.ITEM, Identifier.of(MOD_ID, "school_boots"), SCHOOL_BOOTS);

        // Blocks
        Registry.register(Registries.BLOCK, Identifier.of(MOD_ID, "moonstone_block"), MOONSTONE_BLOCK);
        Registry.register(Registries.ITEM, Identifier.of(MOD_ID, "moonstone_block"),
                new BlockItem(MOONSTONE_BLOCK, new Item.Settings()));

        Registry.register(Registries.BLOCK, Identifier.of(MOD_ID, "moonstone_ore"), MOONSTONE_ORE_BLOCK);
        Registry.register(Registries.ITEM, Identifier.of(MOD_ID, "moonstone_ore"),
                new BlockItem(MOONSTONE_ORE_BLOCK, new Item.Settings()));
        Registry.register(Registries.BLOCK, Identifier.of(MOD_ID, "deepslate_moonstone_ore"), MOONSTONE_ORE_DEEP_BLOCK);
        Registry.register(Registries.ITEM, Identifier.of(MOD_ID, "deepslate_moonstone_ore"),
                new BlockItem(MOONSTONE_ORE_DEEP_BLOCK, new Item.Settings()));

        Registry.register(Registries.BLOCK, Identifier.of(MOD_ID, "floo_fireplace"), FLOO_FIREPLACE_BLOCK);
        Registry.register(Registries.ITEM, Identifier.of(MOD_ID, "floo_fireplace"),
                new BlockItem(FLOO_FIREPLACE_BLOCK, new Item.Settings()));

        Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(MOD_ID, "floo_fireplace_blocke"), FLOO_FIREPLACE_BLOCK_ENTITY_TYPE);

        // Entities

        // Items Creative Tab
        Registry.register(Registries.ITEM_GROUP, Identifier.of(MOD_ID, "magic_items_tab"), MAGIC_GROUP);

        // Wand needs special attention
        WandProperties.init();

        // World Generation
        BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(),
                GenerationStep.Feature.UNDERGROUND_ORES, MOONSTONE_ORE_PLACED_KEY);

        // Spells init
        initSpell(DefaultSpell.class);
        initSpell(AvadaKedavra.class);
        initSpell(Alohomora.class);
        initSpell(Accio.class);
        initSpell(Diffindo.class);
        initSpell(WingardiumLeviosa.class);
//        initSpell(Lumos.class); I mean, since it doesn't work...
        initSpell(ArrestoMomentum.class);
        initSpell(Bombarda.class);
        initSpell(Depulso.class);
        initSpell(Incendio.class);
        initSpell(Expelliarmus.class);
        initSpell(Revelio.class);

        // Persistent States
        ServerLifecycleEvents.SERVER_STARTED.register(
                PLAYER_DATA_PS_ID, server -> playerDataPS = PlayerDataPS.getServerState(server)
        );

        // Commands
        CommandRegistrationCallback.EVENT.register(
                (dispatcher, registryAccess, environment) -> SpellSetCommand.register(dispatcher)
        );

        // Sounds
        Registry.register(Registries.SOUND_EVENT, SOUND_AVADA_ID, AVADA_CAST);
        Registry.register(Registries.SOUND_EVENT, SOUND_ALOHOMORA_ID, ALOHOMORA_CAST);
        Registry.register(Registries.SOUND_EVENT, SOUND_ACCIO_ID, ACCIO_CAST);
        Registry.register(Registries.SOUND_EVENT, SOUND_DIFFINDO_ID, DIFFINDO_CAST);
        Registry.register(Registries.SOUND_EVENT, SOUND_WING_LEV_ID, WING_LEV_CAST);
        Registry.register(Registries.SOUND_EVENT, SOUND_LUMOS_ID, LUMOS_CAST);
        Registry.register(Registries.SOUND_EVENT, SOUND_ARRESTO_ID, ARRESTO_CAST);
        Registry.register(Registries.SOUND_EVENT, SOUND_BOMBARDA_ID, BOMBARDA_CAST);
        Registry.register(Registries.SOUND_EVENT, SOUND_DEPULSO_ID, DEPULSO_CAST);
        Registry.register(Registries.SOUND_EVENT, SOUND_INCENDIO_ID, INCENDIO_CAST);
        Registry.register(Registries.SOUND_EVENT, SOUND_EXPELLIARMUS_ID, EXPELLIARMUS_CAST);
        Registry.register(Registries.SOUND_EVENT, SOUND_REVELIO_ID, REVELIO_CAST);

        // Player Data Sync Packet S -> C
        PayloadTypeRegistry.playS2C().register(PlayerDataSyncPayload.ID, PlayerDataSyncPayload.CODEC);
        // Player onJoin update unlocked Spells to client
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            PlayerDataPS.PlayerMagicData data = playerDataPS.getOrGenerateData(handler.player);
            ServerPlayNetworking.send(handler.player, new PlayerDataSyncPayload(data.getUnlockedSpells()));
        });

        // Spell Hit Packet S -> C
        PayloadTypeRegistry.playS2C().register(SpellHitPayload.ID, SpellHitPayload.CODEC);

        //  Spell Change Packet C -> S
        PayloadTypeRegistry.playC2S().register(SpellChangeInHandPayload.ID, SpellChangeInHandPayload.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(SpellChangeInHandPayload.ID, (payload, context) -> {
            Spell spell = payload.spell();
            ServerPlayerEntity magicUser = context.player();

            ItemStack hand = magicUser.getStackInHand(Hand.MAIN_HAND);
            WandProperties prop = WandProperties.check(hand);
            if (prop != null) {
                magicUser.sendMessage(Text.translatable("magiaborras.spell.changed_spell", spell.getName()));
                prop.spell = spell;
                prop.apply(hand);
            }
        });

        // Floo Fireplace rename both ways
        PayloadTypeRegistry.playS2C().register(FlooFireRenamePayload.ID, FlooFireRenamePayload.CODEC);

        PayloadTypeRegistry.playC2S().register(FlooFireRenamePayload.ID, FlooFireRenamePayload.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(FlooFireRenamePayload.ID, (payload, context) -> {
            BlockEntity be = context.player().getWorld().getBlockEntity(payload.block());

            if (be instanceof FlooFireplaceBlockE flooFireplaceBlockE){
                flooFireplaceBlockE.setName(payload.name());
                BlockState bs = context.player().getWorld().getBlockState(payload.block());
                context.player().getWorld().updateListeners(payload.block(), bs, bs, 0);

                FlooNetwork flooNetwork = FlooNetwork.getNetworkOfWorld(context.player().getServerWorld());
                if (payload.registered()) {
                    flooNetwork.registerOrEdit(payload.block(), payload.name());
                } else {
                    flooNetwork.unregister(payload.block());
                }
            }
        });

        // Floo Fireplace menu
        PayloadTypeRegistry.playS2C().register(FlooFiresMenuPayload.ID, FlooFiresMenuPayload.CODEC);

        // Floo Fireplace TP
        PayloadTypeRegistry.playC2S().register(FlooFireTPPayload.ID, FlooFireTPPayload.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(FlooFireTPPayload.ID, (payload, context) -> {

            PlayerInventory inv = context.player().getInventory();
            ItemStack handStack = inv.getMainHandStack();
            if (handStack.getItem() instanceof FlooPowderItem){
                BlockPos pPos = context.player().getBlockPos();
                context.player().getWorld().getBlockState(payload.objective()); // Need to load the chunk first

                if (context.player().teleport(payload.objective().getX()+0.5, payload.objective().getY()+1, payload.objective().getZ()+0.5, false)){
                    if (!context.player().isCreative()){
                        handStack.decrement(1);
                    }

                    Collection<ServerPlayerEntity> playersOrigin = PlayerLookup.tracking(context.player().getServerWorld(), context.player().getBlockPos());
                    ParticleS2CPacket packetOrigin = new ParticleS2CPacket(FlooFireplaceBlock.tpParticles, false,
                            pPos.getX()+0.5, pPos.getY()+1.5, pPos.getZ()+0.5,
                            1f, 1.5f, 1f, 0f, 1000);
                    context.player().networkHandler.sendPacket(packetOrigin);
                    for (ServerPlayerEntity player : playersOrigin){
                        player.networkHandler.sendPacket(packetOrigin);
                    }

                    Collection<ServerPlayerEntity> playersDest = PlayerLookup.tracking(context.player().getServerWorld(), payload.objective());
                    ParticleS2CPacket packetDest = new ParticleS2CPacket(FlooFireplaceBlock.tpParticles, false,
                            payload.objective().getX()+0.5, payload.objective().getY()+1.5, payload.objective().getZ()+0.5,
                            1f, 1.5f, 1f, 0f, 1000);
                    context.player().networkHandler.sendPacket(packetDest);
                    for (ServerPlayerEntity player : playersDest){
                        player.networkHandler.sendPacket(packetDest);
                    }
                } else {
                    context.player().sendMessage(Text.translatable("magiaborras.screen.floonet.cant_tp"));
                }


            }

        });

        LOGGER.info("Loaded, against all odds.");
    }
}