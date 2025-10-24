package xyz.kohara.adjcore.mixins.items;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "fuzs.mutantmonsters.world.item.SkeletonArmorItem", remap = true)
public class MutantMonstersRemoveSkeleArmorPotionsMixin {

    @Inject(method = "inventoryTick", at = @At("HEAD"), cancellable = true)
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected, CallbackInfo ci) {
        ci.cancel();
    }
}
