package xyz.kohara.adjcore.misc.placementmodifiertypes;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementFilter;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import org.jetbrains.annotations.NotNull;

public class IsHardcorePlacement extends PlacementFilter {

    public static final IsHardcorePlacement INSTANCE = new IsHardcorePlacement();
    public static final MapCodec<IsHardcorePlacement> CODEC = MapCodec.unit(() -> INSTANCE);

    @Override
    protected boolean shouldPlace(@NotNull PlacementContext context, @NotNull RandomSource random, @NotNull BlockPos pos) {

        Level level = context.getLevel().getLevel();
        return level.getLevelData().isHardcore();
    }

    @Override
    public @NotNull PlacementModifierType<?> type() {
        return ModPlacementModifierTypes.IS_HARDCORE.get();
    }
}
