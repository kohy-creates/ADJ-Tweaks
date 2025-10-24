package xyz.kohara.adjcore.mixins.items;


import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "fuzs.mutantmonsters.handler.PlayerEventsHandler", remap = false)
public class MutantMonstersRemoveMutantSkeleArmorEffectsMixin { // what a mouthful

    @Inject(method = "onItemUseTick", at = @At("HEAD"), cancellable = true)
    private static void onItemUseTick(LivingEntity entity, ItemStack useItem, Object useItemRemaining, CallbackInfoReturnable<Object> cir) {
        cir.setReturnValue(null);
    }

    @Inject(method = "onArrowLoose", at = @At("HEAD"), cancellable = true)
    private static void onArrowLoose(Player player, ItemStack stack, Level level, Object charge, boolean hasAmmo, CallbackInfoReturnable<Object> cir) {
        cir.setReturnValue(null);
    }

}
