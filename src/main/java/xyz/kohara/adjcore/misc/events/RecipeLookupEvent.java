package xyz.kohara.adjcore.misc.events;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import org.apache.logging.log4j.core.jmx.Server;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import oshi.util.tuples.Pair;
import xyz.kohara.adjcore.compat.kubejs.ServerEvents;
import xyz.kohara.adjcore.compat.kubejs.serverevents.RecipeLookupEventJS;

import java.util.Optional;

@Cancelable
public class RecipeLookupEvent extends Event {

	private static Pair<ResourceLocation, Boolean> eventResultCache = new Pair<>(null, null);

	public static <C extends Container, T extends Recipe<C>> Optional<T> getRecipeFor(
			RecipeType<T> type,
			C inv,
			Level level,
			Optional<T> optional
	) {
		if (optional.isPresent()) {
			T recipe = optional.get();
			ItemStack item = recipe.getResultItem(level.registryAccess());
			ResourceLocation loc = recipe.getId();

			var cachedLoc = eventResultCache.getA();
			var cachedResult = eventResultCache.getB();

			if (cachedLoc != null && cachedLoc == loc) {
				return (cachedResult == null || cachedResult) ? optional : Optional.empty();
			}
			RecipeLookupEvent eventHook = new RecipeLookupEvent(
					type,
					inv,
					level,
					recipe
			);
//			System.out.println("not cached, firing event!");
			if (MinecraftForge.EVENT_BUS.post(eventHook)) {
				eventResultCache = new Pair<>(loc, false);
				return Optional.empty();
			} else {
				eventResultCache = new Pair<>(loc, true);
				return optional;
			}
		}
		return optional;
	}

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

		if (ServerEvents.RECIPE_LOOKUP.hasListeners()) {
			var result = ServerEvents.RECIPE_LOOKUP.post(new RecipeLookupEventJS(this));

			if (result.interruptFalse()) {
				this.setCanceled(true);
			}
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
