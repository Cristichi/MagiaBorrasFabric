package es.cristichi.mod.magiaborras.spells;

import es.cristichi.mod.magiaborras.MagiaBorras;
import es.cristichi.mod.magiaborras.items.wand.prop.WandProperties;
import es.cristichi.mod.magiaborras.spells.prop.SpellCastType;
import es.cristichi.mod.magiaborras.spells.prop.SpellParticles;
import es.cristichi.mod.magiaborras.spells.prop.SpellParticlesBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.List;

public class Incendio extends Spell {
    public Incendio() {
        super("incendio", Text.translatable("magiaborras.spell.incendio"), List.of(SpellCastType.USE),
                Spell.ANY_ENTITY, Spell.ANY_BLOCK,
                new SpellParticlesBuilder().setType(SpellParticles.SpellParticleType.RAY).setColorStart(new Vector3f(0.9f, 0.5f, 0)).build(),
                140);
    }

    @Override
    public @NotNull Result resolveEffect(ItemStack wand, WandProperties properties, ServerPlayerEntity magicUser, ServerWorld world, HitResult hit) {
        if (hit.getType().equals(HitResult.Type.MISS)){
            return new Result(ActionResult.FAIL, baseCooldown/5, List.of(MagiaBorras.INCENDIO_SOUNDEVENT));
        }
        if (!world.isClient()){
            float power = properties.getPower(magicUser);
            Vec3d center = hit.getPos();
            BlockPos centerBlock = new BlockPos((int) center.x, (int) center.y, (int) center.z);
            int radius = 1+(int)(power * 5);

            if (hit instanceof EntityHitResult entityHitResult) {
                Entity ent = entityHitResult.getEntity();
                ent.setOnFireForTicks(Math.max(20, (int)(power * 70)));
            }
            BlockState fire = Blocks.FIRE.getDefaultState();
            double maxDistance = radius*2;
            for (int i = centerBlock.getX()-radius; i < centerBlock.getX()+radius; i++) {
                for (int j = centerBlock.getY()-radius; j < centerBlock.getY()+radius; j++) {
                    for (int k = centerBlock.getZ()-radius; k < centerBlock.getZ()+radius; k++) {

                        BlockPos pos = new BlockPos(i, j, k);
                        int distance = centerBlock.getManhattanDistance(pos);
                        double chance = Math.abs(distance/maxDistance -1);
                        double rng = Math.random();
                        if (chance > rng){
                            BlockState state = world.getBlockState(pos);
                            if (state.isAir()){
                                world.setBlockState(pos, fire);
                            }
                        }
                    }
                }
            }
        }

        return new Result(ActionResult.SUCCESS, baseCooldown, List.of(MagiaBorras.INCENDIO_SOUNDEVENT));
    }
}
