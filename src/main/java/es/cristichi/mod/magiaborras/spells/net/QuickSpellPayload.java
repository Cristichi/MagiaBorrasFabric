package es.cristichi.mod.magiaborras.spells.net;

import es.cristichi.mod.magiaborras.MagiaBorras;
import es.cristichi.mod.magiaborras.spells.Spell;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record QuickSpellPayload(Spell spell) implements CustomPayload {
    public static final Id<QuickSpellPayload> ID = new Id<>(MagiaBorras.NET_QUICK_SPELL_ID);
    public static final PacketCodec<RegistryByteBuf, QuickSpellPayload> CODEC
            = PacketCodec.tuple(Spell.PACKET_CODEC, QuickSpellPayload::spell, QuickSpellPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
