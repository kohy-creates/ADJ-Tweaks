package xyz.kohara.adjcore.kubejs.serverevents;

import dev.latvian.mods.kubejs.event.EventJS;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import xyz.kohara.adjcore.misc.events.RecipeLookupEvent;

public class RecipeLookupEventJS extends EventJS {

    private final RecipeLookupEvent event;

    public RecipeLookupEventJS(RecipeLookupEvent event) {
        this.event = event;
    }

    public RecipeType<?> getRecipeType() {
        return this.event.getRecipeType();
    }

    public Container getContainer() {
        return this.event.getContainer();
    }

    public Level getLevel() {
        return this.event.getLevel();
    }

    public Recipe<?> getRecipe() {
        return this.event.getRecipe();
    }

    public ItemStack getItem() {
        return this.event.getItem();
    }

}
