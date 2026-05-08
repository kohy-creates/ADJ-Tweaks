package xyz.kohara.adjcore.mixins.compat.botania;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vazkii.botania.api.block_entity.RadiusDescriptor;
import vazkii.botania.common.block.flower.functional.FallenKanadeBlockEntity;
import xyz.kohara.adjcore.campfire.CozyCampfire;
import xyz.kohara.adjcore.registry.ADJEffects;

import java.util.List;

@Mixin(FallenKanadeBlockEntity.class)
public abstract class FallenKanadeBlockEntityMixin {

	@Shadow
	@Final
	private static int COST;

	@Unique
	private static final int adj$NEW_RADIUS = 10;

	@Unique
	private static boolean adj$canHeal(LivingEntity e) {
		return CozyCampfire.isPassiveMob(e);
	}

	@Inject(
			method = "tickFlower",
			at = @At(
					value = "INVOKE",
					target = "Lvazkii/botania/api/block_entity/FunctionalFlowerBlockEntity;tickFlower()V",
					shift = At.Shift.AFTER
			),
			remap = false,
			cancellable = true
	)
	private void modifyAABB(CallbackInfo ci) {
		ci.cancel();
		var flower = (FallenKanadeBlockEntity) (Object) this;
		if (!flower.getLevel().isClientSide) {
			boolean did = false;
			List<LivingEntity> entities = flower.getLevel().getEntitiesOfClass(
					LivingEntity.class, new AABB(
							flower.getEffectivePos().offset(-adj$NEW_RADIUS, -adj$NEW_RADIUS, -adj$NEW_RADIUS),
							flower.getEffectivePos().offset(adj$NEW_RADIUS + 1, adj$NEW_RADIUS + 1, adj$NEW_RADIUS + 1)
					),
					FallenKanadeBlockEntityMixin::adj$canHeal
			);
			for (LivingEntity toHeal : entities) {
				if (flower.getMana() >= COST) {
					toHeal.addEffect(new MobEffectInstance(ADJEffects.FALLEN_KANADE.get(), 59, 0, true, true));
					flower.addMana(-COST);
					did = true;
				}
			}
			if (did) {
				flower.sync();
			}
		}
	}

	@Inject(method = "getRadius", at = @At("HEAD"), cancellable = true, remap = false)
	private void changeRadius(CallbackInfoReturnable<RadiusDescriptor> cir) {
		cir.setReturnValue(RadiusDescriptor.Rectangle.square(
				((FallenKanadeBlockEntity) (Object) this).getEffectivePos(),
				adj$NEW_RADIUS
		));
	}
}
