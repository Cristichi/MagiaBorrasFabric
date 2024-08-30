package es.cristichi.mod.magiaborras.floo.fireplace.net;

import es.cristichi.mod.magiaborras.MagiaBorras;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.BlockPos;

/**
This Payload is for when the client selectes the Fireplace to TP to in the Floo Network menu.

WIP Probably leaving the BlockPos as the only argument but still designing it
*/
public record FlooFireTPPayload(BlockPos objective) implements CustomPayload {
    public static final Id<FlooFireTPPayload> ID = new Id<>(MagiaBorras.NET_FLOO_TP_ID);
    public static final PacketCodec<RegistryByteBuf, FlooFireTPPayload> CODEC
            = PacketCodec.tuple(BlockPos.PACKET_CODEC, FlooFireTPPayload::objective,
            FlooFireTPPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
