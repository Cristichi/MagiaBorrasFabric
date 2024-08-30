package es.cristichi.mod.magiaborras.floo.fireplace;

import es.cristichi.mod.magiaborras.MagiaBorras;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class FlooFireplaceBlockE extends BlockEntity implements NamedScreenHandlerFactory {
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

    @Override
    public Text getDisplayName() {
        return Text.of(name);
    }

    // TODO waiting on tutorial, since the one in the docs doesn't work for me
    //  and I can't figure out how to make it do what I want
    //  https://fabricmc.net/wiki/tutorial:screenhandler
    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return null;
    }
}
