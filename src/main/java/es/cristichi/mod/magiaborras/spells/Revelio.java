package es.cristichi.mod.magiaborras.spells;

import es.cristichi.mod.magiaborras.MagiaBorras;
import es.cristichi.mod.magiaborras.items.wand.prop.WandProperties;
import es.cristichi.mod.magiaborras.spells.prop.SpellCastType;
import es.cristichi.mod.magiaborras.spells.prop.SpellParticles;
import es.cristichi.mod.magiaborras.spells.prop.SpellParticlesBuilder;
import es.cristichi.mod.magiaborras.timer.SpellTimersAccess;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import org.joml.Vector3f;

import java.util.List;

public class Revelio extends Spell {
    private static final double MAX_AREA = 20;

    public Revelio() {
        super("revelio", Text.translatable("magiaborras.spell.revelio"), List.of(SpellCastType.USE),
                Spell.NO_ENTITY, Spell.NO_BLOCK,
                new SpellParticlesBuilder().setRadius(MAX_AREA).setvMar(1).sethMar(1).setType(SpellParticles.SpellParticleType.SPHERE).setColorStart(new Vector3f(1f, 1f, 0)).build(),
                60);
    }

    @Override
    public Result cast(ItemStack wand, WandProperties properties, ServerPlayerEntity magicUser, World world, HitResult hit) {
        double power = properties.getPower(magicUser);
        double radius = MAX_AREA*power;
        List<Entity> ents = world.getOtherEntities(magicUser, magicUser.getBoundingBox().expand(radius));
        for (Entity ent : ents){
            ent.setGlowing(true);
            ((SpellTimersAccess) ent).magiaborras_setRevelioTimer(baseCooldown);
        }
        SpellParticles particles = getDefaultParticles();
        particles.setRadius(radius);
        return new Result(ActionResult.SUCCESS, baseCooldown, List.of(MagiaBorras.REVELIO_SOUNDEVENT), particles);
    }
}
