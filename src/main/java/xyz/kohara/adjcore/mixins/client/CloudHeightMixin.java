package xyz.kohara.adjcore.mixins.client;

import net.minecraft.client.renderer.DimensionSpecialEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(DimensionSpecialEffects.OverworldEffects.class)
public class CloudHeightMixin {

    @ModifyConstant(
            method = "<init>",
            constant = @Constant(floatValue = 192.0F)
    )
    private static float replaceCloudLevel(float constant) {
        return 240.0F;
    }
}
