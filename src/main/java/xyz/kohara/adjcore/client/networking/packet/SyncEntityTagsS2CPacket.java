package xyz.kohara.adjcore.client.networking.packet;

import dev.emi.emi.runtime.EmiReloadManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public class SyncEntityTagsS2CPacket {
    private final int entityId;
    private final Set<String> tags;

    public SyncEntityTagsS2CPacket(int entityId, Set<String> tags) {
        this.entityId = entityId;
        this.tags = tags;
    }

    public SyncEntityTagsS2CPacket(FriendlyByteBuf buf) {
        this.entityId = buf.readInt();
        this.tags = new HashSet<>();
        int size = buf.readInt();
        for (int i = 0; i < size; i++) {
            this.tags.add(buf.readUtf(32767));  // read each string
        }
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(entityId);
        buf.writeInt(tags.size());
        for (String tag : tags) {
            buf.writeUtf(tag);
        }
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ClientLevel level = Minecraft.getInstance().level;
            if (level == null) return;

            Entity entity = level.getEntity(entityId);
            if (entity != null) {
                entity.getPersistentData().putString("adjcore_synced_tags",
                        String.join(";", tags));
            }
        });
        context.setPacketHandled(true);
        return true;
    }
}
