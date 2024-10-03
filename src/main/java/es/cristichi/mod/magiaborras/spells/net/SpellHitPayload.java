package es.cristichi.mod.magiaborras.spells.net;

import es.cristichi.mod.magiaborras.MagiaBorras;
import es.cristichi.mod.magiaborras.spells.prop.SpellParticles;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.Vec3d;

public record SpellHitPayload(Vec3d eyeSource, Vec3d hit, SpellParticles particles) implements CustomPayload {
    public static final Id<SpellHitPayload> ID = new Id<>(MagiaBorras.NET_SPELL_HIT_ID);
    public static final PacketCodec<RegistryByteBuf, SpellHitPayload> CODEC = new PacketCodec<>() {
        @Override
        public SpellHitPayload decode(RegistryByteBuf buf) {
            return new SpellHitPayload(
                    new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble()),
                    new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble()),
                    new SpellParticles(
                            buf.readDouble(),
                            buf.readDouble(),
                            buf.readDouble(),
                            SpellParticles.SpellParticleType.values()[buf.readInt()],
                            buf.readVector3f(),
                            buf.readVector3f())
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


            buf.writeDouble(value.particles.radius());
            buf.writeDouble(value.particles.vMar());
            buf.writeDouble(value.particles.hMar());
            buf.writeInt(value.particles.type().ordinal());
            buf.writeVector3f(value.particles.colorStart());
            buf.writeVector3f(value.particles.colorEnd());
        }
    };

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
