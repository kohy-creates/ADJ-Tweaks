package xyz.kohara.adjcore.mixins.items;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.kohara.adjcore.Config;
import xyz.kohara.adjcore.misc.AuditoryTags;
import xyz.kohara.adjcore.sounds.ModSoundEvents;

@Mixin(ItemStack.class)
abstract class ItemStackMixin {

    @Unique
    private static AttributeModifier adj$HOLDER;

    @Shadow
    public abstract boolean is(TagKey<Item> tag);

    /**
     * Reduces durability damage taken
     */
    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    public void modifyDurability(int amount, RandomSource random, ServerPlayer user, CallbackInfoReturnable<Boolean> cir) {
        if (Math.random() <= Config.DURABILITY_SAVE_CHANCE.get()) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "getEatingSound", at = @At("HEAD"), cancellable = true)
    public void auditory_changeItemEatingSound(CallbackInfoReturnable<SoundEvent> cir) {
        if (this.is(AuditoryTags.DRIED_KELP_SOUNDS)) {
            cir.setReturnValue(ModSoundEvents.DRIED_KELP_EAT.get());
        } else if (this.is(AuditoryTags.SOFT_FRUIT_SOUNDS)) {
            cir.setReturnValue(ModSoundEvents.SOFT_FRUIT_EAT.get());
        } else if (this.is(AuditoryTags.CRUNCHY_FRUIT_SOUNDS)) {
            cir.setReturnValue(ModSoundEvents.CRUNCHY_FRUIT_EAT.get());
        } else if (this.is(AuditoryTags.STEW_SOUNDS)) {
            cir.setReturnValue(ModSoundEvents.STEW_EAT.get());
        } else if (this.is(AuditoryTags.VEGETABLE_SOUNDS)) {
            cir.setReturnValue(ModSoundEvents.VEGETABLE_EAT.get());
        }
    }

    /**
     * Doubles the durability of tools that have the Unbreaking enchantment
     */
    @Inject(method = "getMaxDamage", at = @At("RETURN"), cancellable = true)
    public void getMaxDamage(CallbackInfoReturnable<Integer> cir) {
        ItemStack stack = ((ItemStack) (Object) this);
        if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.UNBREAKING, stack) != 0)
            cir.setReturnValue((int) (cir.getReturnValue() * Config.UNBREAKNG_DURABILITY_MULTIPLIER.get()));
    }
}
