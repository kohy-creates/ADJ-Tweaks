package xyz.kohara.adjcore.registry.fluids.types;

import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import xyz.kohara.adjcore.ADJCore;

import java.util.function.Consumer;

public class ShimmerFluidType extends FluidType {

	public ShimmerFluidType(Properties properties) {
		super(FluidType.Properties.create()
				.density(50000)
				.viscosity(50000)
				.lightLevel(12)
				.sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
				.sound(SoundActions.FLUID_VAPORIZE, SoundEvents.GENERIC_EXTINGUISH_FIRE)
				.sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
				.canConvertToSource(false)
				.canDrown(false)
				.canHydrate(false)
				.fallDistanceModifier(0.33f)
				.rarity(Rarity.EPIC)
				.canExtinguish(true)
				.canSwim(false)
		);
	}

	@Override
	public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
		consumer.accept(new IClientFluidTypeExtensions() {

			@Override
			public ResourceLocation getStillTexture() {
				return ADJCore.of("block/shimmer");
			}

			@Override
			public ResourceLocation getFlowingTexture() {
				return ADJCore.of("block/shimmer_flowing");
			}

			@Override
			public @NotNull Vector3f modifyFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, Vector3f fluidFogColor) {
				return new Vector3f(1f, 1f, 1f);
			}

		});
		super.initializeClient(consumer);
	}
}
