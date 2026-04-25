package xyz.kohara.adjcore.compat.kubejs.serverevents;

import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Explosion;
import xyz.kohara.adjcore.misc.events.ADJExplosionDamageCalcEvent;

import javax.annotation.Nullable;

public class ADJExplosionDamageCalcEventJS extends EventJS {

    private final ADJExplosionDamageCalcEvent event;

    public ADJExplosionDamageCalcEventJS(ADJExplosionDamageCalcEvent event) {
        this.event = event;
    }

    @Info("""
            Sets damage falloff values for the calculated explosion.
            First value is the range at which falloff starts, second is the range where falloff stops.
            Third value is the percentage of the damage the explosion will deal beyond the falloff range.
            """)
    public void setFalloff(double falloffStart, double falloffEnd, double minValue) {
        this.event.setFalloff(falloffStart, falloffEnd, minValue);
    }

    public void setDamage(double damage) {
        this.event.setDamage(damage);
    }
    public double getPercentageSeen() {
        return this.event.getPercentageSeen();
    }

    public double getDistance() {
        return this.event.getDistance();
    }

    public Explosion getExplosion() {
        return this.event.getExplosion();
    }

    public Entity getAffectedEntity() {
        return this.event.getAffectedEntity();
    }

    @Nullable
    public Entity getSourceEntity() {
        return this.event.getSourceEntity();
    }

    @Nullable
    public LivingEntity getIndirectSourceEntity() {
        return this.event.getIndirectSourceEntity();
    }

    public double getRange() {
        return this.event.getRange();
    }
}
