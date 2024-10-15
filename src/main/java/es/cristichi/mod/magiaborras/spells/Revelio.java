package es.cristichi.mod.magiaborras.spells;

import es.cristichi.mod.magiaborras.MagiaBorras;
import es.cristichi.mod.magiaborras.items.wand.prop.WandProperties;
import es.cristichi.mod.magiaborras.mixinaccess.EntitySpellsAccess;
import es.cristichi.mod.magiaborras.spells.prop.SpellCastType;
import es.cristichi.mod.magiaborras.spells.prop.SpellParticles;
import es.cristichi.mod.magiaborras.spells.prop.SpellParticlesBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.HitResult;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.List;

public class Revelio extends Spell {
    private static final double MIN_AREA = 10;
    private static final double MAX_AREA = 30;

    public Revelio() {
        super("revelio", Text.translatable("magiaborras.spell.revelio"), List.of(SpellCastType.USE),
                Spell.NO_ENTITY, Spell.NO_BLOCK,
                new SpellParticlesBuilder()
                        .setType(SpellParticles.SpellParticleType.SPHERE)
                        .setRadius(MAX_AREA)
                        .sethMar(1)
                        .setvMar(1)
                        .setColorStart(new Vector3f(1f, 1f, 0))
                        .setSize(5f)
                        .setFill(false)
                        .build(),
                60);
    }

    @Override
    public @NotNull Result resolveEffect(ItemStack wand, WandProperties properties, ServerPlayerEntity magicUser, ServerWorld world, HitResult hit) {
        double power = properties.getPower(magicUser);
        double radius = (MAX_AREA-MIN_AREA)*power+MIN_AREA;
        List<Entity> ents = world.getOtherEntities(magicUser, magicUser.getBoundingBox().expand(radius));
        for (Entity ent : ents){
            ((EntitySpellsAccess) ent).magiaborras_setRevelioTimer(baseCooldown);
        }
        SpellParticles particles = getDefaultParticles();
        double partRad = Math.min(radius, magicUser.getViewDistance());
        float percent = (float)(partRad/MAX_AREA + 0.2f);
        particles.setRadius(partRad);
        particles.sethMar(particles.getHMar()*1/percent);
        particles.setvMar(particles.getVMar()*1/percent);
        particles.setSize(particles.getSize()*percent);
        return new Result(ActionResult.SUCCESS, baseCooldown, List.of(MagiaBorras.REVELIO_SOUNDEVENT), particles);
    }
}
