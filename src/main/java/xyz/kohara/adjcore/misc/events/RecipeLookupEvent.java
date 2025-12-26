package xyz.kohara.adjcore.misc.events;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import xyz.kohara.adjcore.compat.kubejs.ServerEvents;
import xyz.kohara.adjcore.compat.kubejs.serverevents.RecipeLookupEventJS;

@Cancelable
public class RecipeLookupEvent extends Event {

    private final RecipeType<?> recipeType;
    private final Container container;
    private final Level level;
    private final Recipe<?> recipe;

    public RecipeLookupEvent(
            RecipeType<?> recipeType,
            Container container,
            Level level,
            Recipe<?> recipe
    ) {
        this.recipeType = recipeType;
        this.container = container;
        this.level = level;
        this.recipe = recipe;

        var result = ServerEvents.RECIPE_LOOKUP.post(new RecipeLookupEventJS(this));

        if (result.interruptFalse()) {
            this.setCanceled(true);
        }
    }

    public ItemStack getItem() {
        return this.recipe.getResultItem(this.getLevel().registryAccess());
    }

    public RecipeType<?> getRecipeType() {
        return this.recipeType;
    }

    public Container getContainer() {
        return this.container;
    }

    public Level getLevel() {
        return this.level;
    }

    public Recipe<?> getRecipe() {
        return this.recipe;
    }
}
