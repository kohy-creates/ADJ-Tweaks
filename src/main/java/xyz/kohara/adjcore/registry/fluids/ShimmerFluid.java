//package xyz.kohara.adjcore.registry.fluids;
//
//import net.minecraft.core.BlockPos;
//import net.minecraft.core.Direction;
//import net.minecraft.world.item.Item;
//import net.minecraft.world.level.BlockGetter;
//import net.minecraft.world.level.Level;
//import net.minecraft.world.level.LevelAccessor;
//import net.minecraft.world.level.LevelReader;
//import net.minecraft.world.level.block.state.BlockState;
//import net.minecraft.world.level.block.state.StateDefinition;
//import net.minecraft.world.level.material.FlowingFluid;
//import net.minecraft.world.level.material.Fluid;
//import net.minecraft.world.level.material.FluidState;
//import org.jetbrains.annotations.NotNull;
//import xyz.kohara.adjcore.registry.ADJFluids;
//import xyz.kohara.adjcore.registry.ADJItems;
//
//public class ShimmerFluid {
//
//    @Override
//    public @NotNull Fluid getFlowing() {
//        return ADJFluids.FLOWING_SHIMMER.get();
//    }
//
//    @Override
//    public @NotNull Fluid getSource() {
//        return ADJFluids.SHIMMER.get();
//    }
//
//    @Override
//    protected boolean canConvertToSource(Level arg) {
//        return false;
//    }
//
//    @Override
//    protected void beforeDestroyingBlock(LevelAccessor level, BlockPos pos, BlockState state) {
//
//    }
//
//    @Override
//    protected int getSlopeFindDistance(LevelReader level) {
//        return 4;
//    }
//
//    @Override
//    protected int getDropOff(LevelReader level) {
//        return 1;
//    }
//
//    @Override
//    public int getAmount(FluidState state) {
//        return state.getValue(LEVEL);
//    }
//
//    @Override
//    public @NotNull Item getBucket() {
//        return ADJItems.SHIMMER_BUCKET.get();
//    }
//
//    @Override
//    protected boolean canBeReplacedWith(FluidState state, BlockGetter level, BlockPos pos, Fluid fluid, Direction direction) {
//        return false;
//    }
//
//    @Override
//    public int getTickDelay(LevelReader level) {
//        return 0;
//    }
//
//    @Override
//    protected float getExplosionResistance() {
//        return 1000000;
//    }
//
//    @Override
//    protected BlockState createLegacyBlock(FluidState state) {
//        return null;
//    }
//
//    @Override
//    public boolean isSource(FluidState state) {
//        return false;
//    }
//
//    public static class Flowing extends ShimmerFluid {
//        @Override
//        protected void createFluidStateDefinition(StateDefinition.@NotNull Builder<Fluid, FluidState> builder) {
//            super.createFluidStateDefinition(builder);
//            builder.add(LEVEL);
//        }
//    }
//
//    public static class Source extends ShimmerFluid {
//        @Override
//        public int getAmount(FluidState state) {
//            return 8;
//        }
//
//        @Override
//        public boolean isSource(FluidState state) {
//            return true;
//        }
//    }
//}
