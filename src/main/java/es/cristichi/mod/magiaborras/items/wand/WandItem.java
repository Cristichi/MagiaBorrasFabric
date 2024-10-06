package es.cristichi.mod.magiaborras.items.wand;

import es.cristichi.mod.magiaborras.MagiaBorras;
import es.cristichi.mod.magiaborras.items.wand.prop.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class WandItem extends Item {
    public WandItem(Settings settings) {
        super(settings.maxCount(1).fireproof().rarity(Rarity.RARE));
    }

    @Override
    public ItemStack getDefaultStack() {
        ItemStack is = super.getDefaultStack();
        WandProperties props = new WandProperties(
                WandCore.NONE,
                WandWood.NONE,
                WandFlexibility.NONE,
                WandLength.NONE,
                0.5f,
                MagiaBorras.SPELLS.get(""),
                false);
        props.apply(is);
        return is;
    }

    @Override
    public void onCraft(ItemStack stack, World world) {
        if (!world.isClient()) {
            try {
                WandProperties props = new WandProperties();
                props.apply(stack);
            } catch (Exception e) {
                MagiaBorras.LOGGER.error("Error when trying to craft Wand.", e);
            }
        }
    }

    @Override
    public void onCraftByPlayer(ItemStack stack, World world, PlayerEntity player) {
        if (world.isClient()) {
            player.playSound(SoundEvents.BLOCK_CONDUIT_ACTIVATE, 1.0F, 1.0F);
        } else {
            try {
                WandProperties props = new WandProperties();
                props.apply(stack);
                ArrayList<Identifier> recipes = new ArrayList<>(MagiaBorras.SPELLS.size());
                // It is intended design that only the recipes "spellbook_spellname" are unlocked.
                // Alternative recipes like spellbook_avada_head are "hidden".
                for (String spell : MagiaBorras.SPELLS.keySet()) {
                    recipes.add(Identifier.of(MagiaBorras.MOD_ID, "spellbook_" + spell));
                }
                player.unlockRecipes(recipes);
            } catch (Exception e) {
                MagiaBorras.LOGGER.error("Error when player trying to craft Wand.", e);
            }
        }
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        WandProperties prop = WandProperties.check(stack);

        if (prop == null) {
            return TypedActionResult.fail(stack);
        }
        return prop.spell.cast(world, user, stack, prop);
    }


    @Override
    public Optional<TooltipData> getTooltipData(ItemStack stack) {
        return super.getTooltipData(stack);
    }

    @Override
    public void appendTooltip(ItemStack itemStack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        WandProperties prop = WandProperties.check(itemStack);
        if (prop != null) {
            tooltip.add(Text.stringifiedTranslatable("item.magiaborras.wand.tooltip_core",
                    prop.core.getName()));
            tooltip.add(Text.stringifiedTranslatable("item.magiaborras.wand.tooltip_wood",
                    prop.wood.getName()));
            tooltip.add(Text.stringifiedTranslatable("item.magiaborras.wand.tooltip_flex",
                    prop.flex.getName()));
            tooltip.add(Text.stringifiedTranslatable("item.magiaborras.wand.tooltip_length",
                    prop.length.getName()));
        }
    }
}
