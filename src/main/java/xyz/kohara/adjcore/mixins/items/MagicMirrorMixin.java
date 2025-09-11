package xyz.kohara.adjcore.mixins.items;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelData;
import org.confluence.mod.item.common.MagicMirror;
import org.confluence.mod.misc.ModSoundEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = MagicMirror.class, remap = true)
public class MagicMirrorMixin {

    @Inject(method = "finishUsingItem", at = @At("HEAD"), cancellable = true)
    public void finishUsingItem(ItemStack itemStack, Level level, LivingEntity living, CallbackInfoReturnable<ItemStack> cir) {
        cir.cancel();

        if (level.isClientSide) {
            Minecraft.getInstance().gameRenderer.displayItemActivation(itemStack);
        } else if (living instanceof ServerPlayer) {
            ServerPlayer serverPlayer = (ServerPlayer) living;
            BlockPos respawnPos = serverPlayer.getRespawnPosition();
            if (respawnPos == null) {
                ServerLevel serverLevel = serverPlayer.server.getLevel(Level.OVERWORLD);
                if (serverLevel == null) {
                    serverLevel = (ServerLevel) level;
                }

                LevelData data = serverLevel.getLevelData();
                serverPlayer.teleportTo(serverLevel, data.getXSpawn() + 0.5, data.getYSpawn(), data.getZSpawn(), 0F, 0F);
            } else {
                ResourceKey<Level> respawnLevel = serverPlayer.getRespawnDimension();
                float respawnAngle = serverPlayer.getRespawnAngle();
                serverPlayer.teleportTo(serverPlayer.server.getLevel(respawnLevel), respawnPos.getX(), respawnPos.getY(), respawnPos.getZ(), respawnAngle, 0F);
            }

            serverPlayer.getCooldowns().addCooldown((MagicMirror) (Object) this, 30);
        }

        living.playSound(ModSoundEvents.TRANSMISSION.get());
        cir.setReturnValue(itemStack);
    }
}
