package es.cristichi.mod.magiaborras.perdata;

import es.cristichi.mod.magiaborras.MagiaBorras;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record PlayerDataSyncPayload(String unlocked) implements CustomPayload {
    public static final String DELIM = ";";
    public static final Id<PlayerDataSyncPayload> ID = new Id<>(MagiaBorras.NET_PLAYER_DATA_SYNC_ID);
    public static final PacketCodec<RegistryByteBuf, PlayerDataSyncPayload> CODEC
            = PacketCodec.tuple(PacketCodecs.STRING, PlayerDataSyncPayload::unlocked, PlayerDataSyncPayload::new);

    public PlayerDataSyncPayload(String[] data){
        this(String.join(PlayerDataSyncPayload.DELIM, data));
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
