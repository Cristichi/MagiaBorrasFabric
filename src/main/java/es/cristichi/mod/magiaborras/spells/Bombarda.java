package es.cristichi.mod.magiaborras.spells;

import es.cristichi.mod.magiaborras.MagiaBorras;
import es.cristichi.mod.magiaborras.items.wand.prop.WandProperties;
import es.cristichi.mod.magiaborras.spells.prop.SpellCastType;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import net.minecraft.world.explosion.AdvancedExplosionBehavior;
import org.joml.Vector3f;

import java.util.List;
import java.util.Optional;

public class Bombarda extends Spell {
    public Bombarda() {
        super("bombarda", Text.translatable("magiaborras.spell.bombarda"), List.of(SpellCastType.USE),
                Spell.ANY_ENTITY, Spell.ANY_BLOCK, new Vector3f(0.9f, 0.9f, 0.9f), 120);
    }

    @Override
    public Result cast(ItemStack wand, WandProperties properties, ServerPlayerEntity magicUser, World world, HitResult hit) {
        float power = properties.getPower(magicUser);
        world.createExplosion(null, world.getDamageSources().mobAttack(magicUser),
                new AdvancedExplosionBehavior(true, true, Optional.of(0.5f * power), Optional.empty()),
                hit.getPos(), 4f * power + 1f, false, World.ExplosionSourceType.TNT);
        return new Result(ActionResult.SUCCESS, baseCooldown, List.of(MagiaBorras.BOMBARDA_CAST));
    }
}
