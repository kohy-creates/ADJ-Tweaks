package xyz.kohara.adjcore.mixins.compat;

import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.item.crafting.RecipeManager;
import org.confluence.mod.integration.jei.ModJeiPlugin;
import org.confluence.mod.integration.jei.WorkshopCategory;
import org.confluence.mod.recipe.ModRecipes;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ModJeiPlugin.class, remap = false)
public class TerraCurioNukeJEIDescriptions {

    @Inject(
            method = "registerRecipes",
            at = @At("HEAD"),
            cancellable = true
    )
    private void noDescriptions(@NotNull IRecipeRegistration registration, CallbackInfo ci) {
        ci.cancel();

        ClientLevel level = Minecraft.getInstance().level;
        if (level != null) {
            RecipeManager recipeManager = level.getRecipeManager();
            registration.addRecipes(WorkshopCategory.TYPE, recipeManager.getAllRecipesFor(ModRecipes.WORKSHOP_TYPE.get()));
        }
    }
}
