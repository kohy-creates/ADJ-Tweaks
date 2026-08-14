package xyz.kohara.adjcore.mixins.compat.client;

import com.llamalad7.mixinextras.sugar.Local;
import dev.shadowsoffire.attributeslib.impl.PercentBasedAttribute;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.theillusivec4.curios.client.ClientEventHandler;

import java.util.Map;

import static net.minecraft.world.item.ItemStack.ATTRIBUTE_MODIFIER_FORMAT;

@Mixin(ClientEventHandler.class)
public class CurioTooltipMixin {

	@Redirect(
			method = "onTooltip",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/network/chat/Component;translatable(Ljava/lang/String;[Ljava/lang/Object;)Lnet/minecraft/network/chat/MutableComponent;",
					ordinal = 2
			)
	)
	private MutableComponent modifyTooltip1(
			String key, Object[] args,
			@Local(name = "entry") Map.Entry<Attribute, AttributeModifier> entry,
			@Local(name = "attributemodifier") AttributeModifier attributemodifier,
			@Local(name = "d1") double d1) {
		int operation = attributemodifier.getOperation().toValue();
		if (entry.getKey() instanceof PercentBasedAttribute) operation = 1;
		return Component.translatable("curios.modifiers.slots.plus." + operation, ATTRIBUTE_MODIFIER_FORMAT.format(d1));
	}

	@Redirect(
			method = "onTooltip",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/network/chat/Component;translatable(Ljava/lang/String;[Ljava/lang/Object;)Lnet/minecraft/network/chat/MutableComponent;",
					ordinal = 3
			)
	)
	private MutableComponent modifyTooltip2(
			String key, Object[] args,
			@Local(name = "entry") Map.Entry<Attribute, AttributeModifier> entry,
			@Local(name = "attributemodifier") AttributeModifier attributemodifier,
			@Local(name = "d1") double d1) {
		int operation = attributemodifier.getOperation().toValue();
		if (entry.getKey() instanceof PercentBasedAttribute) operation = 1;
		return Component.translatable("curios.modifiers.slots.take." + operation, ATTRIBUTE_MODIFIER_FORMAT.format(d1));
	}
}
