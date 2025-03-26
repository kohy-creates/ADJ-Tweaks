package xyz.kohara.adjtweaks.potions;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.Level;
import xyz.kohara.adjtweaks.ADJTweaks;
import xyz.kohara.adjtweaks.mixins.effect.StatusEffectInstanceAccessor;

import java.util.Map;

public class PotionsEditor {

    public static void edit() {
        editDurations(PotionConfigReader.loadPotionConfig());
    }

    private static void forPotion(String potionId, PotionConfigReader.PotionConfig config) {
        Identifier effectResourceLocation = new Identifier(config.effectID);
        StatusEffect effect = Registries.STATUS_EFFECT.get(effectResourceLocation);

        if (effect == null) {
            ADJTweaks.LOGGER.log(Level.ERROR, "Effect with id " + config.effectID + " not found!");
            return;
        }

        Registries.POTION.get(new Identifier(potionId))
                .getEffects().forEach(statusEffectInstance -> {
                    if (statusEffectInstance.getEffectType().equals(effect)) {
                        ((StatusEffectInstanceAccessor) statusEffectInstance).setDuration((config.minutes * 60 + config.seconds) * 20);
                        ((StatusEffectInstanceAccessor) statusEffectInstance).setAmplifier(config.level - 1);
                    }
                });
    }

    public static void editDurations(Map<String, PotionConfigReader.PotionConfig> potionConfigs) {
        potionConfigs.forEach(PotionsEditor::forPotion);
    }
}