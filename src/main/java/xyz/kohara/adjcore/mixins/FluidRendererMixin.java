package xyz.kohara.adjcore.mixins;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.block.LiquidBlockRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import xyz.kohara.adjcore.registry.ADJFluidTypes;

@Mixin(LiquidBlockRenderer.class)
public class FluidRendererMixin {

    @WrapMethod(method = "tesselate")
    private void shimmerRendering(BlockAndTintGetter level, BlockPos pos, VertexConsumer vertexConsumer, BlockState blockState, FluidState fluidState, Operation<Void> original) {

        if (fluidState.getFluidType() == ADJFluidTypes.SHIMMER_FLUID_TYPE.get()) {
        }

        original.call(level, pos, vertexConsumer, blockState, fluidState);
    }
}
