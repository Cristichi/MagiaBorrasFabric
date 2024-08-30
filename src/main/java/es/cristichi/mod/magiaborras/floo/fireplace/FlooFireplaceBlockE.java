package es.cristichi.mod.magiaborras.floo.fireplace;

import es.cristichi.mod.magiaborras.MagiaBorras;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;

public class FlooFireplaceBlockE extends BlockEntity {
    private String name;

    public FlooFireplaceBlockE(BlockPos pos, BlockState state) {
        super(MagiaBorras.FLOO_FIREPLACE_BLOCK_ENTITY_TYPE, pos, state);
        name = "New Floo Fireplace";
    }

    public void setName(String name) {
        this.name = name;
        markDirty();
    }

    public String getName() {
        return name;
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        nbt.putString("floo_fire_name", name);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        if (nbt.contains("floo_fire_name", NbtElement.INT_TYPE)){
            name = nbt.getString("floo_fire_name");
        } else {
            name = "New Floo Fireplace";
        }
    }
}
