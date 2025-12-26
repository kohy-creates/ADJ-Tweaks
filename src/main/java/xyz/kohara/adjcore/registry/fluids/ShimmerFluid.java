package xyz.kohara.adjcore.registry.fluids;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import org.jetbrains.annotations.NotNull;
import xyz.kohara.adjcore.registry.*;

public class ShimmerFluid extends ForgeFlowingFluid {

    protected ShimmerFluid(Properties properties) {
        super(properties);
    }

    @Override
    public void animateTick(Level level, BlockPos pos, FluidState state, RandomSource random) {
        if (state.isSource()) {
            BlockPos blockpos = pos.above();
            if (level.getBlockState(blockpos).isAir() && !level.getBlockState(blockpos).isSolidRender(level, blockpos)) {
                if (random.nextInt(3) == 0) {
                    double d0 = (double) pos.getX() + random.nextDouble();
                    double d1 = (double) pos.getY() + 0.9D;
                    double d2 = (double) pos.getZ() + random.nextDouble();
                    level.addParticle(ADJParticles.SHIMMER.get(), d0, d1, d2, 0.0F, 0.0F, 0.0F);
                }
            }
        }

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
        return 3;
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
        return 20;
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
