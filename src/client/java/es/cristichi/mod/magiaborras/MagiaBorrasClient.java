package es.cristichi.mod.magiaborras;

import es.cristichi.mod.magiaborras.items.wand.WandItem;
import es.cristichi.mod.magiaborras.screens.SpellListScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class MagiaBorrasClient implements ClientModInitializer {
    private static KeyBinding keyBinding;
    @Override
    public void onInitializeClient() {
        // This entrypoint is suitable for setting up client-specific logic, such as rendering.

        // Keybindings
        keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.magiaborras.change_spell", // The translation key of the keybinding's name
                InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
                GLFW.GLFW_KEY_R, // The keycode of the key
                "category.magiaborras.keybinds" // The translation key of the keybinding's category.
        ));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            assert client.player != null;
            if (keyBinding.wasPressed()) {
                if (client.player.getInventory().getMainHandStack().getItem() instanceof WandItem){
                    client.setScreen(new SpellListScreen());
                } else {
                    client.player.sendMessage(Text.translatable("magiaborras.screen.spells.nowand"));
                }
            }
        });
    }
}