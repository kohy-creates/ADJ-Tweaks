package xyz.kohara.adjcore.registry.fluids;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import org.jetbrains.annotations.NotNull;
import xyz.kohara.adjcore.registry.ADJBlocks;
import xyz.kohara.adjcore.registry.ADJFluidTypes;
import xyz.kohara.adjcore.registry.ADJFluids;
import xyz.kohara.adjcore.registry.ADJItems;

public class ShimmerFluid extends ForgeFlowingFluid {

    protected ShimmerFluid(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull FluidType getFluidType() {
        return ADJFluidTypes.SHIMMER_FLUID_TYPE.get();
    }

    @Override
    public @NotNull Fluid getFlowing() {
        return ADJFluids.FLOWING_SHIMMER.get();
    }

    @Override
    public @NotNull Fluid getSource() {
        return ADJFluids.SHIMMER.get();
    }

    @Override
    protected boolean canConvertToSource(Level arg) {
        return false;
    }

    @Override
    protected void beforeDestroyingBlock(LevelAccessor level, BlockPos pos, BlockState state) {

    }

    @Override
    protected int getSlopeFindDistance(LevelReader level) {
        return 4;
    }

    @Override
    protected int getDropOff(LevelReader level) {
        return 1;
    }

    @Override
    public int getAmount(@NotNull FluidState state) {
        return isSource(state) ? 8 : state.getValue(LEVEL);
    }

    @Override
    public @NotNull Item getBucket() {
        return ADJItems.SHIMMER_BUCKET.get();
    }

    @Override
    protected boolean canBeReplacedWith(FluidState state, BlockGetter level, BlockPos pos, Fluid fluid, Direction direction) {
        return false;
    }

    @Override
    public int getTickDelay(LevelReader level) {
        return 10;
    }

    @Override
    protected float getExplosionResistance() {
        return 1000000;
    }

    @Override
    protected @NotNull BlockState createLegacyBlock(FluidState state) {
        return ADJBlocks.SHIMMER_BLOCK.get()
                .defaultBlockState()
                .setValue(LiquidBlock.LEVEL, getLegacyLevel(state));
    }

    @Override
    public boolean isSource(FluidState state) {
        return false;
    }

    public static class Flowing extends ShimmerFluid {
        public Flowing(Properties properties) {
            super(properties);
        }

        @Override
        protected void createFluidStateDefinition(StateDefinition.@NotNull Builder<Fluid, FluidState> builder) {
            super.createFluidStateDefinition(builder);
            builder.add(LEVEL);
        }

        @Override
        public int getAmount(@NotNull FluidState state) {
            return state.getValue(LEVEL);
        }
    }

    public static class Source extends ShimmerFluid {
        public Source(Properties properties) {
            super(properties);
        }

        @Override
        public int getAmount(@NotNull FluidState state) {
            return 8;
        }

        @Override
        public boolean isSource(FluidState state) {
            return true;
        }
    }
}
