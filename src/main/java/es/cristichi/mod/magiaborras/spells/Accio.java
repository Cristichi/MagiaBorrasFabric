package es.cristichi.mod.magiaborras.spells;

import es.cristichi.mod.magiaborras.MagiaBorras;
import es.cristichi.mod.magiaborras.items.wand.prop.WandProperties;
import es.cristichi.mod.magiaborras.spells.prop.SpellCastType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Vector3f;

import java.util.List;

public class Accio extends Spell {
    public Accio() {
        super("accio", Text.translatable("magiaborras.spell.accio"), List.of(SpellCastType.USE),
                Spell.ANY_ENTITY, Spell.NO_BLOCK, new Vector3f(1f, 0, 0), 20);
    }

    @Override
    public Result cast(ItemStack wand, WandProperties properties, ServerPlayerEntity magicUser, World world, HitResult hit) {
        if (hit.getType() == HitResult.Type.ENTITY) {
            EntityHitResult hitEnt = (EntityHitResult) hit;
            Entity ent = hitEnt.getEntity();
            Vec3d pos = ent.getPos();
            Vec3d target = magicUser.getEyePos();
            Vec3d velocity = target.subtract(pos).multiply(0.33333);
            boolean gravity = ent.hasNoGravity();
            ent.setNoGravity(true);
            ent.move(MovementType.PLAYER, velocity);
            ent.move(MovementType.PLAYER, velocity);
            ent.move(MovementType.PLAYER, velocity);
            ent.setNoGravity(gravity);
            return new Result(ActionResult.SUCCESS, baseCooldown, List.of(MagiaBorras.ACCIO_CAST));
        } else {
            return new Result(ActionResult.FAIL, 0, null);
        }
    }
}
