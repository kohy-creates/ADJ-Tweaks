package xyz.kohara.adjcore.registry;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import xyz.kohara.adjcore.ADJCore;
import xyz.kohara.adjcore.misc.LangGenerator;
import xyz.kohara.adjcore.registry.effects.*;

import java.util.function.Supplier;

public class ADJEffects {
    private static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, ADJCore.MOD_ID);
    private static final DeferredRegister<Potion> POTIONS =
            DeferredRegister.create(ForgeRegistries.POTIONS, ADJCore.MOD_ID);

    public static final RegistryObject<MobEffect> COZY_CAMPFIRE = register(
            "cozy_campfire",
            new CozyCampfireEffect(),
            "Cozy Campfire",
            "Slowly regenerates lost life"
    );

    public static final RegistryObject<MobEffect> LESSER_INSTANT_HEALTH = register(
            "lesser_instant_health",
            new LesserInstantHealthEffect(),
            "Lesser Instant Health",
            "Instantly replenishes a small amount of lost health"
    );

    public static final RegistryObject<MobEffect> INSTANT_MANA = register(
            "instant_mana",
            new InstantManaEffect(),
            "Instant Mana",
            "Instantly replenishes some mana"
    );

    public static final RegistryObject<MobEffect> HEART_LANTERN = register(
            "heart_lantern",
            new HeartLanternEffect(),
            "Heart Lantern",
            "Life regeneration increased"
    );
    public static final RegistryObject<MobEffect> FALLEN_KANADE = register(
            "fallen_kanade",
            new FallenKanadeEffect(),
            "Fallen Kanade",
            "Increased life regeneration and reduced negative effect duration"
    );


    private static RegistryObject<MobEffect> register(String id, MobEffect effect, String name, String description) {
        LangGenerator.addEffectTranslation(id, name, description);
        return MOB_EFFECTS.register(id, () -> effect);
    }

    static {
        Potions.register();
    }

    public static void register(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
        POTIONS.register(eventBus);
    }

    public static class Potions {

        public static void register() {
        }

        public static RegistryObject<Potion> LESSER_MANA_POTION = register(
                "lesser_mana",
                "instant_mana",
                () -> new MobEffectInstance(INSTANT_MANA.get(), 1, 0),
                "Lesser Mana"
        );

        public static RegistryObject<Potion> MANA_POTION = register(
                "mana",
                "instant_mana",
                () -> new MobEffectInstance(INSTANT_MANA.get(), 1, 1),
                "Mana"
        );

        public static RegistryObject<Potion> GREATER_MANA_POTION = register(
                "greater_mana",
                "instant_mana",
                () -> new MobEffectInstance(INSTANT_MANA.get(), 1, 2),
                "Greater Mana"
        );

        private static RegistryObject<Potion> register(String id, String langName, Supplier<MobEffectInstance> effect, String name) {
            LangGenerator.addItemTranslation("item.minecraft.potion." + LangGenerator.constructId("effect", langName), "Potion of " + name);
            LangGenerator.addItemTranslation("item.minecraft.splash_potion." + LangGenerator.constructId("effect", langName), "Splash Potion of" + name);
            LangGenerator.addItemTranslation("item.minecraft.lingering_potion." + LangGenerator.constructId("effect", langName), "Lingering Potion of" + name);
            LangGenerator.addItemTranslation("item.minecraft.tipped_arrow." + LangGenerator.constructId("effect", langName), "Arrow of " + name);
            return POTIONS.register(id, () -> new Potion(effect.get()));
        }
    }
}
