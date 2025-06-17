package xyz.kohara.adjcore.mixins;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.shadowsoffire.attributeslib.impl.AttributeEvents;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = AttributeEvents.class, remap = false)
public class AttributesLibNuker {

    @ModifyExpressionValue(
            method = "affixModifiers",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraftforge/event/ItemAttributeModifierEvent;getModifiers()Lcom/google/common/collect/Multimap;"

            )
    )
    private Multimap<Attribute, AttributeModifier> fuckingNukeThisLineLikeWhyIsntItEvenAConfigBruh(Multimap<Attribute, AttributeModifier> original) {
        return ArrayListMultimap.create();
    }
}
