package xyz.kohara.adjcore.registry;

import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import xyz.kohara.adjcore.ADJCore;
import xyz.kohara.adjcore.misc.LangGenerator;

import java.util.function.Supplier;

public class ADJItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, ADJCore.MOD_ID);

    public static final RegistryObject<Item> SHIMMER_BUCKET = register(
            "shimmer_bucket",
            "Shimmer Bucket",
            () -> new BucketItem(ADJFluids.SHIMMER, new Item.Properties()
                    .craftRemainder(Items.BUCKET)
                    .stacksTo(1)
                    .rarity(Rarity.EPIC)
            )
    );

    private static RegistryObject<Item> register(String id, String name, Supplier<Item> factory) {
        LangGenerator.addItemTranslation(id, name);
        return ITEMS.register(id, factory);
    }

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }
}
