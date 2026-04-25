package xyz.kohara.adjcore.misc.events;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Explosion;
import net.minecraftforge.eventbus.api.Event;
import oshi.util.tuples.Pair;
import xyz.kohara.adjcore.compat.kubejs.ServerEvents;
import xyz.kohara.adjcore.compat.kubejs.serverevents.ADJExplosionDamageCalcEventJS;

import javax.annotation.Nullable;

public class ADJExplosionDamageCalcEvent extends Event {

    private final Explosion explosion;
    private final Entity affectedEntity;
    private final @Nullable Entity sourceEntity;
    private final @Nullable LivingEntity indirectSourceEntity;
    private final double distance;
    private final double percentageSeen;
    private final double range;

    private Pair<Double, Double> falloffRange = new Pair<>(null, null);
    private double falloffMinMultiplier = 1;
    private double damage = 80;

    public ADJExplosionDamageCalcEvent(Explosion explosion, Entity entity, @Nullable Entity sourceEntity, @Nullable LivingEntity indirectSourceEntity, double distance, double percentageSeen, double range) {
        this.explosion = explosion;
        this.affectedEntity = entity;
        this.sourceEntity = sourceEntity;
        this.indirectSourceEntity = indirectSourceEntity;
        this.distance = distance;
        this.percentageSeen = percentageSeen;
        this.range = range;

        if (ServerEvents.EXPLOSION_DAMAGE_CALC.hasListeners()) {
            ServerEvents.EXPLOSION_DAMAGE_CALC.post(new ADJExplosionDamageCalcEventJS(this));
        }
    }

    public void setFalloff(@Nullable Double falloffStart, @Nullable Double falloffEnd, double minValue) {
        this.falloffRange = new Pair<>(falloffStart, falloffEnd);
        this.falloffMinMultiplier = minValue;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    public double calculateDamage() {
        var fStart = this.falloffRange.getA();
        var fEnd = this.falloffRange.getB();

        if (fStart == null) fStart = this.range;
        if (fEnd == null) fEnd = this.range;

        if (distance <= fStart * this.range) return this.damage;
        else if (distance >= fEnd * this.range) return this.damage * this.falloffMinMultiplier;
        else {
            var falloffRange = (fEnd - fStart) * this.range;
            var distInFalloff = this.distance - (fStart) * this.range;
            var distMul = distInFalloff / falloffRange;
            var damageMul = 1 - distMul * (1 - this.falloffMinMultiplier);
            return this.damage * damageMul * this.percentageSeen;
        }
    }

    public double getPercentageSeen() {
        return this.percentageSeen;
    }

    public double getDistance() {
        return this.distance;
    }

    public Explosion getExplosion() {
        return this.explosion;
    }

    public Entity getAffectedEntity() {
        return this.affectedEntity;
    }

    @Nullable
    public Entity getSourceEntity() {
        return this.sourceEntity;
    }

    @Nullable
    public LivingEntity getIndirectSourceEntity() {
        return this.indirectSourceEntity;
    }

    public double getRange() {
        return this.range;
    }
}
