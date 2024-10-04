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
    private static final double area = 10;

    public ArrestoMomentum() {
        super("arresto", Text.translatable("magiaborras.spell.arresto"), List.of(SpellCastType.USE),
                Spell.NO_ENTITY, Spell.NO_BLOCK,
                new SpellParticlesBuilder().setRadius(area).sethMar(0.2).setType(SpellParticles.SpellParticleType.FLOOR).setColorStart(new Vector3f(0.4f, 0.4f, 0.4f)).setColorEnd(new Vector3f(0.8f, 0.8f, 0.8f)).build(),
                20);
    }

    @Override
    public Result cast(ItemStack wand, WandProperties properties, ServerPlayerEntity magicUser, World world, HitResult hit) {
        double finalArea = properties.getPower(magicUser)*area;
        List<Entity> entities = world.getOtherEntities(null, magicUser.getBoundingBox().expand(finalArea));
        for(Entity entity : entities){
            entity.setVelocity(0, 0, 0);
            entity.fallDistance = 0;
            if (entity instanceof LivingEntity livingEntity){
                livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.LEVITATION, 10));
            }
        }
        SpellParticles particles = getDefaultParticles();
        particles.setRadius(finalArea);
        return new Result(ActionResult.SUCCESS, baseCooldown, List.of(MagiaBorras.ARRESTO_SOUNDEVENT), particles);
    }
}
