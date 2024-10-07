package es.cristichi.mod.magiaborras.spells;

import es.cristichi.mod.magiaborras.MagiaBorras;
import es.cristichi.mod.magiaborras.items.wand.prop.WandProperties;
import es.cristichi.mod.magiaborras.spells.prop.SpellCastType;
import es.cristichi.mod.magiaborras.spells.prop.SpellParticles;
import es.cristichi.mod.magiaborras.spells.prop.SpellParticlesBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DustColorTransitionParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.HitResult;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.List;

public class Vermillious extends Spell {
    public static final int PUNISH_COOLDOWM = 60;

    public Vermillious() {
        super("vermillious", Text.translatable("magiaborras.spell.vermillious"), List.of(SpellCastType.USE),
                Spell.NO_ENTITY, Spell.NO_BLOCK,
                new SpellParticlesBuilder()
                        .setType(SpellParticles.SpellParticleType.RAY)
                        .sethMar(1)
                        .setColorStart(new Vector3f(0.4f, 0f, 0f))
                        .setColorEnd(new Vector3f(2f, 0f, 0f))
                        .setSize(DustColorTransitionParticleEffect.MAX_SCALE)
                        .build(),
                3);
    }

    @Override
    public @NotNull Result resolveEffect(ItemStack wand, WandProperties properties, ServerPlayerEntity magicUser, ServerWorld world, HitResult hit) {
        return new Result(ActionResult.SUCCESS, baseCooldown, List.of(MagiaBorras.VERMILLIOUS_SOUNDEVENT));
    }
}
