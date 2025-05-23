package xyz.kohara.adjtweaks.effects;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEffects {
    public static final DeferredRegister<StatusEffect> MOB_EFFECTS =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, "adjtweaks");

    public static final RegistryObject<StatusEffect> COZY_CAMPFIRE = MOB_EFFECTS.register("cozy_campfire",
            CozyCampfireEffect::new);

    public static void register(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
    }
}
