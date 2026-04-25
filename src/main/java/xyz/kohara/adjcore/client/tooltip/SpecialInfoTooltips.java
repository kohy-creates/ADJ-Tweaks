package xyz.kohara.adjcore.client.tooltip;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;
import xyz.kohara.adjcore.ADJCore;
import xyz.kohara.adjcore.ADJData;

import java.util.*;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = ADJCore.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SpecialInfoTooltips {

    private static final Set<Item> MATERIAL_ITEMS = new LinkedHashSet<>();

    private static void createIngredientCache() {
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

    private static final Map<String, String> defaultTraitTexts = Map.of(
            "can_be_placed", "Can be placed",
            "material", "Material",
            "equipable", "Equipable",
            "consumable", "Consumable"
    );

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void editTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        Item item = stack.getItem();

        List<String> traits = new ArrayList<>();

        // Defaults
        if (item.isEdible()) {
            traits.add(defaultTraitTexts.get("consumable"));
        }

        if (item instanceof BlockItem) {
            traits.add(defaultTraitTexts.get("can_be_placed"));
        }
        EquipmentSlot slot = LivingEntity.getEquipmentSlotForItem(stack);
        boolean isCurio = !CuriosApi.getCuriosHelper().getCurioTags(item).isEmpty();
        if (item instanceof Equipable || (slot != EquipmentSlot.MAINHAND && slot != EquipmentSlot.OFFHAND) || isCurio) {
            traits.add(defaultTraitTexts.get("equipable"));
        }

        if (MATERIAL_ITEMS.isEmpty()) createIngredientCache();
        if (MATERIAL_ITEMS.contains(item)) {
            traits.add(defaultTraitTexts.get("material"));
        }

        // Overrides for defaults
        for (String defaultTrait : ADJData.TooltipInfoOverrides.defaultTraits) {
            ADJData.TooltipInfoOverrides.OverrideEntry oE = ADJData.TooltipInfoOverrides.getDefaultOverrideFor(item, defaultTrait);

            if (oE == null) continue;

            if (oE.shouldRemove()) {
                traits.remove(defaultTraitTexts.get(defaultTrait));
            } else {
                traits.add(defaultTraitTexts.get(defaultTrait));
            }
        }

        // Custom overrides
        ADJData.TooltipInfoOverrides.OverrideEntry oE = ADJData.TooltipInfoOverrides.getCustomOverrideFor(item);
        if (oE != null) {
            if (oE.names() != null) {
                traits.addAll(oE.names());
            }
        }

        if (!traits.isEmpty()) {
            List<Component> lines = new ArrayList<>();
            MutableComponent infoLine = Component.empty();

            int count = 0;
            int total = traits.size();

            for (int i = 0; i < total; i++) {
                String trait = traits.get(i);
                count++;

                infoLine.append(Component.literal(ADJCore.toSmallUnicode(trait))
                        .withStyle(Style.EMPTY.withBold(false).withColor(ChatFormatting.GRAY)));

                boolean isLastInLine = count == 3;
                boolean isLastOverall = i == total - 1;

                if (!isLastInLine && !isLastOverall) {
                    infoLine.append(Component.literal(" | ")
                            .withStyle(Style.EMPTY.withColor(ChatFormatting.DARK_GRAY).withBold(true)));
                }

                if (isLastInLine || isLastOverall) {
                    lines.add(infoLine);
                    infoLine = Component.empty();
                    count = 0;
                }
            }

            if (event.getToolTip().isEmpty()) return;

            for (int i = lines.size() - 1; i >= 0; i--) {
                event.getToolTip().add(1, lines.get(i));
            }
        }
    }
}
