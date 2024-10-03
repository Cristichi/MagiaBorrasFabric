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
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import org.joml.Vector3f;

import java.util.List;

public class ArrestoMomentum extends Spell {
    private static final double area = 5;

    public ArrestoMomentum() {
        super("arresto", Text.translatable("magiaborras.spell.arresto"), List.of(SpellCastType.USE),
                Spell.NO_ENTITY, Spell.NO_BLOCK,
                new SpellParticlesBuilder().setRadius(area).setvMar(0).sethMar(0.1).setType(SpellParticles.SpellParticleType.FLOOR).setColorStart(new Vector3f(0.4f, 0.4f, 0.4f)).setColorEnd(new Vector3f(0.8f, 0.8f, 0.8f)).build(),
                20);
    }

    @Override
    public Result cast(ItemStack wand, WandProperties properties, ServerPlayerEntity magicUser, World world, HitResult hit) {
        List<Entity> entities = world.getOtherEntities(null, magicUser.getBoundingBox().expand(area));
        for(Entity entity : entities){
            entity.setVelocity(0, 0, 0);
            entity.speed = 0;
            entity.fallDistance = 0;
            entity.velocityDirty = true;
            if (entity instanceof LivingEntity livingEntity){
                livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.LEVITATION, 10));
            }
        }
        return new Result(ActionResult.SUCCESS, baseCooldown, List.of(MagiaBorras.ARRESTO_SOUNDEVENT));
    }
}
