package xyz.kohara.adjcore.registry.capabilities;

import net.minecraft.nbt.CompoundTag;

import java.util.HashMap;
import java.util.Map;

public class PlayerLoadoutsProvider implements IPlayerLoadouts {
    private int currentLoadout = 1;
    private final Map<String, CompoundTag> loadouts = new HashMap<>();

    @Override
    public void setCurrentLoadout(int num) {
        this.currentLoadout = num;
    }

    @Override
    public int getCurrentLoadout() {
        return currentLoadout;
    }

    @Override
    public void setLoadout(String key, CompoundTag data) {
        loadouts.put(key, data.copy());
    }

    @Override
    public CompoundTag getLoadout(String key) {
        return loadouts.getOrDefault(key, new CompoundTag());
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("CurrentLoadout", currentLoadout);

        CompoundTag all = new CompoundTag();
        loadouts.forEach(all::put);
        tag.put("Loadouts", all);

        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        currentLoadout = nbt.getInt("CurrentLoadout");
        loadouts.clear();
        CompoundTag all = nbt.getCompound("Loadouts");
        for (String key : all.getAllKeys()) {
            loadouts.put(key, all.getCompound(key));
        }
    }
}