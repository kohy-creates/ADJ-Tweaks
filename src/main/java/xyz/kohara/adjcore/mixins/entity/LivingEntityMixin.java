package xyz.kohara.adjcore.mixins.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.CombatRules;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.kohara.adjcore.Config;
import xyz.kohara.adjcore.registry.ADJAttributes;
import xyz.kohara.adjcore.combat.KnockbackCooldown;
import xyz.kohara.adjcore.entity.IHealTime;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements KnockbackCooldown, IHealTime {

    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Shadow
    public abstract @NotNull Iterable<ItemStack> getArmorSlots();

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
            method = "checkFallDamage",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Entity;checkFallDamage(DZLnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;)V",
                    shift = At.Shift.BEFORE
            )
    )
    private void modifyFallDamage(double y, boolean onGround, BlockState state, BlockPos pos, CallbackInfo ci) {

        LivingEntity self = (LivingEntity) (Object) this;

        if (!onGround) return;

        double safeFall = 0;
        if (self.getAttributes().hasAttribute(ADJAttributes.SAFE_FALL_DISTANCE.get())) {
            safeFall = self.getAttributeValue(ADJAttributes.SAFE_FALL_DISTANCE.get());
        }

        float adjustedFall = (float) (this.fallDistance - safeFall);

        if (adjustedFall < 5) {
            this.fallDistance = 0;
            return;
        }

        float blocksFallen = adjustedFall - 5;
        float damageAmount = blocksFallen * 10.0f;

        if (damageAmount > 0) {
            if (!(self instanceof Player)) {
                damageAmount /= 3;
            }
            self.hurt(self.damageSources().fall(), damageAmount);
        }

        this.fallDistance = 0;
    }
}
