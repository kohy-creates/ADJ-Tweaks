package xyz.kohara.adjcore.mixins.client;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.kohara.adjcore.ADJCore;

import java.util.List;
import java.util.Random;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Unique
    private String adj$title = null;

    @Inject(method = "createTitle", at = @At("HEAD"), cancellable = true)
    private void overrideTitle(CallbackInfoReturnable<String> cir) {
        if (adj$title == null) {

            List<String> titles = ADJCore.windowTitles;

            String modpackName = titles.get(0);
            List<String> splashes = titles.subList(1, titles.size() - 1);

            String splash = splashes.get(new Random().nextInt(splashes.size()));

            adj$title = modpackName + " ~ " + splash;
        }
        cir.setReturnValue(adj$title);
    }
}
