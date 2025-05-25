package xyz.kohara.adjtweaks.combat;

public interface KnockbackCooldown {

    default int aDJTweaks$getKnockbackCooldown() {
        throw new RuntimeException();
    }

    default void aDJTweaks$setKnockbackCooldown(int cooldown) {
    }
}
