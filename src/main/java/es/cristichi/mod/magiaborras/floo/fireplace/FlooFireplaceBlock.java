package es.cristichi.mod.magiaborras.floo.fireplace;

import com.mojang.serialization.MapCodec;
import es.cristichi.mod.magiaborras.MagiaBorras;
import es.cristichi.mod.magiaborras.floo.FlooNetwork;
import es.cristichi.mod.magiaborras.floo.fireplace.packets.FlooFireRenamePayload;
import es.cristichi.mod.magiaborras.floo.fireplace.packets.FlooFiresMenuPayload;
import es.cristichi.mod.magiaborras.items.FlooPowderItem;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DustColorTransitionParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.HashMap;

public class FlooFireplaceBlock extends BlockWithEntity implements BlockEntityProvider {
    public static DustColorTransitionParticleEffect tpParticles = new DustColorTransitionParticleEffect(new Vector3f(0, 255, 0),
            new Vector3f(100, 255, 100), 1f);
    public FlooFireplaceBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof FlooFireplaceBlockE fireplace){
            FlooNetwork flooNetwork = FlooNetwork.getNetworkOfWorld((ServerWorld) world);
            flooNetwork.unregister(fireplace.getPos());
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.isClient()){
            return ActionResult.SUCCESS;
        } else {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof FlooFireplaceBlockE fireplace){
                FlooNetwork flooNetwork = FlooNetwork.getNetworkOfWorld((ServerWorld) world);
                ServerPlayNetworking.send((ServerPlayerEntity) player,
                        new FlooFireRenamePayload(pos, fireplace.getName(), flooNetwork.isRegistered(pos)));
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.FAIL;
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (stack.getItem() instanceof FlooPowderItem && !world.isClient()){
            if (pos.equals(player.getBlockPos().down())){
                BlockEntity be = world.getBlockEntity(pos);
                if (be instanceof FlooFireplaceBlockE fireplace){
                    FlooNetwork flooNetwork = FlooNetwork.getNetworkOfWorld((ServerWorld) world);
                    HashMap<BlockPos, String> fireplaces = new HashMap<>(flooNetwork.fireplaces);
                    fireplaces.remove(pos);
                    ServerPlayNetworking.send((ServerPlayerEntity) player, new FlooFiresMenuPayload(fireplaces));
                    return ItemActionResult.SUCCESS;
                }
            } else {
                player.sendMessage(Text.translatable("block.magiaborras.floo_fireplace.need_ontop"));
                return ItemActionResult.FAIL;
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
