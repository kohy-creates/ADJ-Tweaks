package xyz.kohara.adjcore.mixins;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyboardHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(KeyboardHandler.class)
public class KeyboardHandlerMixin {


    @ModifyConstant(
            method = "keyPress",
            constant = @Constant(intValue = 290)
    )
    private int removeF1(int constant) {
        return -1;
    }

    @ModifyConstant(
            method = "keyPress",
            constant = @Constant(intValue = 292)
    )
    private int removeF3(int constant) {
        return InputConstants.KEY_HOME;
    }
}
