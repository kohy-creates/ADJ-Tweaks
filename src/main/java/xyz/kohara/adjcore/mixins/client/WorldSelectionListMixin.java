package xyz.kohara.adjcore.mixins.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.worldselection.WorldSelectionList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(WorldSelectionList.class)
public abstract class WorldSelectionListMixin extends ObjectSelectionList<WorldSelectionList.Entry> {

    public WorldSelectionListMixin(Minecraft arg, int i, int j, int k, int l, int m) {
        super(arg, i, j, k, l, m);
    }

    @ModifyArg(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/components/ObjectSelectionList;<init>(Lnet/minecraft/client/Minecraft;IIIII)V"
            ),
            index = 5
    )
    private static int changeItemHeight(int i) {
        return 40;
    }

    @Override
    protected void renderSelection(GuiGraphics guiGraphics, int top, int width, int height, int outerColor, int innerColor) {
        int dx = 3; // move 4 pixels left
        int dy = 2; // move 3 pixels up

        int i = this.x0 + (this.width - width) / 2 - dx;
        int j = this.x0 + (this.width + width) / 2 - dx;
        guiGraphics.fill(i, top - 2 - dy, j, top + height + 2 - dy, outerColor);
        guiGraphics.fill(i + 1, top - 1 - dy, j - 1, top + height + 1 - dy, innerColor);
    }
}
