package es.cristichi.mod.magiaborras.spells;

import es.cristichi.mod.magiaborras.MagiaBorras;
import es.cristichi.mod.magiaborras.items.wand.prop.WandProperties;
import es.cristichi.mod.magiaborras.spells.prop.SpellCastType;
import es.cristichi.mod.magiaborras.spells.prop.SpellParticles;
import es.cristichi.mod.magiaborras.spells.prop.SpellParticlesBuilder;
import es.cristichi.mod.magiaborras.mixinaccess.EntitySpellsAccess;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class Depulso extends Spell {
    public Depulso() {
        super("depulso", Text.translatable("magiaborras.spell.depulso"), List.of(SpellCastType.USE),
                Spell.ANY_ENTITY, Spell.NO_BLOCK,
                new SpellParticlesBuilder().setType(SpellParticles.SpellParticleType.RAY).setColorStart(new Vector3f(0.5f, 0.5f, 0.5f)).build(),
                80);
    }

    @Override
    public Result cast(ItemStack wand, WandProperties properties, ServerPlayerEntity magicUser, World world, HitResult hit) {
        float power = properties.getPower(magicUser);
        if (hit instanceof EntityHitResult hitEnt){
            Entity ent = hitEnt.getEntity();
            //ent.addVelocity(ent.getPos().subtract(magicUser.getPos()).normalize().multiply(power*5));
            Vec3d velocity = ent.getPos().subtract(magicUser.getPos()).multiply(power*0.4);
            ((EntitySpellsAccess) ent).magiaborras_setMovement(20, velocity);
            return new Result(ActionResult.SUCCESS, baseCooldown, List.of(MagiaBorras.DEPULSO_SOUNDEVENT));
        }
        return new Result(ActionResult.FAIL, 0, new ArrayList<>(0));
    }
}
