package xyz.kohara.adjcore.mixins.compat.botania;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vazkii.botania.api.mana.ManaItemHandler;
import vazkii.botania.common.item.equipment.armor.terrasteel.TerrasteelArmorItem;
import vazkii.botania.common.item.equipment.armor.terrasteel.TerrasteelHelmItem;

@Mixin(TerrasteelHelmItem.class)
public class TerrasteelHelmItemMixin extends TerrasteelArmorItem {

	public TerrasteelHelmItemMixin(Type type, Properties props) {
		super(type, props);
	}

	@Inject(method = "inventoryTick", at = @At("HEAD"), cancellable = true)
	public void inventoryTick(ItemStack stack, Level world, Entity entity, int slot, boolean selected, CallbackInfo ci) {
		ci.cancel();
		super.inventoryTick(stack, world, entity, slot, selected);
		if (!world.isClientSide && entity instanceof Player player
				&& player.getInventory().armor.contains(stack)
				&& hasArmorSet(player)) {
			if (player.tickCount % 10 == 0) {
				ManaItemHandler.instance().dispatchManaExact(stack, player, 10, true);
			}
		}
	}

}
