package xyz.kohara.adjcore.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import xyz.kohara.adjcore.ADJCore;

public class ADJTags {

    public static final TagKey<Item> CURIOS_DROPPED_ON_DEATH = TagKey.create(ForgeRegistries.Keys.ITEMS, ResourceLocation.fromNamespaceAndPath(ADJCore.MOD_ID, "curios_dropped_on_death"));

}
