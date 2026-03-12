package xyz.kohara.adjcore.mixins.compat.ars.emi;

import com.hollingsworth.arsnouveau.api.enchanting_apparatus.EnchantingApparatusRecipe;
import com.hollingsworth.arsnouveau.client.jei.EnchantingApparatusRecipeCategory;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import xyz.kohara.adjcore.ADJCore;

@Mixin(value = EnchantingApparatusRecipeCategory.class, remap = false)
public class EnchantingApparatusRecipeCategoryMixin {

    /**
     * @author me
     * @reason bruh
     */
    @Overwrite
    public void draw(EnchantingApparatusRecipe recipe, @NotNull IRecipeSlotsView slotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        ADJCore.expandArsEMIGuis(guiGraphics, recipe.pedestalItems.size(), recipe.sourceCost);
    }
}
