package xyz.kohara.adjtweaks.combat;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import xyz.kohara.adjtweaks.Config;

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
        event.setAmount((float) (event.getAmount() * multiplier));
    }
}
