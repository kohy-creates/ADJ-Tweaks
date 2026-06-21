package xyz.kohara.adjcore.mixins.items;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.extensions.IForgeItem;
import org.spongepowered.asm.mixin.Mixin;
import xyz.kohara.adjcore.Config;
import xyz.kohara.adjcore.compat.kubejs.serverevents.ItemRarityGetEventJS;
import xyz.kohara.adjcore.misc.events.ItemRarityGetEvent;

import java.util.Objects;

@Mixin(Item.class)
public abstract class ItemMixin implements IForgeItem {

	@Override
	public int getMaxDamage(ItemStack stack) {
		double durability = IForgeItem.super.getMaxDamage(stack);
		int i = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.UNBREAKING, stack);
		if (i > 0) {
			durability *= Config.Tools.unbreakingMultiplier;
		}
		return (int) durability;
	}

	@WrapMethod(method = "getRarity")
	private Rarity getRarity(ItemStack stack, Operation<Rarity> original) {
		var base = original.call(stack);
		var handler = new ItemRarityGetEvent(stack, base);
		MinecraftForge.EVENT_BUS.post(handler);
		return Objects.requireNonNullElse(handler.rarity, base);
	}
}
