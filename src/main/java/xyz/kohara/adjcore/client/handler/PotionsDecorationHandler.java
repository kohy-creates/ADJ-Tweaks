package xyz.kohara.adjcore.client.handler;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.IItemDecorator;
import net.minecraftforge.client.event.RegisterItemDecorationsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xyz.kohara.adjcore.ADJCore;

import java.util.List;

// Adapted from https://github.com/Fuzss/distinguishedpotions/blob/main/1.20.1/Common/src/main/java/fuzs/distinguishedpotions/client/DistinguishedPotionsClient.java#L45
@Mod.EventBusSubscriber(modid = ADJCore.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class PotionsDecorationHandler {
    private static final ChatFormatting[] POTION_AMPLIFIER_COLORS = new ChatFormatting[]{
            ChatFormatting.AQUA,
            ChatFormatting.BLUE,
            ChatFormatting.DARK_BLUE,
            ChatFormatting.LIGHT_PURPLE,
            ChatFormatting.DARK_PURPLE,
            ChatFormatting.RED,
            ChatFormatting.WHITE
    };

    public static boolean renderPotionDecorations(GuiGraphics guiGraphics, ItemStack stack, int itemPosX, int itemPosY) {
        List<MobEffectInstance> mobEffects = PotionUtils.getMobEffects(stack);
        if (mobEffects.isEmpty()) return false;

        int dotCount = mobEffects.stream()
                .mapToInt(effect -> effect.getAmplifier() + 1)
                .sum();
        if (dotCount > 0) {
            int startX = itemPosX + 3;
            int startY = itemPosY + 13;
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(0, 0, 200);

            guiGraphics.fill(startX, startY, startX + 11, startY + 2, 0xFF000000);
            for (int i = 0; i < Math.min(4, dotCount); i++) {

                int colorIndex = Math.min((dotCount - i - 1) / 4, POTION_AMPLIFIER_COLORS.length - 1);
                Integer colorObj = POTION_AMPLIFIER_COLORS[colorIndex].getColor();
                int color = (colorObj != null ? colorObj : 0xFFFFFF) | 0xFF000000;

                guiGraphics.fill(startX + 3 * i, startY, startX + 3 * i + 2, startY + 2, color | 0xFF000000);
            }
            guiGraphics.pose().popPose();
            return true;
        } else {
            return false;
        }
    }

    public static void renderPotionStackSize(GuiGraphics guiGraphics, Font font, ItemStack stack, int itemPosX, int itemPosY) {
        if (stack.getCount() != 1) {
            PoseStack poseStack = guiGraphics.pose();
            poseStack.pushPose();
            String string = String.valueOf(stack.getCount());
            poseStack.translate(0.0, 0.0, 200.0F);
            guiGraphics.drawString(font, string, itemPosX + 19 - 2 - font.width(string), itemPosY + 6 + 3, 0xFFFFFF, true);
            poseStack.popPose();
        }
    }

    @SubscribeEvent
    public static void onRegisterItemDecorator(RegisterItemDecorationsEvent event) {
        IItemDecorator decorator = (guiGraphics, font, stack, x, y) -> {
            if (renderPotionDecorations(guiGraphics, stack, x, y)) {
                renderPotionStackSize(guiGraphics, font, stack, x, y);
                return true;
            }
            return false;
        };
        List<Item> potions = List.of(
                Items.POTION,
                Items.LINGERING_POTION,
                Items.TIPPED_ARROW,
                Items.SPLASH_POTION
        );
        potions.forEach(potion -> {
            event.register(potion, decorator);
        });
    }
}
