package xyz.kohara.adjcore.mixins.client;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.kohara.adjcore.registry.ADJDamageTypeTags;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

	@Shadow
	@Final
	Minecraft minecraft;

	@WrapMethod(method = "bobHurt")
	private void redoBobHurtLogic(PoseStack matrixStack, float partialTicks, Operation<Void> original) {
		if (this.minecraft.getCameraEntity() instanceof LivingEntity livingentity) {
			var damage = livingentity.getLastDamageSource();
			if (damage != null && damage.is(ADJDamageTypeTags.NO_HURT_BOB)) return;

			float f = livingentity.hurtTime - partialTicks;
			if (livingentity.isDeadOrDying()) {
				float f1 = Math.min(livingentity.deathTime + partialTicks, 20.0F);
				matrixStack.mulPose(Axis.ZP.rotationDegrees(40.0F - 8000.0F / (f1 + 200.0F)));
			}

			if (f < 0.0F) {
				return;
			}

			f /= livingentity.hurtDuration;
			f = Mth.sin(f * f * f * f * (float) Math.PI);
			float f3 = livingentity.getHurtDir();
			matrixStack.mulPose(Axis.YP.rotationDegrees(-f3));
			float f2 = (float) (-f * 14.0 * this.minecraft.options.damageTiltStrength().get());
			matrixStack.mulPose(Axis.ZP.rotationDegrees(f2));
			matrixStack.mulPose(Axis.YP.rotationDegrees(f3));
		}
	}
}
