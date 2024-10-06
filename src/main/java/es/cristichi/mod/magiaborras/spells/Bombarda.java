package es.cristichi.mod.magiaborras.spells;

import es.cristichi.mod.magiaborras.MagiaBorras;
import es.cristichi.mod.magiaborras.items.wand.prop.WandProperties;
import es.cristichi.mod.magiaborras.spells.prop.SpellCastType;
import es.cristichi.mod.magiaborras.spells.prop.SpellParticles;
import es.cristichi.mod.magiaborras.spells.prop.SpellParticlesBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import net.minecraft.world.explosion.AdvancedExplosionBehavior;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Bombarda extends Spell {
    public Bombarda() {
        super("bombarda", Text.translatable("magiaborras.spell.bombarda"), List.of(SpellCastType.USE),
                Spell.ANY_ENTITY, Spell.ANY_BLOCK,
                new SpellParticlesBuilder().setType(SpellParticles.SpellParticleType.RAY).setColorStart(new Vector3f(0.9f, 0.9f, 0.9f)).build(),
                120);
    }

    @Override
    public @NotNull Result resolveEffect(ItemStack wand, WandProperties properties, ServerPlayerEntity magicUser, ServerWorld world, HitResult hit) {
        if (hit.getType().equals(HitResult.Type.MISS)){
            return new Result(ActionResult.FAIL, baseCooldown/5, new ArrayList<>());
        }
        float power = properties.getPower(magicUser);
        world.createExplosion(null, world.getDamageSources().mobAttack(magicUser),
                new AdvancedExplosionBehavior(true, true, Optional.of(0.5f * power), Optional.empty()),
                hit.getPos(), 4f * power + 1f, false, World.ExplosionSourceType.TNT);
        return new Result(ActionResult.SUCCESS, baseCooldown, List.of(MagiaBorras.BOMBARDA_SOUNDEVENT));
    }
}
