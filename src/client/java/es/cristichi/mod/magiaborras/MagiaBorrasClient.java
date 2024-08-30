package es.cristichi.mod.magiaborras;

import es.cristichi.mod.magiaborras.floo.fireplace.net.FlooFireRenamePayload;
import es.cristichi.mod.magiaborras.items.wand.WandItem;
import es.cristichi.mod.magiaborras.screens.FlooNameScreen;
import es.cristichi.mod.magiaborras.spells.net.SpellHitPayload;
import es.cristichi.mod.magiaborras.screens.SpellListScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.particle.DustColorTransitionParticleEffect;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

public class MagiaBorrasClient implements ClientModInitializer {
    private static KeyBinding keyChangeSpell;

    @SuppressWarnings("DataFlowIssue")
    @Override
    public void onInitializeClient() {
        // Keybindings
        keyChangeSpell = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.magiaborras.change_spell", // The translation key of the keybinding's name
                InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
                GLFW.GLFW_KEY_R, // The keycode of the key
                "category.magiaborras.keybinds" // The translation key of the keybinding's category.
        ));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            assert client.player != null;
            if (keyChangeSpell.wasPressed()) {
                if (client.player.getInventory().getMainHandStack().getItem() instanceof WandItem){
                    client.setScreen(new SpellListScreen());
                } else {
                    client.player.sendMessage(Text.translatable("magiaborras.screen.spells.nowand"));
                }
            }
        });

        // Spell hit packet receive
        ClientPlayNetworking.registerGlobalReceiver(SpellHitPayload.ID, (payload, context) -> context.client().execute(() -> {
            if (context.client() != null && context.client().player != null && context.client().world != null){
                DustColorTransitionParticleEffect particleEffect = new DustColorTransitionParticleEffect(
                        payload.color(), payload.color(), 0.6f
                );

                Vec3d current = payload.eyeSource();

                while (current.distanceTo(payload.hit()) > 1) {
                    Vec3d step = payload.hit().subtract(current).normalize().multiply(0.1);
                    context.client().world.addParticle(particleEffect,
                            current.getX(), current.getY(), current.getZ(),
                            step.x, step.y, step.z);

                    current = current.add(step);
                }
            }
        }));

        // Floo Fireplace rename packet receive + send
        ClientPlayNetworking.registerGlobalReceiver(FlooFireRenamePayload.ID, (payload, context) -> context.client().execute(() -> {
            if (context.client() != null){
                context.client().setScreen(new FlooNameScreen(payload.name(), payload.registered()) {
                    @Override
                    public void close() {
                        if (!getName().equals(payload.name()) || !getRegistered() == payload.registered()){
                            ClientPlayNetworking.send(new FlooFireRenamePayload(payload.block(), getName(), getRegistered()));
                        }
                        super.close();
                    }
                });
            }
        }));

        // TODO: Floo Fireplace menu to TP receive + send
    }
}