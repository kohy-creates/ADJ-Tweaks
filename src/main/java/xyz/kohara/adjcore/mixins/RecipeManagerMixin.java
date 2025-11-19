package xyz.kohara.adjcore.mixins;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.kohara.adjcore.misc.events.RecipeLookupEvent;

import java.util.Map;
import java.util.Optional;

@Mixin(RecipeManager.class)
public abstract class RecipeManagerMixin {

    @Shadow
    protected abstract <C extends Container, T extends Recipe<C>> Map<ResourceLocation, T> byType(RecipeType<T> recipeType);

    @Inject(method = "getRecipeFor(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/Container;Lnet/minecraft/world/level/Level;)Ljava/util/Optional;", at = @At("HEAD"), cancellable = true)
    public <C extends Container, T extends Recipe<C>> void getRecipeFor(RecipeType<T> recipeType, C inventory, Level level, CallbackInfoReturnable<Optional<T>> cir) {
        Optional<T> optional = this.byType(recipeType).values().stream().filter(arg3 -> arg3.matches(inventory, level)).findFirst();
        if (optional.isPresent()) {
            RecipeLookupEvent eventHook = new RecipeLookupEvent(
                    recipeType,
                    inventory,
                    level,
                    optional.get()
            );
            if (MinecraftForge.EVENT_BUS.post(eventHook)) {
                optional = Optional.empty();
            }
        }
        cir.setReturnValue(optional);
    }
}
