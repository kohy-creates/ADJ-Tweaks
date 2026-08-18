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
import xyz.kohara.adjcore.registry.items.TerraSlashTestItem;
import xyz.kohara.adjcore.registry.items.WingsItem;

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

    public static final RegistryObject<Item> MANA_STAR = register(
            "mana_star",
            "Mana Star",
            () -> new Item(new Item.Properties())
    );

    public static final RegistryObject<Item> HEART = register(
            "heart",
            "Heart",
            () -> new Item(new Item.Properties())
    );

    public static final RegistryObject<Item> TERRA_SLASH = register(
            "terra_slash",
            "Terra Slash",
            () -> new Item(new Item.Properties())
    );

    public static final RegistryObject<Item> TERRA_SLASH_TEST_ITEM = register(
            "terra_slash_test_item",
            "Terra Slash Test Item",
            () -> new TerraSlashTestItem(new Item.Properties())
    );

    public static final RegistryObject<Item> TEST_WINGS = register(
            "test_wings",
            "Test Wings",
            () -> new WingsItem(
                    new Item.Properties(),
                    40,
                    1.0f,
                    1.0f,
                    1.0f,
                    ADJCore.of("item/tiara_wing_1")
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
