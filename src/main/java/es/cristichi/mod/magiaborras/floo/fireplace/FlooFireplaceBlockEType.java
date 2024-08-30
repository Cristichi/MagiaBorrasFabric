package es.cristichi.mod.magiaborras.floo.fireplace;

import com.mojang.datafixers.types.Type;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;

import java.util.Set;

public class FlooFireplaceBlockEType extends BlockEntityType<FlooFireplaceBlockE> {
    public FlooFireplaceBlockEType(BlockEntityFactory<? extends FlooFireplaceBlockE> factory, Set<Block> blocks, Type<?> type) {
        super(factory, blocks, type);
    }
}
