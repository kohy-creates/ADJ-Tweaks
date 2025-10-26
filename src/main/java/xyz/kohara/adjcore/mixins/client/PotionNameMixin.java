package xyz.kohara.adjcore.mixins.client;

import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.item.*;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PotionItem.class)
public class PotionNameMixin extends Item {

    public PotionNameMixin(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {

        PotionItem inst = (PotionItem) (Object) this;
        Component name = super.getName(stack);

        String translated = I18n.get(((TranslatableContents) name.getContents()).getKey(), ((TranslatableContents) name.getContents()).getArgs());
        String[] split = translated.replace("Lingering ", "")
                .replace("Splash ", "")
                .split(" of ");

        String type = null;
        TextColor color = null;
        if (inst instanceof SplashPotionItem) {
            type = "Throwable";
            color = TextColor.fromLegacyFormat(ChatFormatting.GOLD);
        }
        else if (inst instanceof LingeringPotionItem) {
            type = "Lingering";
            color = TextColor.fromLegacyFormat(ChatFormatting.LIGHT_PURPLE);
        }


        MutableComponent newName = Component.empty();

        if (split.length > 1) {
            newName.append(split[1])
                    .append(" ")
                    .append(split[0]);
        }
        else {
            newName.append(split[0]);
        }

        if (type != null) {
            newName
                    .append(" (")
                    .append(Component.literal(type).withStyle(Style.EMPTY.withColor(color)))
                    .append(")");
        }

        return newName;
//        return super.getName(stack);
    }
}
