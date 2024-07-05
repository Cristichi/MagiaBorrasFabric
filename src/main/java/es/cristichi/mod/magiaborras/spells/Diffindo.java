package es.cristichi.mod.magiaborras.spells;

import es.cristichi.mod.magiaborras.MagiaBorras;
import es.cristichi.mod.magiaborras.items.wand.prop.WandProperties;
import es.cristichi.mod.magiaborras.spells.prop.SpellCastType;
import es.cristichi.mod.magiaborras.util.EasyList;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import org.joml.Vector3f;

public class Diffindo extends Spell {
    public Diffindo() {
        super("diffindo", Text.translatable("magiaborras.spell.diffindo"), new EasyList<>(SpellCastType.USE),
                Spell.LIVING_ENTITIES, Spell.NO_BLOCK, new Vector3f(200, 0, 0), 20);
    }

    @Override
    public Result use(ItemStack wand, WandProperties properties, PlayerEntity magicUser, World world, HitResult hit) {
        if (hit.getType() == HitResult.Type.ENTITY) {
            EntityHitResult hitEnt = (EntityHitResult) hit;
            Entity ent = hitEnt.getEntity();
            if (ent instanceof LivingEntity livingEntity && !(ent instanceof ArmorStandEntity)){
                livingEntity.damage(world.getDamageSources().mobAttack(magicUser), 8 * properties.getPower(magicUser));
            }
            return new Result(TypedActionResult.success(wand), baseCooldown, new EasyList<>(MagiaBorras.DIFFINDO_CAST));
        } else {
            return new Result(TypedActionResult.fail(wand), 0, null);
        }
    }
}
