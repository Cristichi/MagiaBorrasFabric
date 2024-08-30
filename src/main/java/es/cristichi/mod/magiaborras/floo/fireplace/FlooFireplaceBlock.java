package es.cristichi.mod.magiaborras.floo.fireplace;

import com.mojang.serialization.MapCodec;
import es.cristichi.mod.magiaborras.MagiaBorras;
import es.cristichi.mod.magiaborras.floo.FlooPowderItem;
import es.cristichi.mod.magiaborras.floo.fireplace.net.FlooFireRenamePayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class FlooFireplaceBlock extends BlockWithEntity implements BlockEntityProvider {
    public FlooFireplaceBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.isClient()){
            return ActionResult.SUCCESS;
        } else {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof FlooFireplaceBlockE fireplace){
                ServerPlayNetworking.send((ServerPlayerEntity) player, new FlooFireRenamePayload(pos, fireplace.getName(), true));
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (stack.getItem() instanceof FlooPowderItem){
            BlockEntity be = world.getBlockEntity(pos);
            if (world.isClient()){
                if (be instanceof FlooFireplaceBlockE){
                    player.playSound(SoundEvents.BLOCK_CAMPFIRE_CRACKLE);
                    return ItemActionResult.SUCCESS;
                }
            } else {
                if (be instanceof FlooFireplaceBlockE fireplace){
                    player.sendMessage(Text.of(fireplace.getName()));
                    // TODO send package of FlooFiresMenuPayload
                    return ItemActionResult.SUCCESS;
                }
            }
        }
        return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected MapCodec<? extends FlooFireplaceBlock> getCodec() {
        return createCodec(FlooFireplaceBlock::new);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return MagiaBorras.FLOO_FIREPLACE_BLOCK_ENTITY_TYPE.instantiate(pos, state);
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }
}
