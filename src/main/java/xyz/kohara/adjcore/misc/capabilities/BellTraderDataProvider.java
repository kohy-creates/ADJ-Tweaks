package xyz.kohara.adjcore.misc.capabilities;

import net.minecraft.core.Direction;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BellTraderDataProvider implements ICapabilityProvider {

    public static final Capability<IBellTraderData> BELL_TRADER_CAP =
            CapabilityManager.get(new CapabilityToken<>() {});

    private final BellTraderData data = new BellTraderData();
    private final LazyOptional<IBellTraderData> optional = LazyOptional.of(() -> data);

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return cap == BELL_TRADER_CAP ? optional.cast() : LazyOptional.empty();
    }

    public Tag serializeNBT() {
        return LongTag.valueOf(data.getLastBellDay());
    }

    public void deserializeNBT(Tag tag) {
        if (tag instanceof LongTag longTag) {
            data.setLastBellDay(longTag.getAsLong());
        }
    }
}
