package es.cristichi.mod.magiaborras.floo;

import es.cristichi.mod.magiaborras.MagiaBorras;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * There is a different Floo Network for each World, you cannot travel between dimensions. I feel like that
 * makes sense in some way for both lore and balance.
 */
public class FlooNetwork extends PersistentState {
    private static final Type<FlooNetwork> type = new Type<>(FlooNetwork::new, FlooNetwork::createFromNbt, null);

    public HashMap<BlockPos, String> fireplaces = new HashMap<>(5);

    public void registerOrEdit(BlockPos pos, String name){
        fireplaces.put(pos, name);
    }

    public void unregister(BlockPos pos){
        fireplaces.remove(pos);
    }

    public boolean isRegistered(BlockPos pos){
        return fireplaces.containsKey(pos);
    }


    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        for (Map.Entry<BlockPos, String> entry : fireplaces.entrySet()) {
            BlockPos key = entry.getKey();
            String value = entry.getValue();
            nbt.putString(key.getX() + ";" + key.getY() + ";" + key.getZ(), value);
        }
        return nbt;
    }

    public static FlooNetwork getNetworkOfWorld(ServerWorld world) {
        PersistentStateManager persistentStateManager = world.getPersistentStateManager();

        FlooNetwork state = persistentStateManager.getOrCreate(type, MagiaBorras.FLOO_NETWORK_ID.getPath());
        state.markDirty();

        return state;
    }

    public static @NotNull FlooNetwork createFromNbt(@NotNull NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        FlooNetwork flooNet = new FlooNetwork();
        flooNet.fireplaces = new HashMap<>(tag.getSize());
        for (String key : tag.getKeys()) {
            String[] split = key.split(";");
            try {
                flooNet.fireplaces.put(
                        new BlockPos(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2])),
                        tag.getString(key));
            } catch (Exception e){
                throw new IllegalArgumentException("The data \""+ key +"\" cannot be parsed to a BlockPos object.", e);
            }
        }
        return flooNet;
    }

}
