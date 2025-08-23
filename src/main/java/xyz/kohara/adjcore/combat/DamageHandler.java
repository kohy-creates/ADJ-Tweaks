package xyz.kohara.adjcore.combat;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import dev.shadowsoffire.attributeslib.api.ALObjects;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;
import xyz.kohara.adjcore.ADJCore;
import xyz.kohara.adjcore.Config;
import xyz.kohara.adjcore.attributes.ModAttributes;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class DamageHandler {

    private static int INVUL_TIME;

    private static final List<TagKey<DamageType>> DISALLOWED_TAGS = List.of(
            DamageTypeTags.BYPASSES_ARMOR,
            DamageTypeTags.BYPASSES_RESISTANCE,
            DamageTypeTags.BYPASSES_EFFECTS,
            DamageTypeTags.BYPASSES_INVULNERABILITY
    );
    private static final Supplier<Double> VARIATION = () -> Config.RANDOM_DAMAGE_VARIATION.get() / 100d;

    private static final Supplier<Float> MIN_DAMAGE = () -> Config.MIN_DAMAGE_TAKEN.get().floatValue();
    private static final Supplier<Float> ARMOR_POINT_FACTOR = () -> Config.ARMOR_POINT_REDUCTION_FACTOR.get().floatValue();
    private static final Supplier<Float> ARMOR_POINT_FACTOR_ENTITY = () -> Config.ARMOR_POINT_REDUCTION_FACTOR_ENTITY.get().floatValue();

    private static final String PATH = "config/adjcore/damage_multipliers.json";
    public static Map<String, Double> MULTIPLIERS = new HashMap<>();

    public static Map<String, IFrameConfig> IFRAMES = new HashMap<>();
    public static final String IFRAMES_PATH = "config/adjcore/iframes.json";

    static {
        loadConfig();
        loadIFrameConfig();
    }

    public static void setInvulTime(LivingEntity entity, int time) {
        entity.invulnerableTime = time * 2;
        entity.hurtTime = entity.hurtDuration = time;
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onLivingKnockback(LivingKnockBackEvent event) {
        LivingEntity entity = event.getEntity();

        int cooldown = entity.adjcore$getKnockbackCooldown();
        if (cooldown > 0) {
            float s = event.getStrength();
            s = Math.max(0f, s - s * cooldown / 15f);
            event.setStrength(s);
        } else if (cooldown == 0) {
            entity.adjcore$setKnockbackCooldown(INVUL_TIME != 0 ? INVUL_TIME * 2 : 5);
        }
        setInvulTime(entity, INVUL_TIME);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void adjustIFrames(LivingHurtEvent event) {
        DamageSource source = event.getSource();
        LivingEntity entity = event.getEntity();
        Entity attacker = source.getEntity();

        int finalIFrames = -1;

//        ADJCore.LOGGER.info("Handling damage event: source={}, entity={}, attacker={}",
//                source.type().msgId(),
//                entity.getType(),
//                attacker != null ? ForgeRegistries.ENTITY_TYPES.getKey(attacker.getType()) : "none");

        for (String id : IFRAMES.keySet()) {
            IFrameConfig cfg = IFRAMES.get(id);
//            ADJCore.LOGGER.info("Checking config entry: {}", id);

            if (id.startsWith("#")) {
                TagKey<DamageType> damageTypeTag = TagKey.create(Registries.DAMAGE_TYPE, ResourceLocation.parse(id.substring(1)));
                if (source.is(damageTypeTag)) {
                    finalIFrames = cfg.iframes;
//                    ADJCore.LOGGER.info("Matched tag {} with base iframes={}", id, finalIFrames);

                    if (attacker != null) {
                        String attackerId = ForgeRegistries.ENTITY_TYPES.getKey(attacker.getType()).toString();
                        if (cfg.overrides.containsKey(attackerId)) {
                            finalIFrames = cfg.overrides.get(attackerId);
//                            ADJCore.LOGGER.info("Applied override for attacker {} -> iframes={}", attackerId, finalIFrames);
                        }
                    }
                }
            } else {
                if (source.is(ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.parse(id)))) {
                    finalIFrames = cfg.iframes;
//                    ADJCore.LOGGER.info("Matched damage type {} with base iframes={}", id, finalIFrames);

                    if (attacker != null) {
                        String attackerId = ForgeRegistries.ENTITY_TYPES.getKey(attacker.getType()).toString();
                        if (cfg.overrides.containsKey(attackerId)) {
                            finalIFrames = cfg.overrides.get(attackerId);
//                            ADJCore.LOGGER.info("Applied override for attacker {} -> iframes={}", attackerId, finalIFrames);
                        }
                    }
                    break;
                }
            }
        }

        if (finalIFrames >= 0) {
//            ADJCore.LOGGER.info("Setting invulnerability time for {} -> {}", entity.getName().getString(), finalIFrames);
            INVUL_TIME = finalIFrames;
            setInvulTime(entity, INVUL_TIME);
        } // else {
//            ADJCore.LOGGER.info("No iframe config matched for this damage event.");
//        }
    }


    private static float getValue(LivingEntity entity, Attribute attribute) {
        AttributeInstance instance = entity.getAttribute(attribute);
        return instance != null ? (float) instance.getValue() : 0;
    }

    public static void loadConfig() {
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, Double>>() {
        }.getType();

        if (!Files.exists(Path.of(PATH))) return;

        try (FileReader reader = new FileReader(PATH)) {
            MULTIPLIERS = gson.fromJson(reader, type);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void loadIFrameConfig() {
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, IFrameConfig>>() {
        }.getType();

        if (!Files.exists(Path.of(IFRAMES_PATH))) return;

        try (FileReader reader = new FileReader(IFRAMES_PATH)) {
            IFRAMES = gson.fromJson(reader, type);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void handleLivingHurt(LivingHurtEvent event) {
        DamageSource source = event.getSource();
        LivingEntity entity = event.getEntity();
        Entity attackerEntity = source.getEntity();
        float amount = event.getAmount();

        // 1. Apply damage multipliers from config
        float finalAmount = amount;
        for (String id : MULTIPLIERS.keySet()) {
            if (id.startsWith("#")) {
                TagKey<DamageType> damageTypeTag = TagKey.create(Registries.DAMAGE_TYPE, ResourceLocation.parse(id.substring(1)));
                if (source.is(damageTypeTag)) {
                    finalAmount *= MULTIPLIERS.get(id);
                }
            } else {
                if (source.is(ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.parse(id)))) {
                    finalAmount = amount;
                    finalAmount *= MULTIPLIERS.get(id);
                    break;
                }
            }
        }

        // 2. Apply variation
        if (DISALLOWED_TAGS.stream().noneMatch(source::is)) {
            double min = 1d - VARIATION.get();
            double max = 1d + VARIATION.get();
            double multiplier = min + Math.random() * (max - min);

            if (attackerEntity instanceof LivingEntity attacker) {
                AttributeInstance luck = attacker.getAttribute(Attributes.LUCK);
                if (luck != null) {
                    int luckValue = (int) Math.abs(luck.getValue());
                    for (int i = 0; i < luckValue; i++) {
                        if (Math.random() < 0.5) continue;
                        double test = min + Math.random() * (max - min);
                        multiplier = luck.getValue() > 0 ? Math.max(multiplier, test) : Math.min(multiplier, test);
                    }
                }
            }
            finalAmount *= (float) multiplier;
        }

        // 3. Apply combat rules
        if (!source.is(DamageTypeTags.BYPASSES_RESISTANCE)) {
            finalAmount *= 1 - getValue(entity, ModAttributes.DAMAGE_REDUCTION.get());
            if (source.is(DamageTypeTags.IS_PROJECTILE)) {
                finalAmount *= 1 - getValue(entity, ModAttributes.PROJECTILE_DAMAGE_REDUCTION.get());
            }
        }
        if (!source.is(DamageTypeTags.BYPASSES_ARMOR)) {
            int armorPoints = (int) getValue(entity, Attributes.ARMOR);
            int armorToughness = (int) getValue(entity, Attributes.ARMOR_TOUGHNESS);

            if (attackerEntity instanceof LivingEntity attacker) {
                int armorPierce = (int) getValue(attacker, ALObjects.Attributes.ARMOR_PIERCE.get());
                float armorShred = Math.max(1 - getValue(attacker, ALObjects.Attributes.ARMOR_SHRED.get()), 0);
                armorPierce = Math.max(0, armorPierce - armorToughness);
                armorPoints = Math.max(Math.round(armorPoints * armorShred) - armorPierce, 0);
            }

            float factor = (entity instanceof Player) ? ARMOR_POINT_FACTOR.get() : ARMOR_POINT_FACTOR_ENTITY.get();
            finalAmount = Math.max(MIN_DAMAGE.get(), finalAmount - (armorPoints / factor));
        }

        // 4. Ensure minimum damage for bypass types
        event.setAmount(Math.max(Math.round(finalAmount), MIN_DAMAGE.get()));
    }

    public static class IFrameConfig {
        public int iframes;
        public Map<String, Integer> overrides = new HashMap<>();
    }
}
