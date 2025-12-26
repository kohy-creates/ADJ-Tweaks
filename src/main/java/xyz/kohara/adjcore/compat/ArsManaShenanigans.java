package xyz.kohara.adjcore.compat;

public interface ArsManaShenanigans {

    default int adjcore$getManaRegenDelay() {
        throw new RuntimeException();
    }

    default void adjcore$setManaRegenDelay(int cooldown) {
    }

    default void adjcore$increaseManaRegenCounter(int amount) {
    }

    default int adjcore$getManaRegenCounter() {
        throw new RuntimeException();
    }

}
