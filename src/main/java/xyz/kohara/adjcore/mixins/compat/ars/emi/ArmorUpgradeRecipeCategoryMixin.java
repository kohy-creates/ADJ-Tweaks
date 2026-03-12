package xyz.kohara.adjcore.mixins.compat.ars.emi;

import com.hollingsworth.arsnouveau.api.enchanting_apparatus.ArmorUpgradeRecipe;
import com.hollingsworth.arsnouveau.client.jei.ArmorUpgradeRecipeCategory;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import xyz.kohara.adjcore.ADJCore;

@Mixin(value = ArmorUpgradeRecipeCategory.class, remap = false)
public class ArmorUpgradeRecipeCategoryMixin {

    /**
     * @author me
     * @reason bruh
     */
    @Overwrite
    public void draw(ArmorUpgradeRecipe recipe, @NotNull IRecipeSlotsView slotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        ADJCore.expandArsEMIGuis(guiGraphics, recipe.pedestalItems.size(), recipe.sourceCost);
    }
}
