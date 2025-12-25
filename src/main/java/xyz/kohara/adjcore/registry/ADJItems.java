package xyz.kohara.adjcore.registry;

import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import xyz.kohara.adjcore.ADJCore;

public class ADJItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, ADJCore.MOD_ID);

    public static final RegistryObject<Item> SHIMMER_BUCKET = ITEMS.register(
            "shimmer_bucket",
            () -> new BucketItem(
                    ADJFluids.SHIMMER,
                    new Item.Properties()
                            .craftRemainder(Items.BUCKET)
                            .stacksTo(1)
            )
    );

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }
}
