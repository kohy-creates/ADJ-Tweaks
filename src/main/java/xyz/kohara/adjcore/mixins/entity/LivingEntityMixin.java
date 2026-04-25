package xyz.kohara.adjcore.mixins.entity;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.CombatRules;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.kohara.adjcore.Config;
import xyz.kohara.adjcore.combat.KnockbackCooldown;
import xyz.kohara.adjcore.entity.IHealTime;

import java.util.UUID;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements KnockbackCooldown, IHealTime {

    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Shadow
    public abstract @NotNull Iterable<ItemStack> getArmorSlots();

    @Shadow
    @Final
    private static UUID SPEED_MODIFIER_SPRINTING_UUID;

    @Shadow
    @Final
    @Mutable
    private static AttributeModifier SPEED_MODIFIER_SPRINTING;

    // Yeeting Resistance effect logic out of here
    @Inject(
            method = "getDamageAfterMagicAbsorb",
            at = @At(
                    value = "HEAD"
            ),
            cancellable = true
    )
    private void resistanceEdit(DamageSource damageSource, float damageAmount, CallbackInfoReturnable<Float> cir) {
        if (damageSource.is(DamageTypeTags.BYPASSES_EFFECTS)) {
            cir.setReturnValue(damageAmount);
        } else {
            if (damageAmount <= 0.0F) {
                cir.setReturnValue(0.0F);
            } else if (damageSource.is(DamageTypeTags.BYPASSES_ENCHANTMENTS)) {
                cir.setReturnValue(damageAmount);
            } else {
                int k = EnchantmentHelper.getDamageProtection(this.getArmorSlots(), damageSource);
                if (k > 0) {
                    damageAmount = CombatRules.getDamageAfterMagicAbsorb(damageAmount, k);
                }
                cir.setReturnValue(damageAmount);
            }
        }
    }

    // Removes shield use delay
    @ModifyConstant(method = "isBlocking", constant = @Constant(intValue = 5))
    private int setShieldUseDelay(int constant) {
        return Config.SHIELD_DELAY.get();
    }

    @Unique
    public int adjcore$knockbackCooldown;

    @Unique
    public int adjcore$healTime;

    @Override
    public int adjcore$getKnockbackCooldown() {
        return adjcore$knockbackCooldown;
    }

    @Override
    public void adjcore$setKnockbackCooldown(int cooldown) {
        adjcore$knockbackCooldown = cooldown;
    }

    @Override
    public int adjcore$getHealTime() {
        return adjcore$healTime;
    }

    public void adjcore$setHealTime(int time) {
        adjcore$healTime = time;
    }


    @Inject(
            method = "tick",
            at = @At("HEAD")
    )
    private void onTick(CallbackInfo ci) {
        if (adjcore$knockbackCooldown > 0) {
            adjcore$knockbackCooldown--;
        }
        if (adjcore$healTime > 0) {
            adjcore$healTime--;
        }
    }

    @Inject(
            method = "<clinit>",
            at = @At("TAIL")
    )
    private static void nerfSprinting(CallbackInfo ci) {
        SPEED_MODIFIER_SPRINTING = new AttributeModifier(
                SPEED_MODIFIER_SPRINTING_UUID, "Sprinting speed boost", 0.3F, AttributeModifier.Operation.MULTIPLY_BASE
        );
    }
}
