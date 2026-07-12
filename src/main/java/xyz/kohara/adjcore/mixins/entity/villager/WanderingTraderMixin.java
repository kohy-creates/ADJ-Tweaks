package xyz.kohara.adjcore.mixins.entity.villager;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WanderingTrader.class)
public class WanderingTraderMixin extends AbstractVillager{

	public WanderingTraderMixin(EntityType<? extends AbstractVillager> entityType, Level level) {
		super(entityType, level);
	}

	@Unique
	private void adj$addTradesIfPossible(MerchantOffers offers, VillagerTrades.ItemListing[] listings, int min, int max) {
		this.addOffersFromItemListings(
				offers, listings,
				this.random.nextInt(
						Math.min(min, listings.length),
						Math.min(max, listings.length)
				)
		);
	}

	@Inject(method = "updateTrades", at = @At("HEAD"), cancellable = true)
	private void addMoreMerchantRecipes(CallbackInfo ci) {
		ci.cancel();
		VillagerTrades.ItemListing[] itemListings = VillagerTrades.WANDERING_TRADER_TRADES.get(1);
		VillagerTrades.ItemListing[] itemListings2 = VillagerTrades.WANDERING_TRADER_TRADES.get(2);
		if (itemListings != null && itemListings2 != null) {
			MerchantOffers merchantOffers = this.getOffers();
			adj$addTradesIfPossible(merchantOffers, itemListings, 6, 13);
			adj$addTradesIfPossible(merchantOffers, itemListings2, 0, 4);

		}
	}

	@Override
	protected void rewardTradeXp(@NotNull MerchantOffer offer) {

	}

	@Override
	protected void updateTrades() {

	}

	@Override
	public @Nullable AgeableMob getBreedOffspring(@NotNull ServerLevel level, @NotNull AgeableMob otherParent) {
		return null;
	}
}
