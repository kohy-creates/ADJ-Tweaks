package xyz.kohara.adjcore.mixins.client.liquidfadeout;

import me.jellysquid.mods.sodium.client.model.color.ColorProvider;
import me.jellysquid.mods.sodium.client.model.light.LightMode;
import me.jellysquid.mods.sodium.client.model.light.LightPipeline;
import me.jellysquid.mods.sodium.client.model.light.LightPipelineProvider;
import me.jellysquid.mods.sodium.client.model.light.data.QuadLightData;
import me.jellysquid.mods.sodium.client.model.quad.ModelQuadView;
import me.jellysquid.mods.sodium.client.model.quad.ModelQuadViewMutable;
import me.jellysquid.mods.sodium.client.model.quad.properties.ModelQuadFacing;
import me.jellysquid.mods.sodium.client.render.chunk.compile.ChunkBuildBuffers;
import me.jellysquid.mods.sodium.client.render.chunk.compile.buffers.ChunkModelBuilder;
import me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.FluidRenderer;
import me.jellysquid.mods.sodium.client.render.chunk.terrain.material.DefaultMaterials;
import me.jellysquid.mods.sodium.client.render.chunk.terrain.material.Material;
import me.jellysquid.mods.sodium.client.util.DirectionUtil;
import me.jellysquid.mods.sodium.client.world.WorldSlice;
import net.caffeinemc.mods.sodium.api.util.ColorABGR;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.client.ForgeHooksClient;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// Roughly ported from https://github.com/DaFuqs/FluidVoidFading/

@Pseudo
@Mixin(value = FluidRenderer.class, remap = false)
public abstract class EmbeddiumLiquidRendererMixin {

    @Shadow
    @Final
    private BlockPos.MutableBlockPos scratchPos;

    @Shadow protected abstract ColorProvider<FluidState> getColorProvider(Fluid fluid);

    @Shadow @Final private ModelQuadViewMutable quad;

    @Shadow @Final private LightPipelineProvider lighters;

    @Shadow @Final private QuadLightData quadLightData;

    @Shadow @Final private int[] quadColors;

    @Shadow
    private static void setVertex(ModelQuadViewMutable quad, int i, float x, float y, float z, float u, float v) {
    }

    @Shadow
    protected abstract void writeQuad(ChunkModelBuilder builder, Material material, BlockPos offset, ModelQuadView quad, ModelQuadFacing facing, boolean flip);

    @Inject(method = "render", at = @At("HEAD"))
    public void render(WorldSlice world, FluidState fluidState, BlockPos pos, BlockPos offset, ChunkBuildBuffers buffers, CallbackInfo cir) {
        if (pos.getY() == world.getMinBuildHeight()) {
            renderFluidInVoid(world, fluidState, pos, offset, buffers);
        }
    }

    @Inject(method = "isSideExposed", at = @At("HEAD"), cancellable = true)
    private void isSideExposed(BlockAndTintGetter world, int x, int y, int z, Direction dir, float height, CallbackInfoReturnable<Boolean> cir) {
        if (dir == Direction.DOWN && y == world.getMinBuildHeight()) {
            cir.setReturnValue(false);
        }
    }

