package xyz.kohara.adjcore.combat.critevent;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.eventbus.api.Event;
import xyz.kohara.adjcore.kubejs.ServerEvents;

public class ApothCritStrikeEvent extends Event {
    public final LivingEntity attacker;
    public final Entity victim;
    public float baseDamage, critDamage, chance, multiplier;

    public ApothCritStrikeEvent(
            LivingEntity attacker,
            Entity victim,
            float baseDamage,
            float critDamage,
            float chance,
            float multiplier
    ) {
        this.attacker = attacker;
        this.victim = victim;
        this.baseDamage = baseDamage;
        this.critDamage = critDamage;
        this.chance = chance;
        this.multiplier = multiplier;
        ServerEvents.APOTH_CRIT_STRIKE.post(new CritStrikeEventJS(this));
    }
}
