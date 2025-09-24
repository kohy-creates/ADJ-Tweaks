package xyz.kohara.adjcore.mixins;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.shadowsoffire.attributeslib.AttributesLib;
import dev.shadowsoffire.attributeslib.api.ALObjects;
import dev.shadowsoffire.attributeslib.impl.AttributeEvents;
import dev.shadowsoffire.attributeslib.packet.CritParticleMessage;
import dev.shadowsoffire.placebo.network.PacketDistro;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.kohara.adjcore.combat.damageevent.ADJHurtEvent;

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
    private void critStrikeHook(LivingHurtEvent e, CallbackInfo ci) {
        ci.cancel();

        LivingEntity attacker = e.getSource().getEntity() instanceof LivingEntity le ? le : null;
        if (attacker == null) return;

        double critChance = attacker.getAttributeValue(ALObjects.Attributes.CRIT_CHANCE.get());
        float critDmg = (float) attacker.getAttributeValue(ALObjects.Attributes.CRIT_DAMAGE.get());

        RandomSource rand = e.getEntity().getRandom();

        float critMult = 1.0F;

        // Roll for crits. Each overcrit reduces the effectiveness by 15%
        // We stop rolling when crit chance fails or the crit damage would reduce the total damage dealt.
        while (rand.nextFloat() <= critChance && critDmg > 1.0F) {
            critChance--;
            critMult *= critDmg;
            critDmg *= 0.85F;
        }

        e.setAmount(e.getAmount() * critMult);

        boolean isCrit = false;

        if (critMult > 1) {
            isCrit = true;
            if (!attacker.level().isClientSide) {
                PacketDistro.sendToTracking(AttributesLib.CHANNEL, new CritParticleMessage(e.getEntity().getId()), (ServerLevel) attacker.level(), e.getEntity().blockPosition());
            }
        }

        ADJHurtEvent eventHook = new ADJHurtEvent(
                attacker,
                e.getEntity(),
                e.getAmount(),
                critDmg,
                isCrit,
                (float) critChance,
                critMult
        );
        MinecraftForge.EVENT_BUS.post(eventHook);
    }
}
