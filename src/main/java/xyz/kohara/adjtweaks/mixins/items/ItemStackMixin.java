package xyz.kohara.adjtweaks.mixins.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.kohara.adjtweaks.Config;
import xyz.kohara.adjtweaks.misc.AuditoryTags;
import xyz.kohara.adjtweaks.sounds.ModSoundEvents;

@Mixin(ItemStack.class)
abstract class ItemStackMixin {

    @Inject(method = "damage(ILnet/minecraft/util/math/random/Random;Lnet/minecraft/server/network/ServerPlayerEntity;)Z", at = @At("HEAD"), cancellable = true)
    public void modifyDurability(int amount, Random random, ServerPlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
        if (Math.random() <= Config.DURABILITY_SAVE_CHANCE.get()) {
            cir.setReturnValue(false);
        }
    }

    @Shadow
    public abstract boolean isIn(TagKey<Item> tag);

    @Inject(method = "getEatSound", at = @At("HEAD"), cancellable = true)
    public void auditory_changeItemEatingSound(CallbackInfoReturnable<SoundEvent> cir) {
        if (this.isIn(AuditoryTags.DRIED_KELP_SOUNDS)) {
            cir.setReturnValue(ModSoundEvents.DRIED_KELP_EAT.get());
        } else if (this.isIn(AuditoryTags.SOFT_FRUIT_SOUNDS)) {
            cir.setReturnValue(ModSoundEvents.SOFT_FRUIT_EAT.get());
        } else if (this.isIn(AuditoryTags.CRUNCHY_FRUIT_SOUNDS)) {
            cir.setReturnValue(ModSoundEvents.CRUNCHY_FRUIT_EAT.get());
        } else if (this.isIn(AuditoryTags.STEW_SOUNDS)) {
            cir.setReturnValue(ModSoundEvents.STEW_EAT.get());
        } else if (this.isIn(AuditoryTags.VEGETABLE_SOUNDS)) {
            cir.setReturnValue(ModSoundEvents.VEGETABLE_EAT.get());
        }
    }
}
