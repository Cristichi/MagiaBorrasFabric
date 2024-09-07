package es.cristichi.mod.magiaborras.spells;

import es.cristichi.mod.magiaborras.MagiaBorras;
import es.cristichi.mod.magiaborras.items.wand.prop.WandProperties;
import es.cristichi.mod.magiaborras.spells.prop.SpellCastType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import org.joml.Vector3f;

import java.util.List;

public class AvadaKedavra extends Spell {
    public AvadaKedavra() {
        super("avada", Text.translatable("magiaborras.spell.avada"), List.of(SpellCastType.USE),
                Spell.LIVING_ENTITIES, Spell.NO_BLOCK, new Vector3f(0, 0.9f, 0), 240);
    }

    @Override
    public Result cast(ItemStack wand, WandProperties properties, ServerPlayerEntity magicUser, World world, HitResult hit) {
        if (hit.getType() == HitResult.Type.ENTITY) {
            EntityHitResult hitEnt = (EntityHitResult) hit;
            Entity ent = hitEnt.getEntity();
            if (ent instanceof LivingEntity livingEntity){
                if (!(ent instanceof ArmorStandEntity)){
                    livingEntity.damage(magicUser.getDamageSources().mobAttack(magicUser), livingEntity.getMaxHealth());
                }
                return new Result(ActionResult.SUCCESS, baseCooldown,
                        List.of(MagiaBorras.AVADA_CAST, SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER));
            }
        }
        return new Result(ActionResult.FAIL, 10, null);
    }
}
