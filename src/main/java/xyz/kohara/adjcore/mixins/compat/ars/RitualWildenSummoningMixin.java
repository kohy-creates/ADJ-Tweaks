package xyz.kohara.adjcore.mixins.compat.ars;

import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.common.ritual.RitualWildenSummoning;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = RitualWildenSummoning.class, remap = false)
public abstract class RitualWildenSummoningMixin extends AbstractRitual {

    @ModifyReturnValue(method = "isBossSpawn", at = @At("RETURN"))
    private boolean isBossSpawnNew(boolean original) {
        return didConsumeItem(() -> Items.ENCHANTED_GOLDEN_APPLE);
    }

    @WrapMethod(method = "canConsumeItem")
    private boolean canConsumeItem(ItemStack stack, Operation<Boolean> original) {
        return stack.is(Items.ENCHANTED_GOLDEN_APPLE);
    }
}
