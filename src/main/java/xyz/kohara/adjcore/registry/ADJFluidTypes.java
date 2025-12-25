package xyz.kohara.adjcore.registry;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import xyz.kohara.adjcore.ADJCore;
import xyz.kohara.adjcore.registry.fluids.types.ShimmerFluidType;

public class ADJFluidTypes {


    public static final DeferredRegister<FluidType> FLUID_TYPES =
            DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, ADJCore.MOD_ID);

    public static final RegistryObject<FluidType> SHIMMER_FLUID_TYPE = FLUID_TYPES.register(
            "shimmer",
            () -> new ShimmerFluidType(FluidType.Properties.create())
    );

    public static void register(IEventBus bus) {
        FLUID_TYPES.register(bus);
    }
}
