package xyz.kohara.adjcore.combat.damageevent;

import dev.latvian.mods.kubejs.event.EventJS;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class ADJHurtEventJS extends EventJS {
    private final ADJHurtEvent event;

    public ADJHurtEventJS(ADJHurtEvent event) {
        this.event = event;
    }

    public float getBaseDamage() {
        return this.event.getBaseDamage();
    }

    public float getDamage() {
        return this.event.getDamage();
    }

    public float getCritChance() {
        return this.event.getCritChance();
    }

    public boolean isCritical() {
        return this.event.isCritical();
    }

    public float getCritMultiplier() {
        return this.event.getCritMultiplier();
    }

    public Entity getVictim() {
        return this.event.getVictim();
    }

    public LivingEntity getAttacker() {
        return this.event.getAttacker();
    }

}
