package xyz.kohara.adjcore.mixins.ars;

import com.hollingsworth.arsnouveau.api.event.MaxManaCalcEvent;
import com.hollingsworth.arsnouveau.api.mana.IManaCap;
import com.hollingsworth.arsnouveau.api.mana.IManaDiscountEquipment;
import com.hollingsworth.arsnouveau.api.perk.PerkAttributes;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.util.CuriosUtil;
import com.hollingsworth.arsnouveau.api.util.ManaUtil;
import com.hollingsworth.arsnouveau.setup.config.ServerConfig;
import com.hollingsworth.arsnouveau.setup.registry.CapabilityRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ModPotions;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.kohara.adjcore.attributes.ModAttributes;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Mixin(value = ManaUtil.class, remap = false)
public class ReworkMaxManaProgression {

    @Shadow
    @Final
    static UUID MAX_MANA_MODIFIER;

    @Shadow
    @Final
    static UUID MANA_REGEN_MODIFIER;

    @Inject(method = "calcMaxMana", at = @At("HEAD"), cancellable = true)
    // Calculate Max Mana & Mana Reserve to keep track of the mana reserved by familiars & co.
    private static void calcMaxMana(Player e, CallbackInfoReturnable<ManaUtil.Mana> cir) {

        cir.cancel();

        double rawMax = ServerConfig.INIT_MAX_MANA.get();
//        int tier = mana.getBookTier();
//        int numGlyphs = mana.getGlyphBonus();
//        rawMax += numGlyphs * ServerConfig.GLYPH_MAX_BONUS.get();
//        rawMax += tier * ServerConfig.TIER_MAX_BONUS.get();

        var manaAttribute = e.getAttribute(PerkAttributes.MAX_MANA.get());
        if (manaAttribute != null) {
            var manaCache = manaAttribute.getModifier(MAX_MANA_MODIFIER);
            if (manaCache == null || manaCache.getAmount() != rawMax) {
                if (manaCache != null) manaAttribute.removeModifier(manaCache);
                manaAttribute.addTransientModifier(new AttributeModifier(MAX_MANA_MODIFIER, "Mana Cache", rawMax, AttributeModifier.Operation.ADDITION));
            }
            rawMax = manaAttribute.getValue();
        }

        int max = (int) rawMax;

        MaxManaCalcEvent event = new MaxManaCalcEvent(e, max);
        MinecraftForge.EVENT_BUS.post(event);
        max = event.getMax();
        float reserve = event.getReserve();
        cir.setReturnValue(new ManaUtil.Mana(max, reserve));
    }

    @Inject(method = "getManaRegen", at = @At("HEAD"), cancellable = true)
    private static void getManaRegen(Player e, CallbackInfoReturnable<Double> cir) {

        if (e.adjcore$getManaRegenDelay() > 0) {
            cir.setReturnValue(0d);
        }

        double regenBonus = ServerConfig.INIT_MANA_REGEN.get();

        var manaAttribute = e.getAttribute(PerkAttributes.MANA_REGEN_BONUS.get());
        if (manaAttribute != null) {
            var manaCache = manaAttribute.getModifier(MANA_REGEN_MODIFIER);
            if (manaCache == null || manaCache.getAmount() != regenBonus) {
                if (manaCache != null) manaAttribute.removeModifier(manaCache);
                manaAttribute.addTransientModifier(new AttributeModifier(MANA_REGEN_MODIFIER, "Mana Regen Cache", regenBonus, AttributeModifier.Operation.ADDITION));
            }
            regenBonus = manaAttribute.getValue();
        } else {
            cir.setReturnValue(0d);
        }

        Optional<IManaCap> manaCapOptional = CapabilityRegistry.getMana(e).resolve();
        if (manaCapOptional.isEmpty()) {
            cir.setReturnValue(0d);
            return;
        }
        IManaCap mana = manaCapOptional.get();

        double stationaryBonus = 0;
        if (!e.walkAnimation.isMoving() || e.hasEffect(ModPotions.MANA_REGEN_EFFECT.get())) {
            stationaryBonus = mana.getMaxMana() / 3d;
        }
        double regenFactor = (e.hasEffect(ModPotions.MANA_REGEN_EFFECT.get())) ? 1d : (mana.getCurrentMana() / mana.getMaxMana()) * 0.8d + 0.2d;
        double regen = ((mana.getMaxMana() / 3d) + 1 + stationaryBonus + (regenBonus * 5)) * regenFactor * 1.15d;

        cir.setReturnValue(Math.ceil(regen / 3d));
    }

    @Redirect(method = "getPlayerDiscounts", at = @At(value = "INVOKE", target = "Ljava/util/concurrent/atomic/AtomicInteger;get()I"))
    private static int applyPercentReductions(AtomicInteger instance, @Local(argsOnly = true) LivingEntity e, @Local(argsOnly = true) Spell spell) {

        int original = instance.get();
        if (!(e instanceof Player player)) return original;

        double reduction = 0d;

        AttributeInstance costReduction = player.getAttribute(ModAttributes.MANA_COST_REDUCTION.get());
        if (costReduction != null) {
            reduction = costReduction.getValue();
        }

        double cost = spell.getCost();

        return (int) (original + (cost * reduction));
    }

    @Inject(method = "getPlayerDiscounts", at = @At("HEAD"), cancellable = true)
    private static void getPlayerDiscounts(LivingEntity e, Spell spell, ItemStack casterStack, CallbackInfoReturnable<Integer> cir) {
        if (e == null) {
            cir.setReturnValue(0);
            return;
        }

        double reduction = 0d;

        AttributeInstance costReduction = e.getAttribute(ModAttributes.MANA_COST_REDUCTION.get());
        if (costReduction != null) {
            reduction = costReduction.getValue();
        }

        double cost = spell.getCost();

        cir.setReturnValue((int) (cost * reduction));
    }
}
