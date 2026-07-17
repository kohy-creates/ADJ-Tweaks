package xyz.kohara.adjcore.misc;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class KJSTreeGrower extends AbstractTreeGrower {

	private final ResourceKey<ConfiguredFeature<?, ?>> treeFeature;

	public KJSTreeGrower(ResourceLocation treeFeature) {
		this.treeFeature = ResourceKey.create(Registries.CONFIGURED_FEATURE, treeFeature);
	}

	@Override
	protected @Nullable ResourceKey<ConfiguredFeature<?, ?>> getConfiguredFeature(@NotNull RandomSource random, boolean hasFlowers) {
		return this.treeFeature;
	}
}
