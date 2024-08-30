package es.cristichi.mod.magiaborras.floo;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.PersistentState;

import java.util.HashMap;

public class FlooNetwork extends PersistentState {
    public HashMap<BlockPos, String> fireplaces = new HashMap<>(5);

    public void register(BlockPos pos, String name){

    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        return null;
    }
}
