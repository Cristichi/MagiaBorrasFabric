package es.cristichi.mod.magiaborras.floo.fireplace;

import es.cristichi.mod.magiaborras.MagiaBorras;
import es.cristichi.mod.magiaborras.floo.FlooPowderItem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class FlooFireplaceBlock extends Block implements BlockEntityProvider {
    public FlooFireplaceBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.isClient()){
            return ActionResult.SUCCESS;
        }
        return super.onUse(state, world, pos, player, hit);
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (stack.getItem() instanceof FlooPowderItem){
            if (world.isClient()){
                BlockEntity be = world.getBlockEntity(pos);
                if (be instanceof FlooFireplaceBlockE fireplace){
                    player.sendMessage(Text.of(fireplace.getName()));
                    player.playSound(SoundEvents.BLOCK_CAMPFIRE_CRACKLE);
                }
                return ItemActionResult.SUCCESS;
            } else {
            }
        }
        return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return MagiaBorras.FLOO_FIREPLACE_BLOCK_ENTITY_TYPE.instantiate(pos, state);
    }

}
