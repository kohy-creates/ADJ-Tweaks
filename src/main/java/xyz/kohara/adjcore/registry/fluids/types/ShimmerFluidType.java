package xyz.kohara.adjcore.registry.fluids.types;

import io.netty.util.internal.StringUtil;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import xyz.kohara.adjcore.ADJCore;

import java.util.ArrayList;
import java.util.List;
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
            public ResourceLocation getStillTexture(FluidState state, BlockAndTintGetter world, BlockPos pos) {

                BlockState northBlockState = world.getBlockState(pos.north());
                FluidState northFluidState = northBlockState.getFluidState();
                BlockState southBlockState = world.getBlockState(pos.south());
                FluidState southFluidState = southBlockState.getFluidState();
                BlockState westBlockState = world.getBlockState(pos.west());
                FluidState westFluidState = westBlockState.getFluidState();
                BlockState eastBlockState = world.getBlockState(pos.east());
                FluidState eastFluidState = eastBlockState.getFluidState();

                List<String> missingDirections = new ArrayList<>();

                if (state != northFluidState) {
                    missingDirections.add("north");
                }
                if (state != eastFluidState) {
                    missingDirections.add("east");
                }
                if (state != southFluidState) {
                    missingDirections.add("south");
                }
                if (state != westFluidState) {
                    missingDirections.add("west");
                }

                if (missingDirections.isEmpty()) {
                    return this.getStillTexture();
                }
                else if (missingDirections.size() == 4) {
                    return ADJCore.of("block/shimmer/center");
                }
                else {
                    String name = StringUtil.join("_", missingDirections).toString();
                    return ADJCore.of("block/shimmer/" + name);
                }
            }

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
                return new Vector3f(1f,1f,1f);
            }

        });
        super.initializeClient(consumer);
    }
}
