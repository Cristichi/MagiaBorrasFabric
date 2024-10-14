package es.cristichi.mod.magiaborras.spells;

import es.cristichi.mod.magiaborras.MagiaBorras;
import es.cristichi.mod.magiaborras.items.wand.prop.WandProperties;
import es.cristichi.mod.magiaborras.spells.prop.SpellCastType;
import es.cristichi.mod.magiaborras.spells.prop.SpellParticles;
import es.cristichi.mod.magiaborras.spells.prop.SpellParticlesBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.HitResult;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.List;

public class Lumos extends Spell {
    public Lumos() {
        super("lumos", Text.translatable("magiaborras.spell.lumos"), List.of(SpellCastType.USE),
                Spell.NO_ENTITY, Spell.NO_BLOCK,
                new SpellParticlesBuilder().setType(SpellParticles.SpellParticleType.NO_PARTICLES).setColorStart(new Vector3f(1f, 1f, 0f)).build(), 20);
    }

    @Override
    public @NotNull Result resolveEffect(ItemStack wand, WandProperties properties, ServerPlayerEntity magicUser, ServerWorld world, HitResult hit) {
        List<SoundEvent> sounds;
        if (properties.lumos) {
            properties.lumos = false;
            sounds = List.of(SoundEvents.BLOCK_REDSTONE_TORCH_BURNOUT);
        } else {
            properties.lumos = true;
            sounds = List.of(MagiaBorras.LUMOS_SOUNDEVENT);
        }
//        if (!world.isClient()) {
            properties.apply(wand);
//        }
        return new Result(ActionResult.SUCCESS, baseCooldown, sounds);
    }
}
