package xyz.kohara.adjcore.networking;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import xyz.kohara.adjcore.ADJCore;
import xyz.kohara.adjcore.networking.packet.EmiReloadS2CPacket;

public class ModMessages {
    private static SimpleChannel INSTANCE;

    private static int packedId = 0;

    private static int id() {
        return packedId++;
    }

    public static void register() {
        SimpleChannel net = NetworkRegistry.ChannelBuilder
                .named(ResourceLocation.fromNamespaceAndPath(ADJCore.MOD_ID, "messages"))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();
        INSTANCE = net;

        net.messageBuilder(EmiReloadS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(EmiReloadS2CPacket::new)
                .encoder(EmiReloadS2CPacket::toBytes)
                .consumerMainThread(EmiReloadS2CPacket::handle)
                .add();
    }

    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }
}
