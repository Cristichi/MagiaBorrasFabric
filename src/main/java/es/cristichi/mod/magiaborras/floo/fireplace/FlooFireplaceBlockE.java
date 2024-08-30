package es.cristichi.mod.magiaborras.floo.fireplace;

import es.cristichi.mod.magiaborras.MagiaBorras;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class FlooFireplaceBlockE extends BlockEntity {
    private String name;

    public FlooFireplaceBlockE(BlockPos pos, BlockState state) {
        super(MagiaBorras.FLOO_FIREPLACE_BLOCK_ENTITY_TYPE, pos, state);
        name = "";
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
        if (nbt.contains("floo_fire_name", NbtElement.STRING_TYPE)){
            name = nbt.getString("floo_fire_name");
        } else {
            name = "La puta madre no funciona bien la puta";
        }
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }
}
