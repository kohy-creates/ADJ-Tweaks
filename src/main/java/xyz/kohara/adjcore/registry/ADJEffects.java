package xyz.kohara.adjcore.registry;

import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import xyz.kohara.adjcore.ADJCore;
import xyz.kohara.adjcore.misc.LangGenerator;
import xyz.kohara.adjcore.registry.effects.CozyCampfireEffect;

public class ADJEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, ADJCore.MOD_ID);

    public static final RegistryObject<MobEffect> COZY_CAMPFIRE = register(
            "cozy_campfire",
            new CozyCampfireEffect(),
            "Cozy Campfire",
            "Slowly regenerates lost life"
    );

    private static RegistryObject<MobEffect> register(String id, MobEffect effect, String name, String description) {
        LangGenerator.addEffectTranslation("effect.adjcore." + id, name, description);
        return MOB_EFFECTS.register(id, () -> effect);
    }

    public static void register(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
    }
}
