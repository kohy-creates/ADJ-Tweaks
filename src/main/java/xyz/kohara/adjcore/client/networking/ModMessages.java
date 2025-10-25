package xyz.kohara.adjcore.client.networking;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import xyz.kohara.adjcore.ADJCore;
import xyz.kohara.adjcore.client.networking.packet.ChangeLoadOutC2SPacket;
import xyz.kohara.adjcore.client.networking.packet.DamageIndicatorS2CPacket;
import xyz.kohara.adjcore.client.networking.packet.EmiReloadS2CPacket;
import xyz.kohara.adjcore.client.networking.packet.ShowRainbowMessageS2CPacket;

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

        net.messageBuilder(ShowRainbowMessageS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(ShowRainbowMessageS2CPacket::new)
                .encoder(ShowRainbowMessageS2CPacket::toBytes)
                .consumerMainThread(ShowRainbowMessageS2CPacket::handle)
                .add();

        net.messageBuilder(ChangeLoadOutC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(ChangeLoadOutC2SPacket::new)
                .encoder(ChangeLoadOutC2SPacket::toBytes)
                .consumerMainThread(ChangeLoadOutC2SPacket::handle)
                .add();

        net.messageBuilder(DamageIndicatorS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(DamageIndicatorS2CPacket::new)
                .encoder(DamageIndicatorS2CPacket::toBytes)
                .consumerMainThread(DamageIndicatorS2CPacket::handle)
                .add();
    }

    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }
}
