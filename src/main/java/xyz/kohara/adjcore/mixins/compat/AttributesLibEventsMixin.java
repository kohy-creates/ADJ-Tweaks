package xyz.kohara.adjcore.mixins.compat;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.shadowsoffire.attributeslib.impl.AttributeEvents;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.kohara.adjcore.combat.DamageHandler;

@Mixin(value = AttributeEvents.class, remap = false)
public class AttributesLibEventsMixin {

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

    @Inject(method = "apothCriticalStrike", at = @At(value = "HEAD"), cancellable = true)
    private void critStrikeHook(LivingHurtEvent event, CallbackInfo ci) {
        ci.cancel();

        DamageHandler.handleLivingHurt(event);
    }
}
