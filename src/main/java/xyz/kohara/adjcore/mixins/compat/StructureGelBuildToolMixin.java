package xyz.kohara.adjcore.mixins.compat;

import com.legacy.structure_gel.core.item.building_tool.BuildingToolItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = BuildingToolItem.class, remap = false)
public class StructureGelBuildToolMixin {

    @Inject(method = "hasPermission", at = @At("HEAD"), cancellable = true)
    private void allowInSurvival(ItemStack stack, Player player, CallbackInfoReturnable<Boolean> cir) {
        BuildingToolItem toolItem = (BuildingToolItem) (Object) this;
        if (stack.is(toolItem)) {
            cir.setReturnValue(true);
        }

    }
}
