package xyz.kohara.adjcore.combat.critevent;

import dev.latvian.mods.kubejs.event.EventJS;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class CritStrikeEventJS extends EventJS {
    private final ApothCritStrikeEvent event;

    public CritStrikeEventJS(ApothCritStrikeEvent event) {
        this.event = event;
    }

    public float getBaseDamage() {
        return event.baseDamage;
    }

    public float getCritDamage() {
        return event.critDamage;
    }

    public float getChance() {
        return event.chance;
    }

    public float getMultiplier() {
        return event.multiplier;
    }

    public Entity getVictim() {
        return event.victim;
    }

    public LivingEntity getAttacker() {
        return event.attacker;
    }

}
