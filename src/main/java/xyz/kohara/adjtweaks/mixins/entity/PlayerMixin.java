package xyz.kohara.adjtweaks.mixins.entity;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.Position;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.kohara.adjtweaks.Config;
import xyz.kohara.adjtweaks.sounds.ModSoundEvents;

import java.util.List;

@Mixin(PlayerEntity.class)
public abstract class PlayerMixin extends LivingEntity {

    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @ModifyExpressionValue(
            method = "attack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/PlayerEntity;isClimbing()Z"
            )
    )
    private boolean adjUtils$canCrit(boolean isOnClimbable)
    {
        return Config.DISABLE_CRITS.get() || isOnClimbable;
    }

    @ModifyExpressionValue(
            method = "attack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;getNonSpectatingEntities(Ljava/lang/Class;Lnet/minecraft/util/math/Box;)Ljava/util/List;"
            )
    )
    private List<LivingEntity> adjUtils$modifyListOfSweepAttacks(List<LivingEntity> listOfSweepAttacks) {
        return Config.DISABLE_SWEEP_ATTACKS.get() ? List.of() : listOfSweepAttacks;
    }

    @Inject(
            method = "dropItem(Lnet/minecraft/item/ItemStack;ZZ)Lnet/minecraft/entity/ItemEntity;",
            at = @At(value = "NEW", target = "(Lnet/minecraft/world/World;DDDLnet/minecraft/item/ItemStack;)Lnet/minecraft/entity/ItemEntity;")
    )
    private void auditory_itemDropSound(ItemStack stack, boolean throwRandomly, boolean retainOwnership, CallbackInfoReturnable<ItemEntity> cir) {
        if (!this.isDead() && !this.isRemoved()) {
            getWorld().playSound(null, getX(), getY() + getEyeHeight(getPose()), getZ(),ModSoundEvents.ENTITY_PLAYER_DROP_ITEM.get(), SoundCategory.PLAYERS, 0.4F, 0.5F + getWorld().random.nextFloat() * 0.4f);
        }
    }
}
