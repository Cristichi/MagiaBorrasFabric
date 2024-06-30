package es.cristichi.mod.magiaborras.spells;

import es.cristichi.mod.magiaborras.MagiaBorras;
import es.cristichi.mod.magiaborras.items.wand.prop.WandProperties;
import es.cristichi.mod.magiaborras.spells.prop.SpellCastType;
import es.cristichi.mod.magiaborras.util.EasyList;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Vector3f;

public class Accio extends Spell {
    public Accio() {
        super("accio", Text.translatable("magiaborras.spell.accio"), new EasyList<>(SpellCastType.USE),
                Spell.ANY_ENTITY, Spell.NO_BLOCK, new Vector3f(200, 0, 0), 20);
    }

    @Override
    public Result use(ItemStack wand, WandProperties properties, PlayerEntity magicUser, World world, HitResult hit) {
        if (hit.getType() == HitResult.Type.ENTITY) {
            EntityHitResult hitEnt = (EntityHitResult) hit;
            Entity ent = hitEnt.getEntity();
            Vec3d pos = ent.getPos();
            Vec3d target = magicUser.getPos();
            Vec3d velocity = target.subtract(pos);
            ent.move(MovementType.PLAYER, velocity);
            return new Result(TypedActionResult.success(wand), baseCooldown, new EasyList<>(MagiaBorras.ACCIO_CAST));
        } else {
            return new Result(TypedActionResult.fail(wand), 0, null);
        }
    }
}
