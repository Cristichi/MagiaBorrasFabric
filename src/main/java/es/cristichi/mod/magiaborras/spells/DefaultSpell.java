package es.cristichi.mod.magiaborras.spells;

import es.cristichi.mod.magiaborras.items.wand.prop.WandProperties;
import es.cristichi.mod.magiaborras.spells.prop.SpellCastType;
import es.cristichi.mod.magiaborras.spells.prop.SpellParticles;
import es.cristichi.mod.magiaborras.spells.prop.SpellParticlesBuilder;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.HitResult;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public class DefaultSpell extends Spell {
    public DefaultSpell() {
        super("", Text.translatable("magiaborras.spell.none"), List.of(SpellCastType.USE),
                Spell.NO_ENTITY, Spell.NO_BLOCK,
                new SpellParticlesBuilder().setType(SpellParticles.SpellParticleType.NO_PARTICLES).build(),
                10);

    }

    @Override
    public @NotNull Result resolveEffect(ItemStack wand, WandProperties properties, ServerPlayerEntity magicUser, ServerWorld world, HitResult hit) {
        float power = properties.getPower(magicUser);
        int count = 100 + (int) (power*200);
        SoundEvent sound = SoundEvents.ENTITY_ENDER_DRAGON_GROWL;
        SimpleParticleType particle = ParticleTypes.DRAGON_BREATH;
        if (power < 0.1) {
            particle = ParticleTypes.ASH;
            sound = SoundEvents.BLOCK_DISPENSER_FAIL;
        } else if (power < 0.2) {
            particle = ParticleTypes.FALLING_WATER;
            sound = SoundEvents.BLOCK_DISPENSER_FAIL;
        } else if (power < 0.3) {
            particle = ParticleTypes.DRIPPING_WATER;
            sound = SoundEvents.AMBIENT_UNDERWATER_ENTER;
        } else if (power < 0.4) {
            particle = ParticleTypes.UNDERWATER;
            sound = SoundEvents.AMBIENT_UNDERWATER_ENTER;
        } else if (power < 0.5) {
            particle = ParticleTypes.FALLING_LAVA;
            sound = SoundEvents.BLOCK_LAVA_POP;
        } else if (power < 0.6) {
            particle = ParticleTypes.DRIPPING_LAVA;
            sound = SoundEvents.BLOCK_LAVA_POP;
        } else if (power < 0.7) {
            particle = ParticleTypes.FLAME;
            sound = SoundEvents.BLOCK_FIRE_AMBIENT;
        } else if (power < 0.8) {
            particle = ParticleTypes.EGG_CRACK;
            sound = SoundEvents.BLOCK_AMETHYST_BLOCK_STEP;
        } else if (power < 0.9) {
            particle = ParticleTypes.FIREWORK;
            sound = SoundEvents.ENTITY_FIREWORK_ROCKET_LARGE_BLAST;
        } else if (power < 1) {
            particle = ParticleTypes.CHERRY_LEAVES;
            sound = SoundEvents.BLOCK_CHERRY_LEAVES_PLACE;
        }

        double maxVariation = 1 + power*4;
        float var = (float) (Math.random() * maxVariation);
        Collection<ServerPlayerEntity> players = PlayerLookup.tracking((ServerWorld) world, magicUser.getBlockPos());
        ParticleS2CPacket packet = new ParticleS2CPacket(particle,
                true, magicUser.getX(), magicUser.getY(), magicUser.getZ(),
                var, var, var,
                0, count);

//        ((ServerPlayerEntity) magicUser).networkHandler.sendPacket(packet);
        for (ServerPlayerEntity player : players){
            player.networkHandler.sendPacket(packet);
        }
        return new Result(ActionResult.SUCCESS, baseCooldown, List.of(sound));
    }
}
