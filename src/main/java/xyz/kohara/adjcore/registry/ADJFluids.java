package xyz.kohara.adjcore.registry;

import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import xyz.kohara.adjcore.ADJCore;
import xyz.kohara.adjcore.registry.fluids.ShimmerFluid;

public class ADJFluids {

    public static final DeferredRegister<Fluid> FLUIDS =
            DeferredRegister.create(ForgeRegistries.FLUIDS, ADJCore.MOD_ID);

    public static final RegistryObject<Fluid> SHIMMER =
            FLUIDS.register("test_fluid", ShimmerFluid.Source::new);

    public static final RegistryObject<Fluid> FLOWING_SHIMMER =
            FLUIDS.register("flowing_test_fluid", ShimmerFluid.Flowing::new);

    public static void register(IEventBus bus) {
        FLUIDS.register(bus);
    }
}
