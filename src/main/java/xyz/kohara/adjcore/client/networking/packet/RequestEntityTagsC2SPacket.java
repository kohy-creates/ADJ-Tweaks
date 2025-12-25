package xyz.kohara.adjcore.client.networking.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;
import xyz.kohara.adjcore.client.networking.ADJMessages;

import java.util.Set;
import java.util.function.Supplier;

public class RequestEntityTagsC2SPacket {
    private final int entityId;

    public RequestEntityTagsC2SPacket(int entityId) {
        this.entityId = entityId;
    }

    public RequestEntityTagsC2SPacket(FriendlyByteBuf buf) {
        this.entityId = buf.readInt();
    }

    public static void encode(RequestEntityTagsC2SPacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.entityId);
    }

    public static RequestEntityTagsC2SPacket decode(FriendlyByteBuf buf) {
        return new RequestEntityTagsC2SPacket(buf);
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(entityId);
    }

    public static void handle(RequestEntityTagsC2SPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            ServerLevel level = player.serverLevel();
            Entity entity = level.getEntity(msg.entityId);
            if (entity != null) {
                Set<String> tags = entity.getTags();
                ADJMessages.sendToPlayer(
                        new SyncEntityTagsS2CPacket(entity.getId(), tags),
                        player
                );
            }
        });
        ctx.get().setPacketHandled(true);
    }
}

