package es.cristichi.mod.magiaborras.spells;

import es.cristichi.mod.magiaborras.MagiaBorras;
import es.cristichi.mod.magiaborras.items.wand.prop.WandProperties;
import es.cristichi.mod.magiaborras.spells.prop.SpellCastType;
import es.cristichi.mod.magiaborras.spells.prop.SpellParticles;
import es.cristichi.mod.magiaborras.spells.prop.SpellParticlesBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.List;

public class WingardiumLeviosa extends Spell {
    public WingardiumLeviosa() {
        super("wingardium_leviosa", Text.translatable("magiaborras.spell.wingardium_leviosa"), List.of(SpellCastType.USE),
                Spell.ANY_ENTITY, Spell.NO_BLOCK,
                new SpellParticlesBuilder()
                        .setType(SpellParticles.SpellParticleType.SPHERE)
                        .setRadius(0.1)
                        .setColorStart(new Vector3f(1f, 1f, 0))
                        .setSize(2f)
                        .setvMar(0.1)
                        .sethMar(0.1)
                        .build(),
                80);
    }

    @Override
    public @NotNull Result resolveEffect(ItemStack wand, WandProperties properties, ServerPlayerEntity magicUser, ServerWorld world, HitResult hit) {
        if (hit.getType() == HitResult.Type.ENTITY) {
            EntityHitResult hitEnt = (EntityHitResult) hit;
            Entity ent = hitEnt.getEntity();
            if (ent instanceof LivingEntity aliveEnt) {
                int ticks = (int) (baseCooldown + 10 * properties.getPower(magicUser));
                aliveEnt.addStatusEffect(new StatusEffectInstance(StatusEffects.LEVITATION, ticks, 1), magicUser);
                aliveEnt.setVelocity(aliveEnt.getVelocity().add(new Vec3d(0, .1, 0)));
            } else {
                ent.setVelocity(ent.getVelocity().add(new Vec3d(0, .3, 0)));
            }
            return new Result(ActionResult.SUCCESS, baseCooldown, List.of(MagiaBorras.WINGARDIUM_SOUNDEVENT));
        } else {
            return new Result(ActionResult.FAIL, 0, null);
        }
    }
}
