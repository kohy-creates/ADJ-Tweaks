package xyz.kohara.adjcore.mixins.client;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.kohara.adjcore.misc.events.ItemIsLockedRenderCheckEvent;

@Mixin(ItemStack.class)
public class ItemStackItemNameMixin {

    @Inject(method = "getHoverName", at = @At(value = "HEAD"), cancellable = true)
    private void obfuscateNameIfNeeded(CallbackInfoReturnable<Component> cir) {

        Minecraft mc = Minecraft.getInstance();
        ItemStack itemStack = (ItemStack) (Object) this;

        if (mc.level != null) {
            ItemIsLockedRenderCheckEvent eventHook = new ItemIsLockedRenderCheckEvent(
                    itemStack,
                    mc.player
            );
            if (MinecraftForge.EVENT_BUS.post(eventHook)) {
//                System.out.println("obfuscating");
                cir.setReturnValue(itemStack.getItem().getName(itemStack).copy().withStyle(Style.EMPTY.withObfuscated(true).withColor(ChatFormatting.GRAY)));
            }
        }
    }
}
