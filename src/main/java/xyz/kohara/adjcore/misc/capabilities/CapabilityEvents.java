package xyz.kohara.adjcore.misc.capabilities;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.jetbrains.annotations.NotNull;
import xyz.kohara.adjcore.ADJCore;
import xyz.kohara.adjcore.misc.ModCapabilities;

public class CapabilityEvents {

    @SubscribeEvent
    public static void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            event.addCapability(ResourceLocation.fromNamespaceAndPath(ADJCore.MOD_ID, "loadouts"),
                    new ICapabilitySerializable<CompoundTag>() {
                        final IPlayerLoadouts inst = new PlayerLoadoutsProvider();

                        @Override
                        public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, Direction side) {
                            return cap == ModCapabilities.PLAYER_LOADOUTS ? LazyOptional.of(() -> inst).cast() : LazyOptional.empty();
                        }

                        @Override
                        public CompoundTag serializeNBT() {
                            return inst.serializeNBT();
                        }

                        @Override
                        public void deserializeNBT(CompoundTag nbt) {
                            inst.deserializeNBT(nbt);
                        }
                    });

            event.addCapability(ADJCore.of("last_trader_summon"), new BellTraderDataProvider());
        }
    }

    @SubscribeEvent
    public static void clonePlayer(PlayerEvent.Clone event) {
        event.getOriginal().reviveCaps(); // in case
        event.getOriginal().getCapability(ModCapabilities.PLAYER_LOADOUTS).ifPresent(oldCap -> {
            event.getEntity().getCapability(ModCapabilities.PLAYER_LOADOUTS).ifPresent(newCap -> {
                newCap.deserializeNBT(oldCap.serializeNBT());
            });
        });
    }
}
