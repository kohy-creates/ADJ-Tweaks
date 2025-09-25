package xyz.kohara.adjcore.networking.packet;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import xyz.kohara.adjcore.entity.PlayerLoadouts;

import java.util.function.Supplier;

public class ChangeLoadOutC2SPacket {

    private final int newLoadOutNumber;

    public ChangeLoadOutC2SPacket(int loadoutNumber) {
        this.newLoadOutNumber = loadoutNumber;
    }

    public ChangeLoadOutC2SPacket(FriendlyByteBuf buf) {
        this.newLoadOutNumber = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(newLoadOutNumber);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                PlayerLoadouts.changeLoadout(player, newLoadOutNumber);
            }
        });
        return true;
    }
}
