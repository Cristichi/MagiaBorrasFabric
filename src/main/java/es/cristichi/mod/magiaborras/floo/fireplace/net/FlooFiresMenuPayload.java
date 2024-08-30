package es.cristichi.mod.magiaborras.floo.fireplace.net;

import es.cristichi.mod.magiaborras.MagiaBorras;
import net.minecraft.network.packet.CustomPayload;

/**
This Payload is for when the server wants the player to open a screen with the fireplaces

WIP we need to change the values of this record to either an NBTComponent or a String
to codify the list of Fireplaces, their locations, etc.
 */
public record FlooFiresMenuPayload() implements CustomPayload {
    public static final Id<FlooFiresMenuPayload> ID = new Id<>(MagiaBorras.NET_FLOO_MENU_ID);
//    public static final PacketCodec<RegistryByteBuf, FlooFiresMenuPayload> CODEC
//            = PacketCodec.tuple(BlockPos.PACKET_CODEC, FlooFiresMenuPayload::block,
//                PacketCodecs.STRING, FlooFiresMenuPayload::name,
//                PacketCodecs.BOOL, FlooFiresMenuPayload::registered,
//                FlooFiresMenuPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
