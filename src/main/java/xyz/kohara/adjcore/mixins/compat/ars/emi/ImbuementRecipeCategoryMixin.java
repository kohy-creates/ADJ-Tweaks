package xyz.kohara.adjcore.mixins.compat.ars.emi;

import com.hollingsworth.arsnouveau.client.jei.ImbuementRecipeCategory;
import com.hollingsworth.arsnouveau.client.jei.MultiInputCategory;
import com.hollingsworth.arsnouveau.common.crafting.recipes.ImbuementRecipe;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import xyz.kohara.adjcore.ADJCore;

import java.util.function.Function;

@Mixin(value = ImbuementRecipeCategory.class, remap = false)
public abstract class ImbuementRecipeCategoryMixin extends MultiInputCategory<ImbuementRecipe> {

    public ImbuementRecipeCategoryMixin(IGuiHelper helper, Function<ImbuementRecipe, MultiProvider> multiProvider) {
        super(helper, multiProvider);
    }

    /**
     * @author me
     * @reason bruh
     */
    @Overwrite
    public void draw(@NotNull ImbuementRecipe recipe, @NotNull IRecipeSlotsView slotsView, @NotNull GuiGraphics guiGraphics, double mouseX, double mouseY) {
        ADJCore.expandArsEMIGuis(guiGraphics, recipe.pedestalItems.size(), recipe.source);
    }
}
