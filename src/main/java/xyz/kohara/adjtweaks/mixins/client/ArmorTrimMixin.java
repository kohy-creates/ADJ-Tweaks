// From Better Trim Tooltips mod
// https://github.com/Andrew6rant/Better-Trim-Tooltips
package xyz.kohara.adjtweaks.mixins.client;

import net.minecraft.ChatFormatting;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Optional;

import static net.minecraft.world.item.armortrim.ArmorTrim.getTrim;

@Mixin(ArmorTrim.class)
public class ArmorTrimMixin {

    @Inject(at = @At("TAIL"), method = "appendUpgradeHoverText")
    private static void appendTooltip(ItemStack armor, RegistryAccess registryAccess, List<Component> tooltip, CallbackInfo ci) {
        Optional<ArmorTrim> optional = getTrim(registryAccess, armor);
        if (optional.isPresent()) {
            // remove all the existing trim tooltips
            // using removeIf to avoid ConcurrentModificationException
            tooltip.removeIf(text -> {
                ComponentContents textContent = text.getContents();
                if (textContent instanceof TranslatableContents) {
                    return ((TranslatableContents) textContent).getKey().equals("item.minecraft.smithing_template.upgrade");
                }
                if (text.getSiblings().size() == 1) {
                    ComponentContents siblingContext = text.getSiblings().get(0).getContents();
                    if (siblingContext instanceof TranslatableContents) {
                        return ((TranslatableContents) siblingContext).getKey().contains("trim");
                    }
                }
                return false;
            });

            ArmorTrim armorTrim = optional.get();
            aDJTweaks$appendTrimTooltip(tooltip, armorTrim);

        }
    }

    @Unique
    private static void aDJTweaks$appendTrimTooltip(List<Component> tooltip, ArmorTrim armorTrim) {
        Style materialStyle = armorTrim.material().value().description().getStyle();
        tooltip.add(Component.literal("")
                .append(armorTrim.pattern().value().description()).withStyle(ChatFormatting.GRAY)
                .append(CommonComponents.space()
                        .append(Component.literal("(").setStyle(materialStyle))
                        .append(armorTrim.material().value().description()))
                .append(Component.literal(")").setStyle(materialStyle)));
    }
}