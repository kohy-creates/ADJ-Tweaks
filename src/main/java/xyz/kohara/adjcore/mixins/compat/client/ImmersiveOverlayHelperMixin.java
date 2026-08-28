package xyz.kohara.adjcore.mixins.compat.client;

import cc.cassian.immersiveoverlays.overlay.OverlayHelpers;
import cc.cassian.mru.compat.ModCompat;
import cc.cassian.mru.util.ItemContainerUtils;
import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

@Mixin(value = ItemContainerUtils.class, remap = false)
public abstract class ImmersiveOverlayHelperMixin {

	/**
	 * @author me
	 * @reason make this work better with Xaero mods
	 */
	@Overwrite
	public static boolean checkInventoryForItem(Inventory inventory, Item item, boolean value) {
		if (value) {
			return true;
		} else {
			Player player = Minecraft.getInstance().player;
			if (ModCompat.CURIOS) {
				LazyOptional<ICuriosItemHandler> capability = CuriosApi.getCuriosInventory(player);
				if (capability.isPresent()) {
					IItemHandlerModifiable allEquipped = capability.resolve().get().getEquippedCurios();

					for (int i = 0; i < allEquipped.getSlots(); ++i) {
						if (allEquipped.getStackInSlot(i).getItem() == item)
							return true;
					}
				}
			}

			if (ModCompat.TRAVELERS_BACKPACK) {
				if (CapabilityUtils.isWearingBackpack(player)) {
					BackpackWrapper backpackWrapper = CapabilityUtils.getBackpackWrapper(player);
					if (backpackWrapper != null) {
						ItemStackHandler backpackInv = backpackWrapper.getStorage();

						for (int i = 0; i < backpackInv.getSlots(); ++i) {
							if (backpackInv.getStackInSlot(i).getItem() == item) {
								return true;
							}
						}
					}
				}
			}

			return OverlayHelpers.checkInventoryForStack(inventory, item) != ItemStack.EMPTY;
		}
	}
}
