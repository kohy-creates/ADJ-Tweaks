package xyz.kohara.adjcore.mixins.ars;

import com.hollingsworth.arsnouveau.api.mana.IManaCap;
import com.hollingsworth.arsnouveau.api.perk.PerkAttributes;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.api.spell.SpellTier;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import com.hollingsworth.arsnouveau.setup.registry.CapabilityRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ModPotions;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.kohara.adjcore.attributes.ModAttributes;

import java.util.Map;

@Mixin(value = SpellResolver.class, remap = false)
public class SpellCooldownMixin {

    @Shadow
    public SpellContext spellContext;

    @Unique
    private final Map<Integer, Integer> adj$spellCooldowns = Map.of(
            1, 9,
            2, 7,
            3, 5,
            99, 5
    );

    @Inject(method = "expendMana", at = @At("TAIL"))
    private void addSpellCooldown(CallbackInfo ci) {
        if (spellContext.getUnwrappedCaster() instanceof Player player && spellContext.getCasterTool().getItem() instanceof SpellBook spellBook) {
            SpellTier tier = spellBook.getTier();

            int cooldown = adj$spellCooldowns.getOrDefault(tier.value, 20);
            player.getCooldowns().addCooldown(spellBook, cooldown);

            IManaCap mana = CapabilityRegistry.getMana(player).orElse(null);

            double i1 = 1d - (mana.getCurrentMana() / mana.getMaxMana());
            double regenDelay = 0.7d * (i1 * 240d + 45);
            if (player.hasEffect(ModPotions.MANA_REGEN_EFFECT.get())) {
                regenDelay = Math.min(regenDelay, 20);
            }

            int delay = (int) Math.ceil(regenDelay / 3);

            player.adjcore$setManaRegenDelay(delay);
        }
    }

//    @ModifyReturnValue(method = "getResolveCost", at = @At("RETURN"))
//    private int reduceCost(int original) {
//
//
//    }
}
