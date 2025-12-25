package xyz.kohara.adjcore.registry.fluids.types;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.fluids.FluidType;

import java.util.function.Consumer;

public class ShimmerFluidType extends FluidType {
    public ShimmerFluidType(Properties properties) {
        super(
                Properties.create()
                        .density(2500)
                        .viscosity(2500)
                        .lightLevel(13)
                        .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
                        .sound(SoundActions.FLUID_VAPORIZE, SoundEvents.GENERIC_EXTINGUISH_FIRE)
                        .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                        .canConvertToSource(false)
                        .canDrown(false)
                        .canHydrate(false)
                        .fallDistanceModifier(0.5f)
                        .rarity(Rarity.EPIC)
                        .canExtinguish(true)
                        .canSwim(false)
        );
    }

    @Override
    public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
    }

}
