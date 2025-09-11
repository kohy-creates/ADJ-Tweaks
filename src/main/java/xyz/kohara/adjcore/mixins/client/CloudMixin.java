//package xyz.kohara.adjcore.mixins.client;
//
//import com.mojang.blaze3d.platform.GlStateManager;
//import com.mojang.blaze3d.systems.RenderSystem;
//import com.mojang.blaze3d.vertex.BufferBuilder;
//import com.mojang.blaze3d.vertex.PoseStack;
//import com.mojang.blaze3d.vertex.Tesselator;
//import com.mojang.blaze3d.vertex.VertexBuffer;
//import net.minecraft.client.CloudStatus;
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.multiplayer.ClientLevel;
//import net.minecraft.client.renderer.FogRenderer;
//import net.minecraft.client.renderer.GameRenderer;
//import net.minecraft.client.renderer.LevelRenderer;
//import net.minecraft.client.renderer.ShaderInstance;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.server.packs.resources.Resource;
//import net.minecraft.util.Mth;
//import net.minecraft.world.phys.Vec3;
//import org.joml.Matrix4f;
//import org.spongepowered.asm.mixin.*;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Constant;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.ModifyConstant;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//
//import javax.annotation.Nullable;
//
//@Mixin(LevelRenderer.class)
//public abstract class CloudMixin {
//
//    @Shadow
//    @Nullable
//    private ClientLevel level;
//
//    @Shadow
//    private int ticks;
//
//    @Shadow
//    private int prevCloudX;
//
//    @Shadow
//    private int prevCloudY;
//
//    @Shadow
//    private int prevCloudZ;
//
//    @Shadow
//    @Final
//    private Minecraft minecraft;
//
//    @Shadow
//    private Vec3 prevCloudColor;
//
//    @Shadow
//    @Nullable
//    private CloudStatus prevCloudsType;
//
//    @Shadow
//    private boolean generateClouds;
//
//    @Shadow
//    @Nullable
//    private VertexBuffer cloudBuffer;
//
//    @Shadow
//    public abstract BufferBuilder.RenderedBuffer buildClouds(BufferBuilder builder, double x, double y, double z, Vec3 cloudColor);
//
//    @Shadow
////    @Mutable
//    @Final
//    private static ResourceLocation CLOUDS_LOCATION;
//
////    @Inject(method = "<clinit>", at = @At("RETURN"))
////    private static void overwriteCloudsTexture(CallbackInfo ci) {
////        CLOUDS_LOCATION = ResourceLocation.parse("adjcore:textures/environment/clouds_layer_1.png");
////    }
//
////    @Unique
////    @Final
////    private ResourceLocation CLOUDS_LOCATION_LAYER_2 = ResourceLocation.parse("adjcore:textures/environment/clouds_layer_2.png");
//
//    @Inject(method = "renderClouds", at = @At("RETURN"))
//    private void renderSecondCloudLayer(PoseStack poseStack, Matrix4f projectionMatrix, float partialTick,
//                                        double camX, double camY, double camZ, CallbackInfo ci) {
//
//        if (!level.effects().renderClouds(level, ticks, partialTick, poseStack, camX, camY, camZ, projectionMatrix)) {
//            float f = level.effects().getCloudHeight() + 40F;
//            if (!Float.isNaN(f)) {
//                RenderSystem.disableCull();
//                RenderSystem.enableBlend();
//                RenderSystem.enableDepthTest();
//                RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
//                RenderSystem.depthMask(true);
//                float f1 = 12.0F;
//                float f2 = 4.0F;
//                double d0 = 2.0E-4;
//                double d1 = (ticks + partialTick) * 0.03F;
//                double d2 = (camX + d1) / 12.0;
//                double d3 = f - (float) camY + 0.33F;
//                double d4 = camZ / 12.0 + 0.33F;
//                d2 -= Mth.floor(d2 / 2048.0) * 2048;
//                d4 -= Mth.floor(d4 / 2048.0) * 2048;
//                float f3 = (float) (d2 - Mth.floor(d2));
//                float f4 = (float) (d3 / 4.0 - Mth.floor(d3 / 4.0)) * 4.0F;
//                float f5 = (float) (d4 - Mth.floor(d4));
//                Vec3 vec3 = level.getCloudColor(partialTick);
//                int i = (int) Math.floor(d2);
//                int j = (int) Math.floor(d3 / 4.0);
//                int k = (int) Math.floor(d4);
//                if (i != prevCloudX
//                        || j != prevCloudY
//                        || k != prevCloudZ
//                        || minecraft.options.getCloudsType() != prevCloudsType
//                        || prevCloudColor.distanceToSqr(vec3) > 2.0E-4) {
//                    prevCloudX = i;
//                    prevCloudY = j;
//                    prevCloudZ = k;
//                    prevCloudColor = vec3;
//                    prevCloudsType = minecraft.options.getCloudsType();
//                    generateClouds = true;
//                }
//
//                if (generateClouds) {
//                    generateClouds = false;
//                    BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
//                    if (cloudBuffer != null) {
//                        cloudBuffer.close();
//                    }
//
//                    cloudBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
//                    BufferBuilder.RenderedBuffer bufferbuilder$renderedbuffer = buildClouds(bufferbuilder, d2, d3, d4, vec3);
//                    cloudBuffer.bind();
//                    cloudBuffer.upload(bufferbuilder$renderedbuffer);
//                    VertexBuffer.unbind();
//                }
//
//                RenderSystem.setShader(GameRenderer::getPositionTexColorNormalShader);
//                RenderSystem.setShaderTexture(0, CLOUDS_LOCATION);
//                FogRenderer.levelFogColor();
//                poseStack.pushPose();
//                poseStack.scale(12.0F, 2.0F, 12.0F);
//                poseStack.translate(-f3, f4, -f5);
//                if (cloudBuffer != null) {
//                    cloudBuffer.bind();
//                    int l = prevCloudsType == CloudStatus.FANCY ? 0 : 1;
//
//                    for (int i1 = l; i1 < 2; i1++) {
//                        if (i1 == 0) {
//                            RenderSystem.colorMask(false, false, false, false);
//                        } else {
//                            RenderSystem.colorMask(true, true, true, true);
//                        }
//
//                        ShaderInstance shaderinstance = RenderSystem.getShader();
//                        cloudBuffer.drawWithShader(poseStack.last().pose(), projectionMatrix, shaderinstance);
//                    }
//
//                    VertexBuffer.unbind();
//                }
//
//                poseStack.popPose();
//                RenderSystem.enableCull();
//                RenderSystem.disableBlend();
//                RenderSystem.defaultBlendFunc();
//            }
//        }
//    }
//}
