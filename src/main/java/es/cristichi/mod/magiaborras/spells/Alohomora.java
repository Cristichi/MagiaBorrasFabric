package es.cristichi.mod.magiaborras.spells;

import es.cristichi.mod.magiaborras.MagiaBorras;
import es.cristichi.mod.magiaborras.items.wand.prop.WandProperties;
import es.cristichi.mod.magiaborras.spells.prop.SpellCastType;
import net.minecraft.block.*;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import java.util.List;

public class Alohomora extends Spell {
    public Alohomora() {
        super("alohomora", Text.translatable("magiaborras.spell.alohomora"), List.of(SpellCastType.USE),
                Spell.NO_ENTITY, (blockState -> blockState.getBlock() instanceof DoorBlock || blockState.getBlock() instanceof TrapdoorBlock),
                null, 20);
    }

    @Override
    public Result cast(ItemStack wand, WandProperties properties, ServerPlayerEntity magicUser, World world, HitResult hit) {
        if (hit.getType() == HitResult.Type.BLOCK) {
            BlockHitResult hitLego = (BlockHitResult) hit;
            BlockState blockState = world.getBlockState(hitLego.getBlockPos());
            if (blockState.getBlock() instanceof DoorBlock door) {
                if (!world.isClient()) {
                    door.setOpen(magicUser, world, blockState, hitLego.getBlockPos(), !door.isOpen(blockState));
                }
                return new Result(ActionResult.SUCCESS, baseCooldown, List.of(MagiaBorras.ALOHOMORA_SOUNDEVENT));
            } else if (blockState.getBlock() instanceof TrapdoorBlock) {
                if (!world.isClient()) {
                    // Code copied from trapdoor.flip because it's private for some fucking reason.
                    BlockState newState = blockState.cycle(TrapdoorBlock.OPEN);
                    world.setBlockState(hitLego.getBlockPos(), newState, Block.NOTIFY_LISTENERS);
                    if (blockState.get(TrapdoorBlock.WATERLOGGED)) {
                        world.scheduleFluidTick(hitLego.getBlockPos(), Fluids.WATER, Fluids.WATER.getTickRate(world));
                    }

                    world.playSound(magicUser, hitLego.getBlockPos(),
                            blockState.get(TrapdoorBlock.OPEN) ? BlockSetType.IRON.trapdoorOpen() : BlockSetType.IRON.trapdoorClose(),
                            SoundCategory.BLOCKS, 1.0F, world.getRandom().nextFloat() * 0.1F + 0.9F
                    );
                    world.emitGameEvent(magicUser,
                            blockState.get(TrapdoorBlock.OPEN) ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, hitLego.getBlockPos());
                }
                return new Result(ActionResult.SUCCESS, baseCooldown, List.of(MagiaBorras.ALOHOMORA_SOUNDEVENT));
            }
        }
        return new Result(ActionResult.FAIL, 0, null);
    }
}
