package xyz.kohara.adjcore.entity;

public interface IHealTime {

    default int adjcore$getHealTime() {
        throw new RuntimeException();
    }

    default void adjcore$setHealTime(int time) {
    }
}
