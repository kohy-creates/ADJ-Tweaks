package xyz.kohara.adjcore.registry;

import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import xyz.kohara.adjcore.ADJCore;

public class ADJFluids {

    public static final DeferredRegister<Fluid> FLUIDS =
            DeferredRegister.create(ForgeRegistries.FLUIDS, ADJCore.MOD_ID);

    private static ForgeFlowingFluid.Properties SHIMMER_PROPERTIES;

    public static final RegistryObject<FlowingFluid> SHIMMER =
            FLUIDS.register("shimmer", () -> new ForgeFlowingFluid.Source(SHIMMER_PROPERTIES));

    public static final RegistryObject<FlowingFluid> FLOWING_SHIMMER =
            FLUIDS.register("flowing_shimmer", () -> new ForgeFlowingFluid.Flowing(SHIMMER_PROPERTIES));

    static {
        SHIMMER_PROPERTIES = new ForgeFlowingFluid.Properties(
                ADJFluidTypes.SHIMMER_FLUID_TYPE,
                SHIMMER,
                FLOWING_SHIMMER
        ).bucket(ADJItems.SHIMMER_BUCKET);
    }

    public static void register(IEventBus bus) {
        FLUIDS.register(bus);
    }
}
