package xyz.kohara.adjcore.mixins.client;

import com.google.common.collect.Ordering;
import com.llamalad7.mixinextras.sugar.Local;
import dev.emi.emi.platform.EmiAgnos;
import dev.emi.emi.runtime.EmiDrawContext;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.ScreenEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.minecraft.client.gui.screens.inventory.AbstractContainerScreen.INVENTORY_LOCATION;

@Mixin(EffectRenderingInventoryScreen.class)
public abstract class EffectInventoryScreenMixin {

    @Unique
    private static final ResourceLocation AMBIENT_TEXTURES = ResourceLocation.fromNamespaceAndPath("adjcore", "textures/gui/ambient_effects.png");

    @Inject(method = "renderBackgrounds", at = @At("HEAD"), cancellable = true)
    private void renderBackgrounds(GuiGraphics guiGraphics, int renderX, int yOffset, Iterable<MobEffectInstance> effects, boolean isSmall, CallbackInfo ci) {
        ci.cancel();
        EffectRenderingInventoryScreen<?> effectRenderingInventoryScreen = (EffectRenderingInventoryScreen<?>) (Object) this;
        int i = effectRenderingInventoryScreen.topPos;

        for (MobEffectInstance mobeffectinstance : effects) {
            if (mobeffectinstance.isAmbient()) {
                if (isSmall) {
                    guiGraphics.blit(AMBIENT_TEXTURES, renderX, i, 0, 0, 120, 32);
                } else {
                    guiGraphics.blit(AMBIENT_TEXTURES, renderX, i, 0, 32, 32, 32);
                }
            } else {
                if (isSmall) {
                    guiGraphics.blit(INVENTORY_LOCATION, renderX, i, 0, 166, 120, 32);
                } else {
                    guiGraphics.blit(INVENTORY_LOCATION, renderX, i, 0, 198, 32, 32);
                }
            }

            i += yOffset;
        }
    }
}

