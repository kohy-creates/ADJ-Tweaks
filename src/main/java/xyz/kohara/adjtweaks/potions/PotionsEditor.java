package xyz.kohara.adjtweaks.potions;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.Level;
import xyz.kohara.adjtweaks.ADJTweaks;
import xyz.kohara.adjtweaks.mixins.effect.MobEffectInstanceAccessor;

import java.util.Map;

public class PotionsEditor {

    public static void edit() {
        editDurations(PotionConfigReader.loadPotionConfig());
    }

    private static void forPotion(String potionId, PotionConfigReader.PotionConfig config) {
        String[] id = config.effectID.split(":");
        ResourceLocation effectResourceLocation = ResourceLocation.fromNamespaceAndPath(id[0], id[1]);
        MobEffect effect = ForgeRegistries.MOB_EFFECTS.getValue(effectResourceLocation);

        if (effect == null) {
            ADJTweaks.LOGGER.log(Level.ERROR, "Effect with id " + config.effectID + " not found!");
            return;
        }

        String[] id2 = potionId.split(":");
        ForgeRegistries.POTIONS.getValue(ResourceLocation.fromNamespaceAndPath(id2[0], id2[1]))
                .getEffects().forEach(statusEffectInstance -> {
                    if (statusEffectInstance.getEffect().equals(effect)) {
                        ((MobEffectInstanceAccessor) statusEffectInstance).setDuration((config.minutes * 60 + config.seconds) * 20);
                        ((MobEffectInstanceAccessor) statusEffectInstance).setAmplifier(config.level - 1);
                    }
                });
    }

    public static void editDurations(Map<String, PotionConfigReader.PotionConfig> potionConfigs) {
        potionConfigs.forEach(PotionsEditor::forPotion);
    }
}