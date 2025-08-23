package xyz.kohara.adjcore.mixins;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.shadowsoffire.attributeslib.impl.AttributeEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.kohara.adjcore.combat.critevent.ApothCritStrikeEvent;

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

    @Inject(method = "apothCriticalStrike", at = @At(value = "INVOKE", target = "Ldev/shadowsoffire/placebo/network/PacketDistro;sendToTracking(Lnet/minecraftforge/network/simple/SimpleChannel;Ljava/lang/Object;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;)V"))
    private void critStrikeHook(LivingHurtEvent e, CallbackInfo ci, @Local LivingEntity attacker, @Local double critChance, @Local(ordinal = 0) float critDmg, @Local(ordinal = 1) float critMult) {
        ApothCritStrikeEvent eventHook = new ApothCritStrikeEvent(
                attacker,
                e.getEntity(),
                e.getAmount(),
                critDmg,
                (float) critChance,
                critMult
        );
        MinecraftForge.EVENT_BUS.post(eventHook);
    }
}
