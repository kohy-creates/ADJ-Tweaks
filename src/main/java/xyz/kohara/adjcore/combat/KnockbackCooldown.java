package xyz.kohara.adjcore.combat;

public interface KnockbackCooldown {

    default int adjcore$getKnockbackCooldown() {
        throw new RuntimeException();
    }

    default void adjcore$setKnockbackCooldown(int cooldown) {
    }
}
