package es.cristichi.mod.magiaborras.spells;

import es.cristichi.mod.magiaborras.MagiaBorras;
import es.cristichi.mod.magiaborras.items.wand.prop.WandProperties;
import es.cristichi.mod.magiaborras.spells.prop.SpellCastType;
import es.cristichi.mod.magiaborras.util.EasyList;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

import java.util.List;

public class ArrestoMomentum extends Spell {
    private static final double area = 50;

    public ArrestoMomentum() {
        super("arresto", Text.translatable("magiaborras.spell.arresto"), new EasyList<>(SpellCastType.USE),
                Spell.NO_ENTITY, Spell.NO_BLOCK, null, 20);
    }

    @Override
    public Result use(ItemStack wand, WandProperties properties, PlayerEntity magicUser, World world, HitResult hit) {
        List<Entity> entities = world.getOtherEntities(null, magicUser.getBoundingBox().expand(area));
        for(Entity entity : entities){
            entity.setVelocity(0, 0, 0);
            entity.fallDistance = 0;
            if (entity instanceof LivingEntity livingEntity){
                livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.LEVITATION, baseCooldown/2));
            }
        }
        return new Result(TypedActionResult.success(wand), baseCooldown, new EasyList<>(MagiaBorras.ARRESTO_CAST));
    }
}
