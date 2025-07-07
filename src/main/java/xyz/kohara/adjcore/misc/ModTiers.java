package xyz.kohara.adjcore.misc;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.ForgeTier;
import net.minecraftforge.common.TierSortingRegistry;
import xyz.kohara.adjcore.ADJCore;

import java.util.List;

public class ModTiers {
    public static final Tier TIER_5 = new ForgeTier(
            5,  // harvest level (higher than Netherite)
            2500,
            10.0F,
            5.0F,
            25,
            TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(ADJCore.MOD_ID, "needs_tier_5_tool")),
            () -> Ingredient.of(Items.IRON_INGOT)
    );

    public static final Tier TIER_6 = new ForgeTier(
            5,  // harvest level (higher than Netherite)
            2500,
            10.0F,
            5.0F,
            25,
            TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(ADJCore.MOD_ID, "needs_tier_6_tool")),
            () -> Ingredient.of(Items.IRON_INGOT)
    );

    static {
        TierSortingRegistry.registerTier(
                TIER_5,
                ResourceLocation.fromNamespaceAndPath(ADJCore.MOD_ID, "tier_5"),
                List.of(Tiers.NETHERITE),
                List.of(TIER_6)
        );

        TierSortingRegistry.registerTier(
                TIER_5,
                ResourceLocation.fromNamespaceAndPath(ADJCore.MOD_ID, "tier_6"),
                List.of(TIER_5),
                List.of()
        );
    }
}
