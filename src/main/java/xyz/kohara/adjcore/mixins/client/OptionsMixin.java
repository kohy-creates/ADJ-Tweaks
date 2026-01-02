package xyz.kohara.adjcore.mixins.client;

import com.mojang.serialization.Codec;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Options.class)
public abstract class OptionsMixin {

    @Shadow
    @Final
    @Mutable
    private OptionInstance<Integer> fov = new OptionInstance<>(
            "options.fov",
            OptionInstance.noTooltip(),
            (arg, integer) -> switch (integer) {
                case 30 -> genericValueLabel(arg, Component.literal("Bootleg Zoom"));
                case 70 -> genericValueLabel(arg, Component.literal("Screenshots"));
                case 90 -> genericValueLabel(arg, Component.literal("Normal"));
                case 110 -> genericValueLabel(arg, Component.literal("Quake Pro"));
                default -> genericValueLabel(arg, integer);
            },
            new OptionInstance.IntRange(30, 110),
            Codec.DOUBLE.xmap(double_ -> (int) (double_ * 40.0 + 70.0), integer -> (integer.intValue() - 70.0) / 40.0),
            70,
            integer -> Minecraft.getInstance().levelRenderer.needsUpdate()
    );

    @Shadow
    public static Component genericValueLabel(Component text, Component value) {
        return Component.translatable("options.generic_value", text, value);
    }

    @Shadow
    public static Component genericValueLabel(Component text, int value) {
        return genericValueLabel(text, Component.literal(Integer.toString(value)));
    }

}
