package es.cristichi.mod.magiaborras.items;

import es.cristichi.mod.magiaborras.MagiaBorras;
import es.cristichi.mod.magiaborras.spells.Spell;
import es.cristichi.mod.magiaborras.util.PlayerDataPS;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;

public class SpellBook extends Item {
    // TODO:
    private final Spell spell;

    public SpellBook(Settings settings, Spell spell) {
        super(settings);
        this.spell = spell;
    }

    @Override
    public Text getName() {
        return super.getName();
    }

    public Spell getSpell() {
        return spell;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        PlayerDataPS.PlayerMagicData data = MagiaBorras.playerDataPS.getOrGenerateData(user);
        if (!world.isClient() && data.addSpell(spell)){
            MagiaBorras.playerDataPS.setData(user, data);
            user.getStackInHand(hand).decrementUnlessCreative(1, user);
            return TypedActionResult.consume(user.getStackInHand(hand));
        }
        return TypedActionResult.fail(user.getStackInHand(hand));
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(spell.getName());
    }
}