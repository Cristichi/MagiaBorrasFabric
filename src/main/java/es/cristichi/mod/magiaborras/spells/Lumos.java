package es.cristichi.mod.magiaborras.spells;

import es.cristichi.mod.magiaborras.MagiaBorras;
import es.cristichi.mod.magiaborras.items.wand.prop.WandProperties;
import es.cristichi.mod.magiaborras.spells.prop.SpellCastType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

import java.util.List;

public class Lumos extends Spell {
    public Lumos() {
        super("lumos", Text.translatable("magiaborras.spell.lumos"), List.of(SpellCastType.USE),
                Spell.NO_ENTITY, Spell.NO_BLOCK, null, 20);
    }

    @Override
    public Result use(ItemStack wand, WandProperties properties, PlayerEntity magicUser, World world, HitResult hit) {
        // TODO: Wait for 1.21 Dynamic Lighs and use it to complete Lumos. Logic is already here, just make it shine!
        //  https://modrinth.com/mod/lambdynamiclights
        List<SoundEvent> sounds;
        if (properties.lumos) {
            properties.lumos = false;
            sounds = List.of(SoundEvents.BLOCK_REDSTONE_TORCH_BURNOUT);
        } else {
            properties.lumos = true;
            sounds = List.of(MagiaBorras.LUMOS_CAST);
        }
//        if (!world.isClient()) {
            properties.apply(wand);
//        }
        return new Result(TypedActionResult.success(wand), baseCooldown, sounds);
    }
}
