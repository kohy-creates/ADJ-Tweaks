package xyz.kohara.adjcore.registry;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import xyz.kohara.adjcore.ADJCore;
import xyz.kohara.adjcore.misc.LangGenerator;
import xyz.kohara.adjcore.registry.fluids.types.ShimmerFluidType;

import java.util.function.Supplier;

public class ADJFluidTypes {


    public static final DeferredRegister<FluidType> FLUID_TYPES =
            DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, ADJCore.MOD_ID);

    public static final RegistryObject<FluidType> SHIMMER_FLUID_TYPE = register(
            "shimmer",
            "Shimmer",
            () -> new ShimmerFluidType(null)
    );

    private static RegistryObject<FluidType> register(String id, String name, Supplier<FluidType> factory) {
        LangGenerator.addFluidTypeTranslation(id, name);
        return FLUID_TYPES.register(id, factory);
    }

    public static void register(IEventBus bus) {
        FLUID_TYPES.register(bus);
    }
}
