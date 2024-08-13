package es.cristichi.mod.magiaborras.util;

import es.cristichi.mod.magiaborras.MagiaBorras;
import es.cristichi.mod.magiaborras.spells.Spell;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDataPS extends PersistentState {
    public HashMap<UUID, PlayerMagicData> values = new HashMap<>(5);

    public PlayerMagicData getOrGenerateData(PlayerEntity player) {
        PlayerMagicData savedPlayerData = values.get(player.getUuid());
        if (savedPlayerData == null) {
            savedPlayerData = new PlayerMagicData(MagiaBorras.RNG.nextFloat(), new String[0]);
            values.put(player.getUuid(), savedPlayerData);
            this.markDirty();
        }
        return savedPlayerData;
    }

    public void setData(PlayerEntity player, PlayerMagicData data) {
        values.put(player.getUuid(), data);
        this.markDirty();
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        for (Map.Entry<UUID, PlayerMagicData> entry : values.entrySet()) {
            UUID key = entry.getKey();
            PlayerMagicData value = entry.getValue();
            nbt.put(key.toString(), value.toNbt());
        }
        return nbt;
    }

    public static PlayerDataPS createFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        PlayerDataPS mn = new PlayerDataPS();
        mn.values = new HashMap<>(tag.getSize());
        for (String key : tag.getKeys()) {
            PlayerMagicData data = PlayerMagicData.fromNbt(tag.get(key));
            mn.values.put(UUID.fromString(key), data);
        }
        return mn;
    }

    private static final Type<PlayerDataPS> type = new Type<>(
            PlayerDataPS::new, // What should be called if one is to be created
            PlayerDataPS::createFromNbt, // What should be called if we have the NBT Data of one
            DataFixTypes.PLAYER // No idea but it let me choose PLAYER, while the tutorial said "null"
    );

    public static PlayerDataPS getServerState(@Nullable MinecraftServer server) {
        if (server != null) {
            // (Note: arbitrary choice to use 'World.OVERWORLD' instead of 'World.END' or 'World.NETHER'.  Any work)
            ServerWorld world = server.getWorld(World.OVERWORLD);
            if (world != null) {
                PersistentStateManager persistentStateManager = world.getPersistentStateManager();

                PlayerDataPS state = persistentStateManager.getOrCreate(type, MagiaBorras.MOD_ID);

                state.markDirty();

                return state;
            }
        }
        return null;
    }

    public static class PlayerMagicData{
        static String MAGIC_NUMBER = "magicNumber", UNLOCKED_SPELLS= "unlockedSpells", UNLOCKED_SPELL_DIVIDER = " ";
        private final Float magicNumber;
        private String[] unlockedSpells;

        private PlayerMagicData(Float magicNumber, String[] unlockedSpells){
            this.magicNumber = magicNumber;
            this.unlockedSpells = unlockedSpells;
        }

        public static PlayerMagicData fromNbt(NbtElement nbt){
            if (nbt instanceof NbtCompound nbtCompound){
                return new PlayerMagicData(nbtCompound.getFloat(MAGIC_NUMBER), nbtCompound.getString(UNLOCKED_SPELLS).split(UNLOCKED_SPELL_DIVIDER));
                //return new PlayerMagicData(nbtCompound.getFloat(MAGIC_NUMBER), new String[0]);
            }
            throw new IllegalArgumentException("Nbt data of the PlayerDataPS state is not NbtCompound.");
        }

        public NbtCompound toNbt() {
            NbtCompound ret = new NbtCompound();
            ret.putFloat(MAGIC_NUMBER, magicNumber);
            ret.putString(UNLOCKED_SPELLS, String.join(UNLOCKED_SPELL_DIVIDER, unlockedSpells));
            return ret;
        }

        public Float getMagicNumber() {
            return magicNumber;
        }

        public String[] getUnlockedSpells() {
            return unlockedSpells;
        }

        public boolean addSpell(Spell spell){
            if (containsSpell(spell.getId())){
                return false;
            }
            String[] newArray = new String[unlockedSpells.length+1];
            int i;
            for (i = 0; i < unlockedSpells.length; i++) {
                newArray[i] = unlockedSpells[i];
            }
            newArray[i] = spell.getId();
            unlockedSpells = newArray;
            return true;
        }

        public boolean containsSpell(Spell spell){
            return containsSpell(spell.getId());
        }

        public boolean containsSpell(String spellId){
            for(String str : unlockedSpells){
                if (str.equals(spellId)){
                    return true;
                }
            }
            return false;
        }
    }
}
