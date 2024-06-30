package es.cristichi.mod.magiaborras.items.wand.prop;

import es.cristichi.mod.magiaborras.MagiaBorras;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class MagicNumbers extends PersistentState {
    public HashMap<UUID, Float> values = new HashMap<>(5);

    public float getOrGenerateValue(PlayerEntity player) {
        Float numeroMagicoPlayer = values.get(player.getUuid());
        if (numeroMagicoPlayer == null) {
            numeroMagicoPlayer = MagiaBorras.RNG.nextFloat();
            values.put(player.getUuid(), numeroMagicoPlayer);
            this.markDirty();
        }
        return numeroMagicoPlayer;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        for(Map.Entry<UUID, Float> entry : values.entrySet()) {
            UUID key = entry.getKey();
            Float value = entry.getValue();
            nbt.putFloat(key.toString(), value);
        }
        return nbt;
    }

    public static MagicNumbers createFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        MagicNumbers mn = new MagicNumbers();
        mn.values = new HashMap<>(tag.getSize());
        for (String key : tag.getKeys()){
            mn.values.put(UUID.fromString(key), Float.parseFloat(Objects.requireNonNull(tag.get(key)).asString()));
        }
        return mn;
    }

    private static final Type<MagicNumbers> type = new Type<>(
            MagicNumbers::new, // What should be called if one is to be created
            MagicNumbers::createFromNbt, // What should be called if we have the NBT Data of one
            DataFixTypes.PLAYER // NPI but it let me choose PLAYER
    );

    public static MagicNumbers getServerState(@Nullable MinecraftServer server) {
        if (server != null){
            // (Note: arbitrary choice to use 'World.OVERWORLD' instead of 'World.END' or 'World.NETHER'.  Any work)
            ServerWorld world = server.getWorld(World.OVERWORLD);
            if (world != null){
                PersistentStateManager persistentStateManager = world.getPersistentStateManager();

                MagicNumbers state = persistentStateManager.getOrCreate(type, MagiaBorras.MOD_ID);

                state.markDirty();

                return state;
            }
        }
        return null;
    }
}
