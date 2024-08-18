package es.cristichi.mod.magiaborras.screens;

import es.cristichi.mod.magiaborras.MagiaBorras;
import es.cristichi.mod.magiaborras.PlayerDataPS;
import es.cristichi.mod.magiaborras.items.wand.prop.WandProperties;
import es.cristichi.mod.magiaborras.networking.SpellChangeInHandPayload;
import es.cristichi.mod.magiaborras.spells.Spell;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;

import java.util.ArrayList;

public class SpellListScreen extends Screen {
    public SpellListScreen() {
        super(Text.translatable("magiaborras.screen.spells.title"));
    }

    public ArrayList<ButtonWidget> spellBtns;

    @Override
    protected void init() {
        assert client != null;
        assert client.player != null;

        spellBtns = new ArrayList<>(MagiaBorras.SPELLS.size());
        int x = width / 2 - 205;
        int xAlt = width / 2 + 5;
        int y = 20;
        int btnWidth = 200;
        int btnHeight = 20;

        boolean alt = false;
        PlayerDataPS.PlayerMagicData playerWorldData = MagiaBorras.playerDataPS.getOrGenerateData(client.player);
        for (Spell spell : MagiaBorras.SPELLS.values()) {
            if (client.player.isCreative() || playerWorldData.containsSpell(spell) || spell.getId().equals("")){
                ButtonWidget btn = ButtonWidget.builder(spell.getName(), button -> {
                        System.out.println("You clicked the Spell " + spell.getId());

                        ItemStack hand = client.player.getStackInHand(Hand.MAIN_HAND);
                        WandProperties prop = WandProperties.check(hand);
                        if (prop != null) {
                            prop.spell = spell;
                            prop.apply(hand);
                            client.player.getInventory().markDirty();

                            ClientPlayNetworking.send(new SpellChangeInHandPayload(spell));
                        }

                        close();
                    })
                    .dimensions((alt?xAlt:x), y, btnWidth, btnHeight)
                    .tooltip(Tooltip.of(Text.translatable("magiaborras.screen.spells.tooltip", spell.getName())))
                    .build();
                spellBtns.add(btn);
                addDrawableChild(btn);

                if (alt){
                    y += btnHeight+10;
                }
                alt = !alt;
            }
        }
    }
}
