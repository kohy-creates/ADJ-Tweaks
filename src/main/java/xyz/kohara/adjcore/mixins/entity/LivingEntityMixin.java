package xyz.kohara.adjcore.mixins.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.CombatRules;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
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
import xyz.kohara.adjcore.attributes.ModAttributes;
import xyz.kohara.adjcore.combat.KnockbackCooldown;

import java.util.Optional;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements KnockbackCooldown {

    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Shadow
    public abstract Iterable<ItemStack> getArmorSlots();

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

    @Override
    public int adjcore$getKnockbackCooldown() {
        return adjcore$knockbackCooldown;
    }

    @Override
    public void adjcore$setKnockbackCooldown(int cooldown) {
        adjcore$knockbackCooldown = cooldown;
    }

    @Inject(
            method = "tick",
            at = @At("HEAD")
    )
    private void onTick(CallbackInfo ci) {
        if (adjcore$knockbackCooldown > 0) {
            adjcore$knockbackCooldown--;
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
        if (self.getAttributes().hasAttribute(ModAttributes.SAFE_FALL_DISTANCE.get())) {
            safeFall = self.getAttributeValue(ModAttributes.SAFE_FALL_DISTANCE.get());
        }

        float adjustedFall = (float) (this.fallDistance - safeFall);

        if (adjustedFall < 5) {
            this.fallDistance = 0;
            return;
        }

        float blocksFallen = adjustedFall - 5;
        float damageAmount = blocksFallen * 10.0f;

        if (damageAmount > 0) {
            self.hurt(self.damageSources().fall(), damageAmount);
        }

        this.fallDistance = 0;
    }
}
