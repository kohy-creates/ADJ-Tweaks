package xyz.kohara.adjcore.misc;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.grower.AbstractMegaTreeGrower;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class KJSMegaTreeGrower extends AbstractMegaTreeGrower {

	private final ResourceKey<ConfiguredFeature<?, ?>> treeFeature;
	private final ResourceKey<ConfiguredFeature<?, ?>> megaTreeFeature;

	public KJSMegaTreeGrower(ResourceLocation treeFeature, ResourceLocation megaTreeFeature) {
		this.treeFeature = ResourceKey.create(Registries.CONFIGURED_FEATURE, treeFeature);
		this.megaTreeFeature = ResourceKey.create(Registries.CONFIGURED_FEATURE, megaTreeFeature);
	}

	@Override
	protected @Nullable ResourceKey<ConfiguredFeature<?, ?>> getConfiguredFeature(@NotNull RandomSource random, boolean hasFlowers) {
		return this.treeFeature;
	}

	@Override
	protected @Nullable ResourceKey<ConfiguredFeature<?, ?>> getConfiguredMegaFeature(@NotNull RandomSource random) {
		return this.megaTreeFeature;
	}
}
