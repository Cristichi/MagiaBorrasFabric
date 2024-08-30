package es.cristichi.mod.magiaborras.floo.fireplace.net;

import es.cristichi.mod.magiaborras.MagiaBorras;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.BlockPos;

/**
Used to both tell the client to open a menu to rename (and register/unregister) the Fireplace and also when the client
is done renaming it to send the updated information back (if it is different).
 */
public record FlooFireRenamePayload(BlockPos block, String name, boolean registered) implements CustomPayload {
    public static final CustomPayload.Id<FlooFireRenamePayload> ID = new CustomPayload.Id<>(MagiaBorras.NET_FLOO_RENAME_ID);
    public static final PacketCodec<RegistryByteBuf, FlooFireRenamePayload> CODEC
            = PacketCodec.tuple(BlockPos.PACKET_CODEC, FlooFireRenamePayload::block,
                PacketCodecs.STRING, FlooFireRenamePayload::name,
                PacketCodecs.BOOL, FlooFireRenamePayload::registered,
                FlooFireRenamePayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
