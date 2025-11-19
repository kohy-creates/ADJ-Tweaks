package xyz.kohara.adjcore.misc.events;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.eventbus.api.Event;
import xyz.kohara.adjcore.kubejs.ServerEvents;
import xyz.kohara.adjcore.kubejs.serverevents.ADJHurtEventJS;

import javax.annotation.Nullable;

public class ADJHurtEvent extends Event {
    private final LivingEntity attacker;
    private final Entity victim;
    private final float baseDamage,
            damage,
            chance,
            multiplier;
    private final boolean critical;

    public ADJHurtEvent(@Nullable LivingEntity attacker,
                        Entity victim,
                        float baseDamage,
                        float finalDamage,
                        boolean critical,
                        float critChance,
                        float critMultiplier
    ) {
        this.attacker = attacker;
        this.victim = victim;
        this.baseDamage = baseDamage;
        this.damage = finalDamage;
        this.critical = critical;
        this.chance = critChance;
        this.multiplier = critMultiplier;

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
}
