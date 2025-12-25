package xyz.kohara.adjcore.mixins.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.latvian.mods.kubejs.util.RotationAxis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.CubeMap;
import net.minecraft.client.renderer.PanoramaRenderer;
import org.joml.Quaternionf;
import org.joml.Random;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PanoramaRenderer.class)
public abstract class PanoramaRendererMixin {

    @Shadow
    private static float wrap(float value, float max) {
        return value > max ? value - max : value;
    }

    @Shadow
    @Final
    private Minecraft minecraft;
    @Shadow
    @Final
    private CubeMap cubeMap;

    @Shadow
    private float spin;
    @Shadow
    private float bob;

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void oldPanorama(float deltaT, float alpha, CallbackInfo ci) {
        ci.cancel();
        float f = (float) ((double) deltaT * this.minecraft.options.panoramaSpeed().get());
        this.spin = wrap(this.spin + f * 0.1F, 360.0F);

        this.bob = wrap(this.bob + f * 0.0033F, (float) Math.PI * 2F);
        float bobOffset = (float)Math.sin(this.bob) * 15F;

        cubeMap.render(this.minecraft, 10.0F + bobOffset, -this.spin, alpha);

    }

}
