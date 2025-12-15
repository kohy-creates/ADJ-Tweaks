package xyz.kohara.adjcore.mixins.client;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.worldselection.WorldSelectionList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import xyz.kohara.adjcore.client.WorldIconRenderer;

@Mixin(WorldSelectionList.WorldListEntry.class)
public class WorldListEntryMixin {

    @Redirect(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIFFIIII)V",
                    ordinal = 0
            )
    )
    private void renderWorldIconAndBorder(
            GuiGraphics instance,
            ResourceLocation atlasLocation,
            int x, int y,
            float uOffset,
            float vOffset,
            int width,
            int height,
            int textureWidth,
            int textureHeight
    ) {

        WorldSelectionList.WorldListEntry entry = (WorldSelectionList.WorldListEntry) (Object) this;

        ResourceLocation icon = WorldIconRenderer.getIcon(WorldIconRenderer.getChapter(entry));
        instance.blit(icon, x, y, 0.0F, 0.0F, 32, 32, 32, 32);

        ResourceLocation border = WorldIconRenderer.getBorder(WorldIconRenderer.isCompleted(entry));
        instance.blit(border, x - 2, y - 2, 0.0F, 0.0F, 64, 64, 64, 64);

    }

    @Redirect(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Ljava/lang/String;IIIZ)I",
                    ordinal = 0
            )
    )
    private int renderCustomWorldName(GuiGraphics instance, Font font, String text, int x, int y, int color, boolean dropShadow) {
        int boxWidth = 180;
        int boxCenterX = x + (boxWidth / 2);
        int textX = boxCenterX - (font.width(text) / 2);

        return instance.drawString(font, text, textX, y, 16777215, false);
    }

    @Redirect(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Ljava/lang/String;IIIZ)I",
                    ordinal = 1
            )
    )
    private int renderCustomWorldID(GuiGraphics instance, Font font, String text, int x, int y, int color, boolean dropShadow) {
        return instance.drawString(font, text, x + 5, y, 8421504, false);
    }

    @Redirect(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;IIIZ)I",
                    ordinal = 0
            )
    )
    private int renderCustomWorldInfo(GuiGraphics instance, Font font, Component text, int x, int y, int color, boolean dropShadow) {
        return instance.drawString(font, text, x + 5, y, 8421504, false);
    }
}
