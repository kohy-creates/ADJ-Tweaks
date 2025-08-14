package xyz.kohara.adjcore.mixins.music;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.kohara.adjcore.music.JukeboxTracker;

@Mixin(JukeboxBlockEntity.class)
public class JukeboxHandler {

    @Inject(method = "stopPlaying", at = @At(value = "HEAD"))
    private void onStopPlaying(CallbackInfo ci) {
        JukeboxBlockEntity jbe = (JukeboxBlockEntity) (Object) this;
        JukeboxTracker.onJukeboxStop(jbe.getBlockPos());
    }

    @Inject(method = "setItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/JukeboxBlockEntity;startPlaying()V"))
    private void onStartPlaying(int slot, ItemStack stack, CallbackInfo ci) {
        JukeboxBlockEntity jbe = (JukeboxBlockEntity) (Object) this;
        JukeboxTracker.onJukeboxPlay(jbe.getLevel(), Minecraft.getInstance(), jbe.getBlockPos());
    }
}