    @Unique
    private void renderFluidInVoid(WorldSlice world, @NotNull FluidState fluidState, BlockPos blockPos, BlockPos offset, ChunkBuildBuffers buffers) {
        Fluid fluid = fluidState.getType();
        Material material = DefaultMaterials.forFluidState(fluidState);
        ChunkModelBuilder meshBuilder = buffers.get(material);
        if (fluid != Fluids.EMPTY) {
            int posX = blockPos.getX();
            int posY = blockPos.getY();
            int posZ = blockPos.getZ();

            BlockState northBlockState = world.getBlockState(blockPos.north());
            FluidState northFluidState = northBlockState.getFluidState();
            BlockState southBlockState = world.getBlockState(blockPos.south());
            FluidState southFluidState = southBlockState.getFluidState();
            BlockState westBlockState = world.getBlockState(blockPos.west());
            FluidState westFluidState = westBlockState.getFluidState();
            BlockState eastBlockState = world.getBlockState(blockPos.east());
            FluidState eastFluidState = eastBlockState.getFluidState();

            boolean sfNorth = northFluidState.getType().isSame(fluidState.getType());
            boolean sfSouth = southFluidState.getType().isSame(fluidState.getType());
            boolean sfWest = westFluidState.getType().isSame(fluidState.getType());
            boolean sfEast = eastFluidState.getType().isSame(fluidState.getType());

            boolean isWater = fluidState.is(FluidTags.WATER);
            final ColorProvider<FluidState> colorProvider = this.getColorProvider(fluid);
            TextureAtlasSprite[] sprites = ForgeHooksClient.getFluidSprites(world, blockPos, fluidState);

            float northWestHeight;
            float southWestHeight;
            float southEastHeight;
            float northEastHeight;
            float yOffset = 0.0F;
            northWestHeight = 1.0F;
            southWestHeight = 1.0F;
            southEastHeight = 1.0F;
            northEastHeight = 1.0F;

            ModelQuadViewMutable quad = this.quad;
            LightMode lightMode = isWater && Minecraft.useAmbientOcclusion() ? LightMode.SMOOTH : LightMode.FLAT;
            LightPipeline lighter = this.lighters.getLighter(lightMode);
            quad.setFlags(0);
            float c1;
            float c2;
            float x1;
            float z1;
            float x2;
            float z2;
            float u1;
            for (Direction dir : DirectionUtil.HORIZONTAL_DIRECTIONS) {
                switch (dir) {
                    case NORTH:
                        if (sfNorth) {
                            continue;
                        }

                        c1 = northWestHeight;
                        c2 = northEastHeight;
                        x1 = 0.0F;
                        x2 = 1.0F;
                        z1 = 0.001F;
                        z2 = z1;
                        break;
                    case SOUTH:
                        if (sfSouth) {
                            continue;
                        }

                        c1 = southEastHeight;
                        c2 = southWestHeight;
                        x1 = 1.0F;
                        x2 = 0.0F;
                        z1 = 0.999F;
                        z2 = z1;
                        break;
                    case WEST:
                        if (sfWest) {
                            continue;
                        }

                        c1 = southWestHeight;
                        c2 = northWestHeight;
                        x1 = 0.001F;
                        x2 = x1;
                        z1 = 1.0F;
                        z2 = 0.0F;
                        break;
                    case EAST:
                        if (!sfEast) {
                            c1 = northEastHeight;
                            c2 = southEastHeight;
                            x1 = 0.999F;
                            x2 = x1;
                            z1 = 0.0F;
                            z2 = 1.0F;
                            break;
                        }
                    default:
                        continue;
                }

                int adjX = posX + dir.getStepX();
                int adjY = posY + dir.getStepY();
                int adjZ = posZ + dir.getStepZ();
                TextureAtlasSprite sprite = sprites[1];
                boolean isOverlay = false;
                if (sprites.length > 2) {
                    BlockPos adjPos = this.scratchPos.set(adjX, adjY, adjZ);
                    BlockState adjBlock = world.getBlockState(adjPos);

                    if (sprites[2] != null && adjBlock.shouldDisplayFluidOverlay(world, adjPos, fluidState)) {
                        sprite = sprites[2];
                        isOverlay = true;
                    }
                }

                u1 = sprite.getU(0.0);
                float u2 = sprite.getU(8.0);
                float v1 = sprite.getV(((1.0F - c1) * 16.0F * 0.5F));
                float v2 = sprite.getV(((1.0F - c2) * 16.0F * 0.5F));
                float v3 = sprite.getV(8.0);
                quad.setSprite(sprite);
                setVertex(quad, 0, x2, c2, z2, u2, v2);
                setVertex(quad, 1, x2, yOffset, z2, u2, v3);
                setVertex(quad, 2, x1, yOffset, z1, u1, v3);
                setVertex(quad, 3, x1, c1, z1, u1, v1);
                float br = dir.getAxis() == Direction.Axis.Z ? 0.8F : 0.6F;
                ModelQuadFacing facing = ModelQuadFacing.fromDirection(dir);

                int[] previousQuadColors = this.quadColors;
                lighter.calculate(quad, blockPos, this.quadLightData, null, dir, false);
                colorProvider.getColors(world, blockPos, fluidState, quad, previousQuadColors);
                int[] biomeColors = previousQuadColors;

                this.calculateAlphaQuadColors(biomeColors, br, 1.0F, 0.3F);
                this.writeQuad(meshBuilder, material, offset.relative(Direction.DOWN, 1), quad, facing, false);
                if (!isOverlay) {
                    this.writeQuad(meshBuilder, material, offset.relative(Direction.DOWN, 1), quad, facing.getOpposite(), true);
                }

                this.calculateAlphaQuadColors(biomeColors, br, 0.3F, 0.0F);
                this.writeQuad(meshBuilder, material, offset.relative(Direction.DOWN, 2), quad, facing, false);
                if (!isOverlay) {
                    this.writeQuad(meshBuilder, material, offset.relative(Direction.DOWN, 2), quad, facing.getOpposite(), true);
                }

                this.quadColors[0] = previousQuadColors[0];
                this.quadColors[1] = previousQuadColors[1];
                this.quadColors[2] = previousQuadColors[2];
                this.quadColors[3] = previousQuadColors[3];
            }
        }
    }

    @Unique
    private void calculateAlphaQuadColors(int[] biomeColors, float brightness, float alpha1, float alpha2) {
        for(int i = 0; i < 4; ++i) {
            this.quadColors[i] = ColorABGR.withAlpha(biomeColors != null ? biomeColors[i] : -1, brightness);
            float a = i == 0 || i == 3 ? alpha1 : alpha2;
            this.quadColors[i] = ColorABGR.pack(
                    ColorABGR.unpackRed(this.quadColors[i]) / 255F,
                    ColorABGR.unpackGreen(this.quadColors[i]) / 255F,
                    ColorABGR.unpackBlue(this.quadColors[i]) / 255F,
                    ColorABGR.unpackAlpha(this.quadColors[i]) / 255F * a
            );
        }
    }

}