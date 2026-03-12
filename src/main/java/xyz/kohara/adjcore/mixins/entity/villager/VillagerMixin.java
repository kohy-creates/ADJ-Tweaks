package xyz.kohara.adjcore.mixins.entity.villager;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.trading.MerchantOffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Villager.class)
public class VillagerMixin {

    @WrapOperation(method = "updateTrades", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/npc/Villager;addOffersFromItemListings(Lnet/minecraft/world/item/trading/MerchantOffers;[Lnet/minecraft/world/entity/npc/VillagerTrades$ItemListing;I)V"))
    private void voidAddMoreTrades(Villager instance, MerchantOffers merchantOffers, VillagerTrades.ItemListing[] itemListings, int i, Operation<Void> original) {
        int amount = Math.toIntExact(Math.round(Math.random() * 2 + 2));
        original.call(instance, merchantOffers, itemListings, amount);
    }
}
