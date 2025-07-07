package xyz.kohara.adjcore.combat;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import xyz.kohara.adjcore.Config;

import java.util.List;
import java.util.function.Supplier;

public class VariatedDamage {

    private static final List<TagKey<DamageType>> DISALLOWED_TAGS = List.of(
            DamageTypeTags.BYPASSES_ARMOR,
            DamageTypeTags.BYPASSES_RESISTANCE,
            DamageTypeTags.BYPASSES_EFFECTS,
            DamageTypeTags.BYPASSES_INVULNERABILITY
    );
    private static final Supplier<Double> VARIATION = () -> Config.RANDOM_DAMAGE_VARIATION.get() / 100d;

    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {
        DamageSource source = event.getSource();
        for (TagKey<DamageType> tag : DISALLOWED_TAGS) {
            if (source.is(tag)) {
                return;
            }
        }
        double min = 1d - VARIATION.get(),
                max = 1d + VARIATION.get(),
                multiplier = min + Math.random() * (max - min);
        Entity sourceEntity = event.getSource().getEntity();
        if (sourceEntity instanceof LivingEntity livingEntity) {
            AttributeInstance luck = livingEntity.getAttribute(Attributes.LUCK);
            if (luck != null) {
                if (luck.getValue() > 0) {
                    int luckValue = (int) Math.ceil(luck.getValue());
                    for (int i = 0; i < luckValue; i++) {
                        if (Math.random() < 0.5) {
                            //System.out.println("50% chance failed, skipping loop");
                            continue;
                        }
                        double multiplierTest = min + Math.random() * (max - min);
                        if (multiplierTest > multiplier) {
                            //System.out.println("Was " + multiplier + ". Replacing with " + multiplierTest);
                            multiplier = multiplierTest;
                        } //else System.out.println("Was smaller, not replacing.");
                    }
                } else if (luck.getValue() < 0) {
                    int luckValue = (int) (Math.floor(luck.getValue()) * -1);
                    for (int i = 0; i < luckValue; i++) {
                        if (Math.random() < 0.5) {
                            //System.out.println("50% chance failed, skipping loop");
                            continue;
                        }
                        double multiplierTest = min + Math.random() * (max - min);
                        if (multiplierTest < multiplier) {
                            //System.out.println("Was " + multiplier + ". Replacing with " + multiplierTest);
                            multiplier = multiplierTest;
                        } //else System.out.println("Was larger, not replacing.");
                    }
                }
            }
        }
        event.setAmount((float) (event.getAmount() * multiplier));
    }
}
