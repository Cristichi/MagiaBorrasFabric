package es.cristichi.mod.magiaborras.spells;

import es.cristichi.mod.magiaborras.items.wand.prop.WandProperties;
import es.cristichi.mod.magiaborras.spells.prop.SpellCastType;
import es.cristichi.mod.magiaborras.util.EasyList;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

public class DefaultSpell extends Spell {
    public DefaultSpell() {
        super("", Text.translatable("magiaborras.spell.none"), new EasyList<>(SpellCastType.USE),
                Spell.NO_ENTITY, Spell.NO_BLOCK, null, 10);

    }

    @Override
    public Result use(ItemStack wand, WandProperties properties, PlayerEntity magicUser, World world, HitResult hit) {
        SoundEvent sound = null;
        if (world.isClient()) {
            float power = properties.getPower(magicUser);
            double variation = 2 * power + 1;
            int count = 100 + (int) (200 * power);
            SimpleParticleType particle = null;
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
                sound = SoundEvents.ENTITY_PLAYER_HURT_ON_FIRE;
            } else if (power < 0.8) {
                particle = ParticleTypes.EGG_CRACK;
                sound = SoundEvents.BLOCK_COMPOSTER_FILL_SUCCESS;
            } else if (power < 0.9) {
                particle = ParticleTypes.FIREWORK;
                sound = SoundEvents.ENTITY_FIREWORK_ROCKET_BLAST;
            } else if (power < 1) {
                particle = ParticleTypes.CHERRY_LEAVES;
                sound = SoundEvents.BLOCK_CHERRY_LEAVES_PLACE;
                count = (int) (count * 1.5);
            } else {
                particle = ParticleTypes.DRAGON_BREATH;
                sound = SoundEvents.ENTITY_ENDER_DRAGON_GROWL;
                count = count * 2;
            }
            for (int i = 0; i < count; i++) {
                double varX = Math.random() * variation * 2 - variation;
                double varY = Math.random() * variation * 2 - variation;
                double varZ = Math.random() * variation * 2 - variation;
                world.addParticle(particle, magicUser.getX() + varX, magicUser.getY() +
                        magicUser.getEyeHeight(magicUser.getPose()) + varY, magicUser.getZ() + varZ, 0, 0, 0);
            }
        }
        return new Result(TypedActionResult.success(wand), baseCooldown, new EasyList<>(sound));
    }
}
