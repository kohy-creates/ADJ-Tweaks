package xyz.kohara.adjcore.mixins.compat;

import dev.shadowsoffire.fastsuite.AuxRecipeManager;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.kohara.adjcore.misc.events.RecipeLookupEvent;

import java.util.Optional;

@Mixin(AuxRecipeManager.class)
public class AuxRecipeManagerMixin {


	@Inject(
			method = "getRecipeFor(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/Container;Lnet/minecraft/world/level/Level;)Ljava/util/Optional;",
			at = @At(value = "RETURN"),
			cancellable = true
	)
	private <C extends Container, T extends Recipe<C>> void getRecipeFor(
			RecipeType<T> recipeType,
			C inventory,
			Level level,
			CallbackInfoReturnable<Optional<T>> cir
	) {
		cir.setReturnValue(RecipeLookupEvent.getRecipeFor(recipeType, inventory, level, cir.getReturnValue()));
	}
}
