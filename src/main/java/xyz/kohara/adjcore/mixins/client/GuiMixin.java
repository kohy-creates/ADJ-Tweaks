package xyz.kohara.adjcore.mixins.client;

import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.MobEffectTextureManager;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.StringUtil;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraftforge.client.extensions.common.IClientMobEffectExtensions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.List;

@Mixin(Gui.class)
public class GuiMixin {

    @Inject(method = "renderEffects", at = @At("HEAD"), cancellable = true)
    public void renderEffects(GuiGraphics guiGraphics, CallbackInfo ci) {
        ci.cancel();
        Gui gui = (Gui) (Object) this;
        Collection<MobEffectInstance> collection = gui.minecraft.player.getActiveEffects();
        if (!collection.isEmpty()) {
            if (gui.minecraft.screen instanceof EffectRenderingInventoryScreen<?> effectrenderinginventoryscreen && effectrenderinginventoryscreen.canSeeEffects()) {
                return;
            }

            RenderSystem.enableBlend();
            MobEffectTextureManager mobeffecttexturemanager = gui.minecraft.getMobEffectTextures();
            List<Runnable> list = Lists.newArrayListWithExpectedSize(collection.size());

            int index = 0;
            int iconSize = 25;
            int maxPerRow = 5;

            for (MobEffectInstance mobeffectinstance : Ordering.natural().reverse().sortedCopy(collection)) {
                MobEffect mobeffect = mobeffectinstance.getEffect();
                IClientMobEffectExtensions renderer = IClientMobEffectExtensions.of(mobeffectinstance);

                if (renderer.isVisibleInGui(mobeffectinstance) && mobeffectinstance.showIcon()) {
                    int row = index / maxPerRow;
                    int col = index % maxPerRow;

                    int x = gui.screenWidth - (iconSize * (col + 1));
                    int y = 1 + row * 36;
                    if (gui.minecraft.isDemo()) {
                        y += 15;
                    }

                    index++;

                    float f = 1.0F;
                    if (mobeffectinstance.isAmbient()) {
                        guiGraphics.blit(AbstractContainerScreen.INVENTORY_LOCATION, x, y, 165, 166, 24, 24);
                    } else {
                        guiGraphics.blit(AbstractContainerScreen.INVENTORY_LOCATION, x, y, 141, 166, 24, 24);
                        if (mobeffectinstance.endsWithin(200)) {
                            int k = mobeffectinstance.getDuration();
                            int l = 10 - k / 20;
                            f = Mth.clamp(k / 10.0F / 5.0F * 0.5F, 0.0F, 0.5F) + Mth.cos(k * (float) Math.PI / 5.0F) * Mth.clamp(l / 10.0F * 0.25F, 0.0F, 0.25F);
                        }

                        Component duration = MobEffectUtil.formatDuration(mobeffectinstance, 1.0F);
                        Font font = gui.getFont();
                        float scale = 0.75F;
                        int textWidth = font.width(duration);
                        float textX = x + (24 - textWidth * scale) / 2.0F;
                        float textY = y + 24 + 2;

                        guiGraphics.pose().pushPose();
                        guiGraphics.pose().translate(textX, textY, 0);
                        guiGraphics.pose().scale(scale, scale, 1.0F);
                        guiGraphics.drawString(font, duration, 0, 0, 16777215, false);
                        guiGraphics.pose().popPose();
                    }

                    if (!renderer.renderGuiIcon(mobeffectinstance, gui, guiGraphics, x, y, 0.0F, f)) {
                        TextureAtlasSprite sprite = mobeffecttexturemanager.get(mobeffect);
                        float fFinal = f;
                        int xFinal = x;
                        int yFinal = y;
                        list.add(() -> {
                            guiGraphics.setColor(1.0F, 1.0F, 1.0F, fFinal);
                            guiGraphics.blit(xFinal + 3, yFinal + 3, 0, 18, 18, sprite);
                            guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
                        });
                    }
                }
            }
            list.forEach(Runnable::run);
        }
    }
}
