package xyz.kohara.adjcore.misc.placementmodifiertypes;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import xyz.kohara.adjcore.ADJCore;

public class ModPlacementModifierTypes {

    public static final DeferredRegister<PlacementModifierType<?>> PLACEMENT_MODIFIER_TYPES = DeferredRegister.create(Registries.PLACEMENT_MODIFIER_TYPE, ADJCore.MOD_ID);

    public static final RegistryObject<PlacementModifierType<IsHardcorePlacement>> IS_HARDCORE = PLACEMENT_MODIFIER_TYPES.register("is_hardcore", () -> IsHardcorePlacement.CODEC::codec);

    public static void register(IEventBus eventBus) {
        PLACEMENT_MODIFIER_TYPES.register(eventBus);
    }
}
