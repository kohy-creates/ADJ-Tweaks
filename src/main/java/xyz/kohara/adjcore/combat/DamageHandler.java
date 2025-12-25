package xyz.kohara.adjcore.combat;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import dev.shadowsoffire.attributeslib.AttributesLib;
import dev.shadowsoffire.attributeslib.api.ALCombatRules;
import dev.shadowsoffire.attributeslib.api.ALObjects;
import dev.shadowsoffire.attributeslib.packet.CritParticleMessage;
import dev.shadowsoffire.placebo.network.PacketDistro;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;
import xyz.kohara.adjcore.Config;
import xyz.kohara.adjcore.registry.ADJAttributes;
import xyz.kohara.adjcore.client.networking.ADJMessages;
import xyz.kohara.adjcore.client.networking.packet.EnchantedCritParticleS2CPacket;
import xyz.kohara.adjcore.misc.events.ADJHurtEvent;

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

        for (String id : IFRAMES.keySet()) {
            IFrameConfig cfg = IFRAMES.get(id);
            if (id.startsWith("#")) {
                TagKey<DamageType> damageTypeTag = TagKey.create(Registries.DAMAGE_TYPE, ResourceLocation.parse(id.substring(1)));
                if (source.is(damageTypeTag)) {
                    finalIFrames = cfg.iframes;
                    if (attacker != null) {
                        String attackerId = ForgeRegistries.ENTITY_TYPES.getKey(attacker.getType()).toString();
                        if (cfg.overrides.containsKey(attackerId)) {
                            finalIFrames = cfg.overrides.get(attackerId);
                        }
                    }
                }
            } else {
                if (source.is(ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.parse(id)))) {
                    finalIFrames = cfg.iframes;
                    if (attacker != null) {
                        String attackerId = ForgeRegistries.ENTITY_TYPES.getKey(attacker.getType()).toString();
                        if (cfg.overrides.containsKey(attackerId)) {
                            finalIFrames = cfg.overrides.get(attackerId);
                        }
                    }
                    break;
                }
            }
        }

        if (finalIFrames < 0) {
            finalIFrames = 10;
        }

        INVUL_TIME = finalIFrames;
        setInvulTime(entity, INVUL_TIME);
    }

    private static float getAttributeValue(LivingEntity entity, Attribute attribute) {
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

    public static void handleLivingHurt(LivingHurtEvent event) {
        DamageSource source = event.getSource();
        LivingEntity victimEntity = event.getEntity();
        Entity attackerEntity = source.getEntity();
        float baseAmount = event.getAmount();

        // Whenever attackerEntity is needed as a LivingEntity
        LivingEntity livingAttacker = (attackerEntity instanceof LivingEntity le) ? le : null;

        // 0. Prevent damaging tamed animals
        if (victimEntity instanceof TamableAnimal tamableAnimal) {
            if (tamableAnimal.getOwner() == attackerEntity && !attackerEntity.isShiftKeyDown()) {
                event.setAmount(0);
//                event.setCanceled(true);
                return;
            }
        }

        // 1. Apply damage multipliers from config
        float finalAmount = baseAmount;
        for (String id : MULTIPLIERS.keySet()) {
            if (id.startsWith("#")) {
                TagKey<DamageType> damageTypeTag = TagKey.create(Registries.DAMAGE_TYPE, ResourceLocation.parse(id.substring(1)));
                if (source.is(damageTypeTag)) {
                    finalAmount *= MULTIPLIERS.get(id);
                }
            } else {
                if (source.is(ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.parse(id)))) {
                    finalAmount = baseAmount;
                    finalAmount *= MULTIPLIERS.get(id);
                    break;
                }
            }
        }

        // 1.5. Apply enchantment multipliers
        if (livingAttacker != null) {
            ItemStack weapon = livingAttacker.getMainHandItem();

            final int sharpness = weapon.getEnchantmentLevel(Enchantments.SHARPNESS);
            int isMagic = sharpness;
            if (sharpness > 0) {
                finalAmount += finalAmount * (float) (sharpness * 0.1);
            }

            if (victimEntity.getMobType() == MobType.UNDEAD) {
                final int smite = weapon.getEnchantmentLevel(Enchantments.SMITE);
                if (smite > 0) {
                    isMagic += smite;
                    finalAmount += finalAmount * (float) (smite * 0.15);
                }
            } else if (victimEntity.getMobType() == MobType.ARTHROPOD) {
                final int bane = weapon.getEnchantmentLevel(Enchantments.BANE_OF_ARTHROPODS);
                if (bane > 0) {
                    isMagic += bane;
                    finalAmount += finalAmount * (float) (bane * 0.15);
                }
            }

            if (victimEntity.isInWater() || victimEntity.level().isRainingAt(victimEntity.blockPosition())) {
                final int impaling = weapon.getEnchantmentLevel(Enchantments.IMPALING);
                if (impaling > 0) {
                    isMagic += impaling;
                    finalAmount *= (float) (1 + impaling * 0.1);
                }
            }

            if (isMagic > 0 && !livingAttacker.level().isClientSide()) {
                livingAttacker.level().getServer().getPlayerList().getPlayers().forEach(viewer -> {
                    ADJMessages.sendToPlayer(
                            new EnchantedCritParticleS2CPacket(victimEntity.getId()),
                            viewer
                    );
                });
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
            finalAmount *= 1 - getAttributeValue(victimEntity, ADJAttributes.DAMAGE_REDUCTION.get());
            if (source.is(DamageTypeTags.IS_PROJECTILE)) {
                finalAmount *= 1 - getAttributeValue(victimEntity, ADJAttributes.PROJECTILE_DAMAGE_REDUCTION.get());
            }
        }

        finalAmount *= ALCombatRules.getProtDamageReduction(EnchantmentHelper.getDamageProtection(victimEntity.getArmorSlots(), source));

        int armorPoints = (int) getAttributeValue(victimEntity, Attributes.ARMOR);
        if (!source.is(DamageTypeTags.BYPASSES_ARMOR)) {
            int armorToughness = (int) getAttributeValue(victimEntity, Attributes.ARMOR_TOUGHNESS);

            if (attackerEntity instanceof LivingEntity attacker) {
                int armorPierce = (int) getAttributeValue(attacker, ALObjects.Attributes.ARMOR_PIERCE.get());
                float armorShred = Math.max(1 - getAttributeValue(attacker, ALObjects.Attributes.ARMOR_SHRED.get()), 0);
                armorPierce = Math.max(0, armorPierce - armorToughness);
                armorPoints = Math.max(Math.round(armorPoints * armorShred) - armorPierce, 0);
            }

            float factor = (victimEntity instanceof Player) ? ARMOR_POINT_FACTOR.get() : ARMOR_POINT_FACTOR_ENTITY.get();
            finalAmount = Math.max(MIN_DAMAGE.get(), finalAmount - (armorPoints / factor));
        }

        // 4. Ensure minimum damage
        finalAmount = (float) Math.max(Math.ceil(finalAmount), MIN_DAMAGE.get());

        // 5. Handle crits
        //    Logic adapted from AttributesLib

        boolean isCrit = false;
        double critChance = 0d;
        float critMult = 1.0F;

        if (livingAttacker != null) {

            critChance = livingAttacker.getAttributeValue(ALObjects.Attributes.CRIT_CHANCE.get());
            float critDmg = (float) livingAttacker.getAttributeValue(ALObjects.Attributes.CRIT_DAMAGE.get());

            RandomSource rand = event.getEntity().getRandom();

            // Roll for crits. Each overcrit reduces the effectiveness by 15%
            // We stop rolling when crit chance fails or the crit damage would reduce the total damage dealt.
            while (rand.nextFloat() <= critChance && critDmg > 1.0F) {
                critChance--;
                critMult *= critDmg;
                critDmg *= 0.85F;
            }

            finalAmount *= critMult;
            event.setAmount(finalAmount);

            if (critMult > 1) {
                isCrit = true;
                if (!livingAttacker.level().isClientSide) {
                    PacketDistro.sendToTracking(AttributesLib.CHANNEL, new CritParticleMessage(event.getEntity().getId()), (ServerLevel) livingAttacker.level(), event.getEntity().blockPosition());
                }
            }
        }

        // 6. Fire event and edit amount
        event.setAmount(finalAmount);

        ADJHurtEvent eventHook = new ADJHurtEvent(
                livingAttacker,
                event.getEntity(),
                baseAmount,
                finalAmount,
                isCrit,
                (float) critChance,
                critMult
        );
        MinecraftForge.EVENT_BUS.post(eventHook);

    }

    public static class IFrameConfig {
        public int iframes;
        public Map<String, Integer> overrides = new HashMap<>();
    }
}
