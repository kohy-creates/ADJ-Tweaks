package xyz.kohara.adjcore.mixins.compat.client;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.violetmoon.quark.base.Quark;
import org.violetmoon.quark.content.client.resources.AttributeDisplayType;
import org.violetmoon.quark.content.client.resources.AttributeIconEntry;
import org.violetmoon.quark.content.client.resources.AttributeSlot;
import org.violetmoon.quark.content.client.tooltip.AttributeTooltips;
import xyz.kohara.adjcore.ADJCore;
import xyz.kohara.adjcore.ADJData;

import java.util.Collection;
import java.util.Map;

@Mixin(value = AttributeTooltips.class, remap = false)
public abstract class QuarkItemTooltipMixin {

	@Shadow
	@Nullable
	private static AttributeIconEntry getIconForAttribute(Attribute attribute) {
		return null;
	}

	@Inject(
			method = "getModifiers",
			at = @At("RETURN"),
			cancellable = true
	)
	private static void sortAttributes(
			ItemStack stack,
			AttributeSlot slot,
			CallbackInfoReturnable<Multimap<Attribute, AttributeModifier>> cir
	) {
		Multimap<Attribute, AttributeModifier> original = cir.getReturnValue();

		if (original.isEmpty())
			return;

		Multimap<Attribute, AttributeModifier> sorted =
				MultimapBuilder
						.treeKeys(ADJData.attributeComparator())
						.arrayListValues()
						.build();

		sorted.putAll(original);

		cir.setReturnValue(sorted);
	}

	@ModifyReturnValue(
			method = "format",
			at = @At(
					value = "RETURN",
					ordinal = 3
			)
	)
	private static MutableComponent formatAttributeNumber(
			MutableComponent original,
			@Local(name = "attribute") Attribute attribute,
			@Local(name = "value") double value
	) {
		if (attribute != Attributes.ATTACK_SPEED) {
			return original;
		}

		String speed = adj$getSpeedTier(value);
		return Component.literal(speed).withStyle(ChatFormatting.WHITE);
	}

	@Unique
	private static String adj$getSpeedTier(double attackSpeed) {
		if (attackSpeed >= 4.0) return "Insanely fast";
		if (attackSpeed >= 3.0) return "Very fast";
		if (attackSpeed >= 2.0) return "Fast";
		if (attackSpeed >= 1.5) return "Average";
		if (attackSpeed >= 1.2) return "Slow";
		if (attackSpeed >= 0.8) return "Very slow";
		if (attackSpeed >= 0.5) return "Extremely slow";
		return "Snail speed";
	}

	// This does nothing for some reason
//    @ModifyConstant(
//            method = "extractAttributeValues",
//            constant = @Constant(stringValue = "[+]")
//    )
//    private static String makeTextFancier(String constant, @Local(name = "slotAttributes") Multimap<Attribute, AttributeModifier> slotAttributes) {
//        System.out.println("makeText");
//        return "[+" + slotAttributes.size() + " more...]";
//    }

	@Unique
	private static TagKey<Attribute> adj$joinStatsInDescription = TagKey.create(Registries.ATTRIBUTE, ADJCore.of("join_stats_in_description"));

	@Unique
	private static boolean adj$shouldJoinBonuses(Attribute attribute, Player player) {
		var level = player.level();

		ResourceKey<Attribute> resourceKey =
				BuiltInRegistries.ATTRIBUTE.getResourceKey(attribute).orElse(null);

		if (resourceKey == null) return false;
		var registry = level.registryAccess().registryOrThrow(Registries.ATTRIBUTE);
		return registry
				.getHolder(resourceKey)
				.map(h -> h.is(adj$joinStatsInDescription))
				.orElse(false);
	}

	/**
	 * @author me
	 * @reason wanted to redo the logic slightly
	 */
	@Overwrite
	private static double getAttribute(Player player, AttributeSlot slot, ItemStack stack, Multimap<Attribute, AttributeModifier> map, Attribute key) {
		if (player == null) {
			return 0.0F;
		} else {
			Collection<AttributeModifier> collection = map.get(key);
			if (collection.isEmpty()) {
				return 0.0F;
			} else {
				double value = 0.0F;
				AttributeIconEntry entry = getIconForAttribute(key);
				if (entry == null) {
					return 0.0F;
				} else {

					Multimap<Attribute, AttributeModifier> hashMultimap = HashMultimap.create(map);

					AttributeDisplayType displayType = entry.displayTypes().get(slot);
					if (displayType != AttributeDisplayType.PERCENTAGE && (slot != AttributeSlot.POTION || !key.equals(Attributes.ATTACK_DAMAGE))) {
						AttributeInstance attribute = player.getAttribute(key);
						if (attribute != null) {
							value = attribute.getBaseValue();
							if (adj$shouldJoinBonuses(key, player)) for (var modifier : attribute.getModifiers()) {
								hashMultimap.put(key, modifier);
							}
						}
					}

					collection = hashMultimap.get(key);

					for (AttributeModifier modifier : collection) {
						if (modifier.getOperation() == AttributeModifier.Operation.ADDITION) {
							value += modifier.getAmount();
						}
					}

					double rawValue = value;

					for (AttributeModifier modifier : collection) {
						if (modifier.getOperation() == AttributeModifier.Operation.MULTIPLY_BASE) {
							value += rawValue * modifier.getAmount();
						}
					}

					for (AttributeModifier modifier : collection) {
						if (modifier.getOperation() == AttributeModifier.Operation.MULTIPLY_TOTAL) {
							value += value * modifier.getAmount();
						}
					}

					if (key.equals(Attributes.ATTACK_DAMAGE) && slot == AttributeSlot.MAINHAND) {
						value += EnchantmentHelper.getDamageBonus(stack, MobType.UNDEFINED);
					}

					if (key.equals(Attributes.ATTACK_KNOCKBACK) && slot == AttributeSlot.MAINHAND) {
						value += Quark.ZETA.itemExtensions.get(stack).getEnchantmentLevelZeta(stack, Enchantments.KNOCKBACK);
					}

					if (displayType == AttributeDisplayType.DIFFERENCE && (slot != AttributeSlot.POTION || !key.equals(Attributes.ATTACK_DAMAGE))) {
						AttributeInstance attribute = player.getAttribute(key);
						if (attribute != null) {
							value -= attribute.getBaseValue();
						}
					}

					return value;
				}
			}
		}
	}
}
