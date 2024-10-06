package es.cristichi.mod.magiaborras;

import es.cristichi.mod.magiaborras.floo.fireplace.FlooFireplaceBlock;
import es.cristichi.mod.magiaborras.floo.fireplace.packets.FlooFireRenamePayload;
import es.cristichi.mod.magiaborras.floo.fireplace.packets.FlooFireTPPayload;
import es.cristichi.mod.magiaborras.floo.fireplace.packets.FlooFiresMenuPayload;
import es.cristichi.mod.magiaborras.items.wand.WandItem;
import es.cristichi.mod.magiaborras.items.wand.prop.WandProperties;
import es.cristichi.mod.magiaborras.perdata.PlayerDataPS;
import es.cristichi.mod.magiaborras.perdata.PlayerDataSyncPayload;
import es.cristichi.mod.magiaborras.screens.FlooMenuScreen;
import es.cristichi.mod.magiaborras.screens.FlooNameScreen;
import es.cristichi.mod.magiaborras.screens.SpellListScreen;
import es.cristichi.mod.magiaborras.spells.net.QuickSpellPayload;
import es.cristichi.mod.magiaborras.spells.net.SpellHitPayload;
import es.cristichi.mod.magiaborras.spells.prop.SpellParticles;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.InputUtil;
import net.minecraft.particle.DustColorTransitionParticleEffect;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Map;

public class MagiaBorrasClient implements ClientModInitializer {
    private static KeyBinding keyChangeSpell;
    private static HashMap<String, KeyBinding> keysQuickSpell;
    public static final EntityModelLayer MODEL_MAGIC_BROOM_LAYER = new EntityModelLayer(Identifier.of(MagiaBorras.MOD_ID, "magic_broom"), "main");

    @Override
    public void onInitializeClient() {
        // Keybindings
        keyChangeSpell = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.magiaborras.change_spell", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_R, "category.magiaborras.keybinds"));
        keysQuickSpell = new HashMap<>(MagiaBorras.SPELLS.size());
        for (String id : MagiaBorras.SPELLS.keySet()){
            if (!id.equals("")){
                keysQuickSpell.put(id, KeyBindingHelper.registerKeyBinding(new KeyBinding(
                        "magiaborras.spell."+id, InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "category.magiaborras.quick_spells")));
            }
        }
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            assert client.player != null;
            if (keyChangeSpell.wasPressed()) {
                if (client.player.getInventory().getMainHandStack().getItem() instanceof WandItem){
                    client.setScreen(new SpellListScreen());
                } else {
                    client.player.sendMessage(Text.translatable("magiaborras.change_spell.nowand"));
                }
            } else {
                for (Map.Entry<String, KeyBinding> entry : keysQuickSpell.entrySet()){
                    if (entry.getValue().wasPressed()){
                        WandProperties prop = WandProperties.check(client.player.getInventory().getMainHandStack());
                        if (client.player.getInventory().getMainHandStack().getItem() instanceof WandItem wand
                                && prop != null){
                            ClientPlayNetworking.send(new QuickSpellPayload(MagiaBorras.SPELLS.get(entry.getKey())));
                        } else {
                            client.player.sendMessage(Text.translatable("magiaborras.quick_cast.nowand"));
                        }
                    }
                }
            }
        });

        // Unlocked Spells update Packet
        ClientPlayNetworking.registerGlobalReceiver(PlayerDataSyncPayload.ID, (payload, context) -> context.client().execute(() -> {
            if (context.client() != null){
                PlayerDataPS.PlayerMagicData data = MagiaBorras.playerDataPS.getOrGenerateData(context.player());
                data.replaceAllSpells(payload.unlocked(), PlayerDataSyncPayload.DELIM);
                MagiaBorras.playerDataPS.updateUserData(context.player(), data);
            }
        }));

        // Spell hit Packet
        ClientPlayNetworking.registerGlobalReceiver(SpellHitPayload.ID, (payload, context) -> context.client().execute(() -> {
            if (context.client() != null && context.client().player != null) {
                DustColorTransitionParticleEffect particleEffect = new DustColorTransitionParticleEffect(
                        payload.particles().getColorStart(), payload.particles().getColorEnd(), payload.particles().getSize()
                );

                final double radius = payload.particles().getRadius();
                final double vertMargin = Math.max(payload.particles().getVMar(), 0.1);
                final double horiMargin = Math.max(payload.particles().getHMar(), 0.1);
                SpellParticles.SpellParticleType type = payload.particles().getType();

                switch (type){
                    case RAY -> {
                        Vec3d current = payload.eyeSource();

                        while (current.distanceTo(payload.hit()) > 1) {
                            Vec3d step = payload.hit().subtract(current).normalize().multiply(0.1);
                            context.client().world.addParticle(particleEffect,
                                    current.getX(), current.getY(), current.getZ(),
                                    step.x, step.y, step.z);

                            current = current.add(step);
                        }
                    }
                    case SPHERE -> {
                        Vec3d center = payload.eyeSource().subtract(0, 0.5, 0);
                        double maxMargin = Math.min(horiMargin, vertMargin);

                        for (double x = center.x-radius; x <= center.x+radius; x += horiMargin) {
                            for (double y = center.y-radius; y <= center.y+radius; y += vertMargin) {
                                for (double z = center.z-radius; z <= center.z+radius; z += horiMargin) {
                                    Vec3d current = new Vec3d(x,y,z);
                                    double dist = current.distanceTo(center);
                                    if (dist <= radius &&
                                            (payload.particles().getFill() || dist > radius - maxMargin)){
                                        context.client().world.addParticle(particleEffect,
                                                current.getX(), current.getY(), current.getZ(),
                                                0,0,0);
                                    }
                                }
                            }
                        }
                    }
                    case FLOOR -> {
                        Vec3d center = payload.eyeSource().subtract(0, 1, 0);
                        double maxMargin = Math.min(horiMargin, vertMargin);

                        double y = center.y;
                        for (double x = center.x-radius; x <= center.x+radius; x += horiMargin) {
                            for (double z = center.z-radius; z <= center.z+radius; z += horiMargin) {
                                Vec3d current = new Vec3d(x,y,z);
                                double dist = current.distanceTo(center);
                                if (dist <= radius &&
                                        (payload.particles().getFill() || dist > radius - maxMargin)) {
                                    context.client().world.addParticle(particleEffect,
                                            x, y, z, 0, 0, 0);
                                }
                            }
                        }
                    }
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
                        if (client != null && client.world != null && getSelected() != null){
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