package xyz.kohara.adjcore.misc.events;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.eventbus.api.Event;
import xyz.kohara.adjcore.compat.kubejs.ServerEvents;
import xyz.kohara.adjcore.compat.kubejs.serverevents.ADJHurtEventJS;
import xyz.kohara.adjcore.misc.ParticleTextIndicators;

import javax.annotation.Nullable;

public class ADJHurtEvent extends Event {
    private final LivingEntity attacker;
    private final Entity victim;
    private final float baseDamage,
            damage,
            chance,
            multiplier;
    private final boolean critical;
    private @Nullable ParticleTextIndicators.Type style;
    private final DamageSource source;

    public ADJHurtEvent(@Nullable LivingEntity attacker,
                        Entity victim,
                        float baseDamage,
                        float finalDamage,
                        boolean critical,
                        float critChance,
                        float critMultiplier,
                        DamageSource source
    ) {
        this.attacker = attacker;
        this.victim = victim;
        this.baseDamage = baseDamage;
        this.damage = finalDamage;
        this.critical = critical;
        this.chance = critChance;
        this.multiplier = critMultiplier;
        this.style = null;
        this.source = source;

        if (ServerEvents.ADJ_HURT.hasListeners())
            ServerEvents.ADJ_HURT.post(new ADJHurtEventJS(this));
    }

    public boolean isCritical() {
        return this.critical;
    }

    public float getBaseDamage() {
        return (isCritical() ? this.baseDamage : Float.NaN);
    }

    public float getDamage() {
        return this.damage;
    }

    public float getCritChance() {
        return this.chance;
    }

    public float getCritMultiplier() {
        return this.multiplier;
    }

    public LivingEntity getAttacker() {
        return this.attacker;
    }

    public Entity getVictim() {
        return this.victim;
    }

    public ParticleTextIndicators.Type getStyle() {
        return this.style;
    }

    public void setStyle(int id) {
        this.style = ParticleTextIndicators.Type.fromValue(id);
    }

    public DamageSource getSource() {
        return this.source;
    }
}
