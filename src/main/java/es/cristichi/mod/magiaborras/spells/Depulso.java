package es.cristichi.mod.magiaborras.spells;

import es.cristichi.mod.magiaborras.MagiaBorras;
import es.cristichi.mod.magiaborras.items.wand.prop.WandProperties;
import es.cristichi.mod.magiaborras.spells.prop.SpellCastType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class Depulso extends Spell {
    public Depulso() {
        super("depulso", Text.translatable("magiaborras.spell.depulso"), List.of(SpellCastType.USE),
                Spell.ANY_ENTITY, Spell.NO_BLOCK, new Vector3f(0.5f, 0.5f, 0.5f), 200);
    }

    @Override
    public Result use(ItemStack wand, WandProperties properties, PlayerEntity magicUser, World world, HitResult hit) {
        float power = properties.getPower(magicUser);
        if (hit instanceof EntityHitResult hitEnt){
            Entity ent = hitEnt.getEntity();
            //ent.move(MovementType.PLAYER, ent.getPos().subtract(magicUser.getPos()).normalize().multiply(power*5));
            ent.addVelocity(ent.getPos().subtract(magicUser.getPos()).normalize().multiply(power*5));
            return new Result(TypedActionResult.success(wand), baseCooldown, List.of(MagiaBorras.DEPULSO_CAST));
        }
        return new Result(TypedActionResult.fail(wand), 0, new ArrayList<>(0));
    }
}
