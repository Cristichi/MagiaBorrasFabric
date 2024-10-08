package es.cristichi.mod.magiaborras.spells;

import es.cristichi.mod.magiaborras.MagiaBorras;
import es.cristichi.mod.magiaborras.items.wand.prop.WandProperties;
import es.cristichi.mod.magiaborras.spells.prop.SpellCastType;
import es.cristichi.mod.magiaborras.spells.prop.SpellParticles;
import es.cristichi.mod.magiaborras.spells.prop.SpellParticlesBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.List;

public class Diffindo extends Spell {
    public Diffindo() {
        super("diffindo", Text.translatable("magiaborras.spell.diffindo"), List.of(SpellCastType.USE),
                Spell.LIVING_ENTITIES, Spell.NO_BLOCK,
                new SpellParticlesBuilder().setType(SpellParticles.SpellParticleType.RAY).setColorStart(new Vector3f(0.7f, 0, 0)).build(),
                20);
    }

    @Override
    public @NotNull Result resolveEffect(ItemStack wand, WandProperties properties, ServerPlayerEntity magicUser, ServerWorld world, HitResult hit) {
        if (hit.getType() == HitResult.Type.ENTITY) {
            EntityHitResult hitEnt = (EntityHitResult) hit;
            Entity ent = hitEnt.getEntity();
            if (ent instanceof LivingEntity livingEntity && !(ent instanceof ArmorStandEntity)){
                livingEntity.damage(world.getDamageSources().mobAttack(magicUser), 8 * properties.getPower(magicUser));
            }
            return new Result(ActionResult.SUCCESS, baseCooldown, List.of(MagiaBorras.DIFFINDO_SOUNDEVENT));
        } else {
            return new Result(ActionResult.FAIL, 0, null);
        }
    }
}
