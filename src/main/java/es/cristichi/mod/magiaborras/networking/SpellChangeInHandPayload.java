package es.cristichi.mod.magiaborras.networking;

import es.cristichi.mod.magiaborras.MagiaBorras;
import es.cristichi.mod.magiaborras.spells.Spell;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record SpellChangeInHandPayload(Spell spell) implements CustomPayload {
    public static final CustomPayload.Id<SpellChangeInHandPayload> ID = new CustomPayload.Id<>(MagiaBorras.NET_CHANGE_SPELL_ID);
    public static final PacketCodec<RegistryByteBuf, SpellChangeInHandPayload> CODEC
            = PacketCodec.tuple(Spell.PACKET_CODEC, SpellChangeInHandPayload::spell, SpellChangeInHandPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
