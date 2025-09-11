package xyz.kohara.adjcore.mixins.entity;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.CombatTracker;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.util.ITeleporter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.kohara.adjcore.ADJCore;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {
    @Inject(
            method = "changeDimension",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;send(Lnet/minecraft/network/protocol/Packet;)V",
                    ordinal = 0
            ),
            cancellable = true
    )
    public void hasSeenCredits(ServerLevel arg, ITeleporter teleporter, CallbackInfoReturnable<Entity> cir) {

        ServerPlayer player = (ServerPlayer) (Object) this;
        player.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.WIN_GAME, 0.0F));

        cir.setReturnValue(player);
    }

    @Redirect(
            method = "die",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/damagesource/CombatTracker;getDeathMessage()Lnet/minecraft/network/chat/Component;"
            )
    )
    private Component changeDeathMessageColor(CombatTracker instance) {
        return ADJCore.formatDeathMessage(instance.getDeathMessage());
    }

    @Redirect(
            method = "setRespawnPosition",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/network/chat/Component;translatable(Ljava/lang/String;)Lnet/minecraft/network/chat/MutableComponent;")
    )
    private MutableComponent fancifyRespawnPosMessage(String key) {
        return Component.empty()
                .append(Component.literal("p").withStyle(Style.EMPTY.withFont(ResourceLocation.parse("adjcore:icons"))))
                .append(Component.literal(" "))
                .append(Component.translatable("block.minecraft.set_spawn").withStyle(Style.EMPTY.withColor(TextColor.parseColor("#32ff82"))));
    }

    @Redirect(
            method = "startSleepInBed",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/network/chat/Component;translatable(Ljava/lang/String;)Lnet/minecraft/network/chat/MutableComponent;")
    )
    private MutableComponent fancifyBedMessage(String key) {
        return Component.empty()
                .append(Component.literal("s").withStyle(Style.EMPTY.withFont(ResourceLocation.parse("adjcore:icons"))))
                .append(Component.literal(" "))
                .append(Component.translatable("sleep.not_possible").withStyle(Style.EMPTY.withColor(TextColor.parseColor("#FF1919"))));
    }
}
