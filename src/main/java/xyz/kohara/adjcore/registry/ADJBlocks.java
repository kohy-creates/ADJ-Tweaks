package xyz.kohara.adjcore.registry;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import xyz.kohara.adjcore.misc.LangGenerator;

import java.util.function.Supplier;

public class ADJBlocks {

    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, "adjcore");

    public static final RegistryObject<LiquidBlock> SHIMMER_BLOCK = register("shimmer", "Shimmer",
            () -> new LiquidBlock(ADJFluids.SHIMMER, Block.Properties.copy(Blocks.WATER)
                    .liquid()
                    .lightLevel(value -> 12)
                    .noCollission()
                    .strength(1000f)
                    .emissiveRendering((arg, arg2, arg3) -> true)
            ));

    private static <T extends Block> RegistryObject<T> register(String id, String name, Supplier<T> factory) {
        LangGenerator.addBlockTranslation(id, name);
        return BLOCKS.register(id, factory);
    }

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
    }
}