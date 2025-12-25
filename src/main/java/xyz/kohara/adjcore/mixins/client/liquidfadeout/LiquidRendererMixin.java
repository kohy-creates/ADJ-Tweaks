package xyz.kohara.adjcore.mixins.client.liquidfadeout;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.LiquidBlockRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// Roughly ported from https://github.com/DaFuqs/FluidVoidFading/

@Mixin(LiquidBlockRenderer.class)
public abstract class LiquidRendererMixin {

    @Shadow
    protected abstract int getLightColor(BlockAndTintGetter world, BlockPos pos);

    @Shadow
    private TextureAtlasSprite waterOverlay;

    @Shadow
    private static boolean isNeighborSameFluid(FluidState firstState, FluidState secondState) {
        return secondState.getType().isSame(firstState.getType());
    }

    @Inject(method = "tesselate", at = @At("HEAD"))
    private void render(
            BlockAndTintGetter world,
            BlockPos pos,
            VertexConsumer vertexConsumer,
            BlockState blockState,
            FluidState fluidState,
            CallbackInfo ci
    ) {
        if (adj$isDirectlyAboveVoid(world, pos)) {
            adj$renderFluidInVoid(world, pos, vertexConsumer, fluidState);
        }
    }

    @Unique
    private static boolean adj$isDirectlyAboveVoid(BlockGetter world, BlockPos pos) {
        return pos.getY() == world.getMinBuildHeight();
    }

