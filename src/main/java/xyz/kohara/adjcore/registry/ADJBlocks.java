package xyz.kohara.adjcore.registry;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ADJBlocks {

    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, "adjcore");

    public static final RegistryObject<LiquidBlock> SHIMMER_BLOCK =
            BLOCKS.register("shimmer",
                    () -> new LiquidBlock(ADJFluids.SHIMMER, Block.Properties.copy(Blocks.WATER)
                            .noCollission()
                            .strength(100f)
                    ));

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
    }
}