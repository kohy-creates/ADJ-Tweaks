package xyz.kohara.adjcore.registry.capabilities;

import net.minecraft.nbt.CompoundTag;

public interface IPlayerLoadouts {

    void setCurrentLoadout(int num);

    int getCurrentLoadout();

    void setLoadout(String key, CompoundTag data);

    CompoundTag getLoadout(String key);

    CompoundTag serializeNBT();

    void deserializeNBT(CompoundTag nbt);
}
