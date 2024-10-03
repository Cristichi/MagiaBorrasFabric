package es.cristichi.mod.magiaborras.spells.net;

import es.cristichi.mod.magiaborras.MagiaBorras;
import es.cristichi.mod.magiaborras.spells.prop.SpellParticles;
import es.cristichi.mod.magiaborras.spells.prop.SpellParticlesBuilder;
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
                    new SpellParticlesBuilder().setRadius(buf.readDouble()).setvMar(buf.readDouble()).sethMar(buf.readDouble()).setType(SpellParticles.SpellParticleType.values()[buf.readInt()]).setColorStart(buf.readVector3f()).setColorEnd(buf.readVector3f()).setSize(buf.readFloat()).setBorderOnly(buf.readBoolean()).build()
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


            buf.writeDouble(value.particles.getRadius());
            buf.writeDouble(value.particles.getVMar());
            buf.writeDouble(value.particles.getHMar());
            buf.writeInt(value.particles.getType().ordinal());
            buf.writeVector3f(value.particles.getColorStart());
            buf.writeVector3f(value.particles.getColorEnd());
            buf.writeFloat(value.particles.getSize());
            buf.writeBoolean(value.particles.isBorderOnly());
        }
    };

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
