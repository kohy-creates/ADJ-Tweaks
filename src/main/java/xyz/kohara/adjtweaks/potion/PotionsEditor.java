package xyz.kohara.adjtweaks.potion;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import org.apache.logging.log4j.Level;
import xyz.kohara.adjtweaks.ADJTweaks;
import xyz.kohara.adjtweaks.mixins.potions.MobEffectInstanceAccessor;

import java.util.Map;

public class PotionsEditor {

    public static void edit() {
        editDurations(PotionConfigReader.loadPotionConfig());
    }

    private static void forPotion(String potionId, PotionConfigReader.PotionConfig config) {
        ResourceLocation effectResourceLocation = new ResourceLocation(config.effectID);
        MobEffect effect = BuiltInRegistries.MOB_EFFECT.get(effectResourceLocation);

        if (effect == null) {
            ADJTweaks.log(Level.ERROR, "Effect with id " + config.effectID + " not found!");
            return;
        }

        BuiltInRegistries.POTION.get(new ResourceLocation(potionId))
                .getEffects().forEach(mobEffectInstance -> {
                    if (mobEffectInstance.getEffect().equals(effect)) {
                        ((MobEffectInstanceAccessor) mobEffectInstance).setDuration((config.minutes * 60 + config.seconds) * 20);
                        ((MobEffectInstanceAccessor) mobEffectInstance).setAmplifier(config.level - 1);
                    }
                });
    }

    public static void editDurations(Map<String, PotionConfigReader.PotionConfig> potionConfigs) {
        potionConfigs.forEach(PotionsEditor::forPotion);
    }
}