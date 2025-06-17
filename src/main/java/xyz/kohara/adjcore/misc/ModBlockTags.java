// Auditory
// If it wasn't obvious enough
package xyz.kohara.adjcore.misc;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import xyz.kohara.adjcore.ADJCore;

public class ModBlockTags {

    public static final TagKey<Block> NEEDS_NETHERITE_TOOLS = TagKey.create(ForgeRegistries.Keys.BLOCKS, ResourceLocation.fromNamespaceAndPath(ADJCore.MOD_ID, "needs_netherite_tools"));
    public static final TagKey<Block> NEEDS_TIER_5_TOOLS = TagKey.create(ForgeRegistries.Keys.BLOCKS, ResourceLocation.fromNamespaceAndPath(ADJCore.MOD_ID, "needs_tier_5_tools"));
    public static final TagKey<Block> NEEDS_TIER_6_TOOLS = TagKey.create(ForgeRegistries.Keys.BLOCKS, ResourceLocation.fromNamespaceAndPath(ADJCore.MOD_ID, "needs_tier_6_tools"));
}
