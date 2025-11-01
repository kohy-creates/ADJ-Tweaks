package xyz.kohara.adjcore.events;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import xyz.kohara.adjcore.kubejs.serverevents.ADJHurtEventJS;
import xyz.kohara.adjcore.kubejs.ServerEvents;

import javax.annotation.Nullable;

public class ADJHurtEvent extends LivingHurtEvent {
    private final LivingEntity attacker;
    private final float baseDamage;
    private final boolean critical;
    private final float critChance;
    private final float critMultiplier;

    public ADJHurtEvent(
            @Nullable LivingEntity attacker,
            LivingEntity victim,
            DamageSource source,
            float baseDamage,
            float finalDamage,
            boolean critical,
            float critChance,
            float critMultiplier
    ) {
        super(victim, source, finalDamage);

        this.attacker = attacker;
        this.baseDamage = baseDamage;
        this.critical = critical;
        this.critChance = critChance;
        this.critMultiplier = critMultiplier;

        ServerEvents.ADJ_HURT.post(new ADJHurtEventJS(this));
    }

    public boolean isCritical() {
        return critical;
    }

    public float getBaseDamage() {
        return baseDamage;
    }

    public float getCritChance() {
        return critChance;
    }

    public float getCritMultiplier() {
        return critMultiplier;
    }

    @Nullable
    public LivingEntity getAttacker() {
        return attacker;
    }

    public LivingEntity getVictim() {
        return getEntity();
    }
}
