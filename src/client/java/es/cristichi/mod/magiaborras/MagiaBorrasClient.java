package es.cristichi.mod.magiaborras;

import es.cristichi.mod.magiaborras.floo.fireplace.FlooFireplaceBlock;
import es.cristichi.mod.magiaborras.floo.fireplace.packets.FlooFireRenamePayload;
import es.cristichi.mod.magiaborras.floo.fireplace.packets.FlooFireTPPayload;
import es.cristichi.mod.magiaborras.floo.fireplace.packets.FlooFiresMenuPayload;
import es.cristichi.mod.magiaborras.items.wand.WandItem;
import es.cristichi.mod.magiaborras.perdata.PlayerDataPS;
import es.cristichi.mod.magiaborras.perdata.PlayerDataSyncPayload;
import es.cristichi.mod.magiaborras.screens.FlooMenuScreen;
import es.cristichi.mod.magiaborras.screens.FlooNameScreen;
import es.cristichi.mod.magiaborras.screens.SpellListScreen;
import es.cristichi.mod.magiaborras.spells.net.SpellHitPayload;
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

import java.util.Arrays;

public class MagiaBorrasClient implements ClientModInitializer {
    private static KeyBinding keyChangeSpell;

    @SuppressWarnings("DataFlowIssue")
    @Override
    public void onInitializeClient() {
        // Keybindings
        keyChangeSpell = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.magiaborras.change_spell", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_R, "category.magiaborras.keybinds"));
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

        // Unlocked Spells update Packet
        ClientPlayNetworking.registerGlobalReceiver(PlayerDataSyncPayload.ID, (payload, context) -> context.client().execute(() -> {
            if (context.client() != null){
                PlayerDataPS.PlayerMagicData data = MagiaBorras.playerDataPS.getOrGenerateData(context.player());
                data.replaceAllSpells(payload.unlocked(), PlayerDataSyncPayload.DELIM);
                MagiaBorras.playerDataPS.updateUserData(context.player(), data);
                MagiaBorras.LOGGER.info(Arrays.toString(data.getUnlockedSpells()));
            }
        }));

        // Spell hit Packet
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

        // Floo Fireplace rename Packets
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

        // Floo Fireplace menu + TP Packets
        ClientPlayNetworking.registerGlobalReceiver(FlooFiresMenuPayload.ID, (payload, context) -> context.client().execute(() -> {
            if (context.client() != null){
                context.client().setScreen(new FlooMenuScreen(payload.fireplaces()) {
                    @Override
                    public void close() {
                        if (getSelected() != null){
                            client.world.getChunk(getSelected()).getBlockState(getSelected());
                            for (int i=0; i< 1000; i++){
                                client.world.addParticle(FlooFireplaceBlock.tpParticles, true,
                                        getSelected().getX()+client.world.random.nextDouble(),
                                        getSelected().getY()+2+client.world.random.nextDouble(),
                                        getSelected().getZ()+client.world.random.nextDouble(),
                                        client.world.random.nextDouble(),
                                        client.world.random.nextDouble(),
                                        client.world.random.nextDouble());
                            }
                            ClientPlayNetworking.send(new FlooFireTPPayload(getSelected()));
                        }
                        super.close();
                    }
                });
            }
        }));

    }
}