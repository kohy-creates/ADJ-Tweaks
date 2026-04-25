package xyz.kohara.adjcore.mixins.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.kohara.adjcore.misc.events.PlayerTitleEvent;

import java.util.ArrayList;
import java.util.List;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin {

    @Unique
    private Component adj$PlayerTitle;

    @Unique
    private static final int VISIBLE_CHARS = 15;
    @Unique
    private static final int SCROLL_TICKS = 5;

    @Shadow
    public abstract Font getFont();

    @Unique
    private static FormattedCharSequence adj$loopScroll(
            Component component,
            int tickCount
    ) {
        FormattedCharSequence sequence = component.getVisualOrderText();
        List<FormattedCharSequence> chars = new ArrayList<>();
        sequence.accept((index, style, codePoint) -> {
            chars.add(FormattedCharSequence.forward(
                    Character.toString(codePoint),
                    style
            ));
            return true;
        });
        if (chars.size() <= VISIBLE_CHARS) {
            return sequence;
        }
        int offset = (tickCount / SCROLL_TICKS) % chars.size();
        List<FormattedCharSequence> window = new ArrayList<>();
        for (int i = 0; i < VISIBLE_CHARS; i++) {
            window.add(chars.get((offset + i) % chars.size()));
        }
        return FormattedCharSequence.composite(window);
    }

    @Inject(
            method = "renderNameTag",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V",
                    shift = At.Shift.AFTER
            )
    )
    private void getPlayerTitle(
            Entity entity,
            Component displayName,
            PoseStack matrixStack,
            MultiBufferSource buffer,
            int packedLight,
            CallbackInfo ci
    ) {
        if (entity instanceof Player player) {
            adj$PlayerTitle = null;

            PlayerTitleEvent event = new PlayerTitleEvent((LocalPlayer) player);
            MinecraftForge.EVENT_BUS.post(event);
            Component title = event.getTitle();
            if (title != null) {
                adj$PlayerTitle = title;
                for (int i = 0; i < title.getString().length() / 3; i++) {
                    adj$PlayerTitle = adj$PlayerTitle.copy().append(" ");
                }
                matrixStack.translate(0.0F, 0.15f, 0.0F);
            }
        }
    }

    @Inject(
            method = "renderNameTag",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/vertex/PoseStack;popPose()V",
                    shift = At.Shift.BEFORE
            )
    )
    private void addPlayerTitle(
            Entity entity,
            Component displayName,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight,
            CallbackInfo ci
    ) {
        if (entity instanceof Player player && adj$PlayerTitle != null) {

            float nameTagYOffset = "deadmau5".equals(displayName.getString()) ? -10f : 0f;
            Font font = this.getFont();
            FormattedCharSequence title = adj$loopScroll(adj$PlayerTitle, player.tickCount);

            float yOffset = nameTagYOffset + 10F;
            float xOffset = (float) (-font.width(title)) / 2.0F;
            boolean seeThrough = !entity.isDiscrete();
            float backgroundOpacity = Minecraft.getInstance().options.getBackgroundOpacity(0.25F);
            int backgroundColor = (int) (backgroundOpacity * 255.0F) << 24;

            font.drawInBatch(title, xOffset, yOffset, 553648127, false, poseStack.last().pose(), buffer, seeThrough ? Font.DisplayMode.SEE_THROUGH : Font.DisplayMode.NORMAL, backgroundColor, packedLight);
            if (seeThrough) {
                font.drawInBatch(title, xOffset, yOffset, -1, false, poseStack.last().pose(), buffer, Font.DisplayMode.NORMAL, 0, packedLight);
            }
        }
    }
}
