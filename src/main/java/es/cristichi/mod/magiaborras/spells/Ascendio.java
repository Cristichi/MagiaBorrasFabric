package es.cristichi.mod.magiaborras.spells;

import es.cristichi.mod.magiaborras.MagiaBorras;
import es.cristichi.mod.magiaborras.items.wand.prop.WandProperties;
import es.cristichi.mod.magiaborras.mixinaccess.EntitySpellsAccess;
import es.cristichi.mod.magiaborras.spells.prop.SpellCastType;
import es.cristichi.mod.magiaborras.spells.prop.SpellParticles;
import es.cristichi.mod.magiaborras.spells.prop.SpellParticlesBuilder;
import net.minecraft.entity.MovementType;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Vector3f;

import java.util.List;

public class Ascendio extends Spell {
    private static final Vec3d step = new Vec3d(0, 0.5, 0);

    public Ascendio() {
        super("ascendio", Text.translatable("magiaborras.spell.ascendio"), List.of(SpellCastType.USE), Spell.NO_ENTITY, Spell.NO_BLOCK,
                new SpellParticlesBuilder()
                        .setType(SpellParticles.SpellParticleType.SPHERE)
                        .setRadius(1)
                        .setColorStart(new Vector3f(0, 0, 1f))
                        .build(),
                20);
    }

    @Override
    public Result cast(ItemStack wand, WandProperties properties, ServerPlayerEntity magicUser, World world, HitResult hit) {
        ((EntitySpellsAccess) magicUser).magiaborras_setMovement(baseCooldown-5, step, MovementType.PLAYER);
        return new Result(ActionResult.SUCCESS, baseCooldown, List.of(MagiaBorras.ASCENDIO_SOUNDEVENT));
    }
}
