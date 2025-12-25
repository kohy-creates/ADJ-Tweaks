package xyz.kohara.adjcore.registry;

import com.mojang.serialization.Codec;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import xyz.kohara.adjcore.ADJCore;
import xyz.kohara.adjcore.registry.biomemodifiers.AddCarversBiomeModifier;

public class ADJBiomeModifiers {

    public static final DeferredRegister<Codec<? extends BiomeModifier>> BIOME_MODIFIER_SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, ADJCore.MOD_ID);

    public static final RegistryObject<Codec<? extends BiomeModifier>> ADD_CARVERS =
            BIOME_MODIFIER_SERIALIZERS.register("add_carvers", () -> AddCarversBiomeModifier.CODEC);

    public static void register(IEventBus eventBus) {
        BIOME_MODIFIER_SERIALIZERS.register(eventBus);
    }
}
