package xyz.kohara.adjcore.mixins.compat.client;

import dev.shadowsoffire.attributeslib.client.AttributesGui;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = AttributesGui.class, remap = false)
public class AttribtuesGUIMixin {

	@Shadow protected boolean open;

	@Shadow @Final protected InventoryScreen parent;

	@Shadow @Final protected AttributesGui.HideUnchangedButton hideUnchangedBtn;

	@Shadow @Final public static int WIDTH;

	@Shadow protected int leftPos;

	@Shadow protected int topPos;

	@Shadow @Final protected ImageButton recipeBookButton;

	/**
	 * @author me
	 * @reason g
	 */
	@Overwrite
	public void toggleVisibility() {
		this.open = !this.open;
		if (this.open && this.parent.getRecipeBookComponent().isVisible()) {
			this.parent.getRecipeBookComponent().toggleVisibility();
		}
		this.hideUnchangedBtn.visible = this.open;

//		int newLeftPos;
//		if (this.open && this.parent.width >= 379) {
//			newLeftPos = 177 + (this.parent.width - this.parent.imageWidth - 200) / 2;
//		}
//		else {
//			newLeftPos = (this.parent.width - this.parent.imageWidth) / 2;
//		}
//
//		this.parent.leftPos = newLeftPos;
		this.leftPos = this.parent.getGuiLeft() - WIDTH;
		this.topPos = this.parent.getGuiTop();

		if (this.recipeBookButton != null) this.recipeBookButton.setPosition(this.parent.getGuiLeft() + 104, this.parent.height / 2 - 22);
		this.hideUnchangedBtn.setPosition(this.leftPos + 7, this.topPos + 151);
	}
}
