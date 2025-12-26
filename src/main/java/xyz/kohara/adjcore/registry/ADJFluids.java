package xyz.kohara.adjcore.registry;

import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import xyz.kohara.adjcore.ADJCore;
import xyz.kohara.adjcore.registry.fluids.ShimmerFluid;

public class ADJFluids {

    public static final DeferredRegister<Fluid> FLUIDS =
            DeferredRegister.create(ForgeRegistries.FLUIDS, ADJCore.MOD_ID);

    public static final RegistryObject<FlowingFluid> SHIMMER =
            FLUIDS.register("shimmer", () -> new ShimmerFluid.Source(ADJFluids.SHIMMER_PROPERTIES));

    public static final RegistryObject<FlowingFluid> FLOWING_SHIMMER =
            FLUIDS.register("flowing_shimmer", () -> new ShimmerFluid.Flowing(ADJFluids.SHIMMER_PROPERTIES));

    public static final ForgeFlowingFluid.Properties SHIMMER_PROPERTIES = new ForgeFlowingFluid.Properties(
            ADJFluidTypes.SHIMMER_FLUID_TYPE,
            SHIMMER,
            FLOWING_SHIMMER)
            .bucket(ADJItems.SHIMMER_BUCKET)
            .block(ADJBlocks.SHIMMER_BLOCK);

    public static void register(IEventBus bus) {
        FLUIDS.register(bus);
    }
}
