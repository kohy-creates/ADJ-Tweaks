package xyz.kohara.adjcore.client.tooltip;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.EquipableCarvedPumpkinBlock;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xyz.kohara.adjcore.ADJCore;

import java.util.*;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = ADJCore.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SpecialInfoTooltips {

    private static Set<Item> MATERIAL_ITEMS = new HashSet<>();

    public static void createIngredientCache() {
        if (MATERIAL_ITEMS.isEmpty()) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.getConnection() != null) {

                mc.getConnection().getRecipeManager().getRecipes().forEach(recipe ->
                        recipe.getIngredients().forEach(ing -> {
                            for (ItemStack ex : ing.getItems()) {
                                MATERIAL_ITEMS.add(ex.getItem());
                            }
                        })
                );
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void editTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        Item item = stack.getItem();

        List<String> traits = new ArrayList<>();

        if (item instanceof BlockItem) {
            traits.add("Can be placed");
        }
        if (item instanceof Equipable || item.getEquipmentSlot()) {
            traits.add("Equipable");
        }

        if (MATERIAL_ITEMS.isEmpty()) createIngredientCache();
        if (MATERIAL_ITEMS.contains(item)) {
            traits.add("Material");
        }

        if (!traits.isEmpty()) {

            MutableComponent infoLine = Component.empty();
            int i = 0;
            for (String trait : traits) {
                i++;

                infoLine.append(Component.literal(ADJCore.toSmallUnicode(trait)).withStyle(Style.EMPTY
                        .withBold(false)
                        .withColor(ChatFormatting.GRAY)
                ));


                if (i != traits.toArray().length) {
                    infoLine.append(Component.literal(" | ") .withStyle(Style.EMPTY
                            .withColor(ChatFormatting.DARK_GRAY)
                            .withBold(true)
                    ));
                }
            }

            event.getToolTip().add(1, infoLine);

        }
    }
}
