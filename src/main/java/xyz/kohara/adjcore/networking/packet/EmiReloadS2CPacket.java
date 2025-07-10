package xyz.kohara.adjcore.networking.packet;

import dev.emi.emi.runtime.EmiReloadManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class EmiReloadS2CPacket {

    public EmiReloadS2CPacket() {
    }

    public EmiReloadS2CPacket(FriendlyByteBuf buf) {
    }

    public void toBytes(FriendlyByteBuf buf) {
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(EmiReloadManager::reload);
        return true;
    }
}