    @Inject(
            method = "isFaceOccludedByNeighbor",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void isSideCovered(
            BlockGetter world,
            BlockPos pos,
            Direction direction,
            float maxDeviation,
            BlockState state,
            CallbackInfoReturnable<Boolean> cir
    ) {
        if (direction == Direction.DOWN && adj$isDirectlyAboveVoid(world, pos)) {
            cir.setReturnValue(true);
        }
    }

    @Unique
    private void adj$renderFluidInVoid(
            BlockAndTintGetter world,
            BlockPos pos,
            VertexConsumer vertexConsumer,
            FluidState fluidState
    ) {
        Fluid fluid = fluidState.getType();
        if (fluid != Fluids.EMPTY) {

            BlockState northBlockState = world.getBlockState(pos.north());
            FluidState northFluidState = northBlockState.getFluidState();
            BlockState southBlockState = world.getBlockState(pos.south());
            FluidState southFluidState = southBlockState.getFluidState();
            BlockState westBlockState = world.getBlockState(pos.west());
            FluidState westFluidState = westBlockState.getFluidState();
            BlockState eastBlockState = world.getBlockState(pos.east());
            FluidState eastFluidState = eastBlockState.getFluidState();

            boolean sameFluidNorth = isNeighborSameFluid(fluidState, northFluidState);
            boolean sameFluidSouth = isNeighborSameFluid(fluidState, southFluidState);
            boolean sameFluidWest = isNeighborSameFluid(fluidState, westFluidState);
            boolean sameFluidEast = isNeighborSameFluid(fluidState, eastFluidState);

            float brightnessUp = world.getShade(Direction.UP, true);
            float brightnessNorth = world.getShade(Direction.NORTH, true);
            float brightnessWest = world.getShade(Direction.WEST, true);

            float n = 1.0F;
            float o = 1.0F;
            float p = 1.0F;
            float q = 1.0F;
            double d = (pos.getX() & 15);
            double e = (pos.getY() & 15);
            double r = (pos.getZ() & 15);
            float t = 0.0F;
            float ca = 0;
            float cb;
            float u1;
            float u2;

            int light = this.getLightColor(world, pos);

            IClientFluidTypeExtensions ext =
                    IClientFluidTypeExtensions.of(fluid);

            TextureAtlasSprite sprite = Minecraft.getInstance()
                    .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                    .apply(ext.getStillTexture(fluidState, world, pos));

            int color = ext.getTintColor(fluidState, world, pos);

            int[] colors = adj$unpackColor(color);

            float redF = colors[1] / 255F;
            float greenF = colors[2] / 255F;
            float blueF = colors[3] / 255F;

            float alpha1 = colors[0] / 255F;
            float alpha2 = 0.3F * alpha1;
            float alpha3 = 0.0F;

            for (int i = 0; i < 4; ++i) { // directions
                double x1;
                double z1;
                double x2;
                double z2;
                boolean shouldRender;
                if (i == 0) {
                    ca = n;
                    cb = q;
                    x1 = d;
                    x2 = d + 1.0D;
                    z1 = r + 0.0010000000474974513D;
                    z2 = r + 0.0010000000474974513D;
                    shouldRender = sameFluidNorth;
                } else if (i == 1) {
                    cb = o;
                    x1 = d + 1.0D;
                    x2 = d;
                    z1 = r + 1.0D - 0.0010000000474974513D;
                    z2 = r + 1.0D - 0.0010000000474974513D;
                    shouldRender = sameFluidSouth;
                } else if (i == 2) {
                    ca = o;
                    cb = n;
                    x1 = d + 0.0010000000474974513D;
                    x2 = d + 0.0010000000474974513D;
                    z1 = r + 1.0D;
                    z2 = r;
                    shouldRender = sameFluidWest;
                } else {
                    ca = q;
                    cb = p;
                    x1 = d + 1.0D - 0.0010000000474974513D;
                    x2 = d + 1.0D - 0.0010000000474974513D;
                    z1 = r;
                    z2 = r + 1.0D;
                    shouldRender = sameFluidEast;
                }

                if (!shouldRender) {
                    u1 = sprite.getU(0.0D);
                    u2 = sprite.getU(8.0D);
                    float v1 = sprite.getV(((1.0F - ca) * 16.0F * 0.5F));
                    float v2 = sprite.getV(((1.0F - cb) * 16.0F * 0.5F));
                    float v3 = sprite.getV(8.0D);
                    float sidedBrightness = i < 2 ? brightnessNorth : brightnessWest;
                    float red = brightnessUp * sidedBrightness * redF;
                    float green = brightnessUp * sidedBrightness * greenF;
                    float blue = brightnessUp * sidedBrightness * blueF;
                    adj$vertex(vertexConsumer, x1, e + (double) ca - 1, z1, red, green, blue, u1, v1, light, alpha1);
                    adj$vertex(vertexConsumer, x2, e + (double) cb - 1, z2, red, green, blue, u2, v2, light, alpha1);
                    adj$vertex(vertexConsumer, x2, e + (double) t - 1, z2, red, green, blue, u2, v3, light, alpha2);
                    adj$vertex(vertexConsumer, x1, e + (double) t - 1, z1, red, green, blue, u1, v3, light, alpha2);

                    adj$vertex(vertexConsumer, x1, e + (double) ca - 2, z1, red, green, blue, u1, v1, light, alpha2);
                    adj$vertex(vertexConsumer, x2, e + (double) cb - 2, z2, red, green, blue, u2, v2, light, alpha2);
                    adj$vertex(vertexConsumer, x2, e + (double) t - 2, z2, red, green, blue, u2, v3, light, alpha3);
                    adj$vertex(vertexConsumer, x1, e + (double) t - 2, z1, red, green, blue, u1, v3, light, alpha3);
                    if (sprite != this.waterOverlay) {
                        adj$vertex(vertexConsumer, x1, e + (double) t - 1, z1, red, green, blue, u1, v3, light, alpha2);
                        adj$vertex(vertexConsumer, x2, e + (double) t - 1, z2, red, green, blue, u2, v3, light, alpha2);
                        adj$vertex(vertexConsumer, x2, e + (double) cb - 1, z2, red, green, blue, u2, v2, light, alpha1);
                        adj$vertex(vertexConsumer, x1, e + (double) ca - 1, z1, red, green, blue, u1, v1, light, alpha1);

                        adj$vertex(vertexConsumer, x1, e + (double) t - 2, z1, red, green, blue, u1, v3, light, alpha3);
                        adj$vertex(vertexConsumer, x2, e + (double) t - 2, z2, red, green, blue, u2, v3, light, alpha3);
                        adj$vertex(vertexConsumer, x2, e + (double) cb - 2, z2, red, green, blue, u2, v2, light, alpha2);
                        adj$vertex(vertexConsumer, x1, e + (double) ca - 2, z1, red, green, blue, u1, v1, light, alpha2);
                    }
                }
            }
        }
    }

    @Unique
    private void adj$vertex(
            VertexConsumer vertexConsumer,
            double x, double y, double z,
            float red, float green, float blue,
            float u, float v,
            int light, float alpha
    ) {
        vertexConsumer.vertex(x, y, z)
                .color(red, green, blue, alpha)
                .uv(u, v)
                .uv2(light)
                .normal(0.0F, 1.0F, 0.0F)
                .endVertex();
    }

    @Unique
    private static int[] adj$unpackColor(int color) {
        return new int[]{
                color >> 24 & 0xff,
                color >> 16 & 0xff,
                color >> 8 & 0xff,
                color & 0xff
        };
    }
}
