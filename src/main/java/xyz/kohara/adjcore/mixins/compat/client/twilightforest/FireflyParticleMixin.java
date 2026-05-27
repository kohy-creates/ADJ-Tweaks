package xyz.kohara.adjcore.mixins.compat.client.twilightforest;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import twilightforest.client.particle.FireflyParticle;

@Mixin(FireflyParticle.class)
public class FireflyParticleMixin extends TextureSheetParticle {

	@Mutable
	@Shadow @Final private int halfLife;

	protected FireflyParticleMixin(ClientLevel level, double x, double y, double z) {
		super(level, x, y, z);
	}

	@Inject(method = "<init>", at = @At("TAIL"))
	private void editThisParticle(
			ClientLevel level,
			double x, double y, double z,
			float movementX, float movementY, float movementZ,
			int minlife,
			boolean checkSkylight,
			CallbackInfo ci
	) {
			this.rCol = 1.0F;
			this.gCol = 1.0F;
			this.bCol = 1.0F;
			this.quadSize = 0.75F * (0.1F * (this.random.nextFloat() * 0.5F + 0.5F) * 2.0F);
			this.lifetime = 36 + level.random.nextIntBetweenInclusive(36, 180);
			this.halfLife = this.lifetime / 2;
	}

	@Override
	public @NotNull ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
	}
}
