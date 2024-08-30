package es.cristichi.mod.magiaborras.floo.fireplace.packets;

import es.cristichi.mod.magiaborras.MagiaBorras;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.Map;

/**
This Payload is for when the server wants the player to open a screen with the fireplaces

WIP we need to change the values of this record to either an NBTComponent or a String
to codify the list of Fireplaces, their locations, etc.
 */
public record FlooFiresMenuPayload(HashMap<BlockPos, String> fireplaces) implements CustomPayload {
    public static final Id<FlooFiresMenuPayload> ID = new Id<>(MagiaBorras.NET_FLOO_MENU_ID);
    public static final PacketCodec<RegistryByteBuf, FlooFiresMenuPayload> CODEC = new PacketCodec<>() {
        @Override
        public FlooFiresMenuPayload decode(RegistryByteBuf buf) {
            int size = buf.readInt();
            HashMap<BlockPos, String> fireplaces = new HashMap<>(size);
            for (int i = 0; i < size; i++) {
                fireplaces.put(buf.readBlockPos(), buf.readString());
            }
            return new FlooFiresMenuPayload(fireplaces);
        }

        @Override
        public void encode(RegistryByteBuf buf, FlooFiresMenuPayload value) {
            buf.writeInt(value.fireplaces.size());
            for (Map.Entry<BlockPos, String> entry : value.fireplaces.entrySet()) {
                BlockPos k = entry.getKey();
                String v = entry.getValue();
                buf.writeBlockPos(k);
                buf.writeString(v);
            }
        }
    };

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
