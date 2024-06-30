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
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import org.joml.Vector3f;

public class AvadaKedavra extends Spell {
    public AvadaKedavra() {
        super("avada", Text.translatable("magiaborras.spell.avada"), new EasyList<>(SpellCastType.USE),
                Spell.LIVING_ENTITIES, Spell.NO_BLOCK, new Vector3f(0, 200, 0), 180);
    }

    @Override
    public Result use(ItemStack wand, WandProperties properties, PlayerEntity magicUser, World world, HitResult hit) {
        if (hit.getType() == HitResult.Type.ENTITY) {
            EntityHitResult hitEnt = (EntityHitResult) hit;
            Entity ent = hitEnt.getEntity();
            if (ent instanceof LivingEntity livingEntity){
                if (!(ent instanceof ArmorStandEntity)){
                    livingEntity.damage(magicUser.getDamageSources().magic(), livingEntity.getMaxHealth());
                }
                return new Result(TypedActionResult.success(wand), baseCooldown,
                        new EasyList<>(MagiaBorras.AVADA_CAST, SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER));
            }
        }
        return new Result(TypedActionResult.fail(wand), 10, null);
    }
}
