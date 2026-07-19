package xyz.kohara.adjcore.mixins.compat.ars;

import com.hollingsworth.arsnouveau.api.mana.IManaCap;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.api.spell.SpellTier;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import com.hollingsworth.arsnouveau.setup.registry.CapabilityRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ModPotions;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.kohara.adjcore.ADJCore;

import java.util.Map;

@Mixin(value = SpellResolver.class, remap = false)
public class SpellCooldownMixin {

	@Shadow
	public SpellContext spellContext;

	@Unique
	private final Map<Integer, Integer> adj$spellCooldowns = Map.of(
			1, 8,
			2, 7,
			3, 6,
			99, 6
	);

	@Inject(method = "expendMana", at = @At("TAIL"))
	private void addSpellCooldown(CallbackInfo ci) {
		if (spellContext.getUnwrappedCaster() instanceof Player player && spellContext.getCasterTool().getItem() instanceof SpellBook spellBook) {
			SpellTier tier = spellBook.getTier();

			int cooldown = adj$spellCooldowns.getOrDefault(tier.value, 20);
			player.getCooldowns().addCooldown(spellBook, cooldown);

			IManaCap mana = CapabilityRegistry.getMana(player).orElse(null);

			// Mana regen delay
			ADJCore.setPlayerManaRegenDelay(player, mana);
		}
	}
}
