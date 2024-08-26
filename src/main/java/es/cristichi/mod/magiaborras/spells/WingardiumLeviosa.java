package es.cristichi.mod.magiaborras.spells;

import es.cristichi.mod.magiaborras.MagiaBorras;
import es.cristichi.mod.magiaborras.items.wand.prop.WandProperties;
import es.cristichi.mod.magiaborras.spells.prop.SpellCastType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public class WingardiumLeviosa extends Spell {
    public WingardiumLeviosa() {
        super("wingardium_leviosa", Text.translatable("magiaborras.spell.wingardium_leviosa"), List.of(SpellCastType.USE),
                Spell.ANY_ENTITY, Spell.NO_BLOCK, null, 80);
    }

    @Override
    public Result use(ItemStack wand, WandProperties properties, PlayerEntity magicUser, World world, HitResult hit) {
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
            return new Result(ActionResult.SUCCESS, baseCooldown, List.of(MagiaBorras.WING_LEV_CAST));
        } else {
            return new Result(ActionResult.FAIL, 0, null);
        }
    }
}
