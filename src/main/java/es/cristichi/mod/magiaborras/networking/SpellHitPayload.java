package es.cristichi.mod.magiaborras.networking;

import es.cristichi.mod.magiaborras.MagiaBorras;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

public record SpellHitPayload(Vec3d eyeSource, Vec3d hit, Vector3f color) implements CustomPayload {
    public static final Id<SpellHitPayload> ID = new Id<>(MagiaBorras.NET_SPELL_HIT_ID);
    public static final PacketCodec<RegistryByteBuf, SpellHitPayload> CODEC = new PacketCodec<>() {
        @Override
        public SpellHitPayload decode(RegistryByteBuf buf) {
            return new SpellHitPayload(
                    new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble()),
                    new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble()),
                    new Vector3f(buf.readFloat(), buf.readFloat(), buf.readFloat())
            );
        }

        @Override
        public void encode(RegistryByteBuf buf, SpellHitPayload value) {
            buf.writeDouble(value.eyeSource.x);
            buf.writeDouble(value.eyeSource.y);
            buf.writeDouble(value.eyeSource.z);

            buf.writeDouble(value.hit.x);
            buf.writeDouble(value.hit.y);
            buf.writeDouble(value.hit.z);

            buf.writeFloat(value.color.x);
            buf.writeFloat(value.color.y);
            buf.writeFloat(value.color.z);
        }
    };

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
