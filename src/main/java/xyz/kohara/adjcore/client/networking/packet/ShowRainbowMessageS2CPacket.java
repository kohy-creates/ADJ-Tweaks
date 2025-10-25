package xyz.kohara.adjcore.client.networking.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ShowRainbowMessageS2CPacket {

    private final Component message;

    public ShowRainbowMessageS2CPacket(Component loadoutNumber) {
        this.message = loadoutNumber;
    }

    public ShowRainbowMessageS2CPacket(FriendlyByteBuf buf) {
        this.message = buf.readComponent();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeComponent(message);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            mc.gui.setOverlayMessage(this.message, true);
            mc.getNarrator().sayNow(this.message);
        });
        return true;
    }
}
