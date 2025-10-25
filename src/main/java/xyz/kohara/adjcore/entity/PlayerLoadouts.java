package xyz.kohara.adjcore.entity;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;
import xyz.kohara.adjcore.misc.ModCapabilities;
import xyz.kohara.adjcore.misc.capabilities.IPlayerLoadouts;
import xyz.kohara.adjcore.client.networking.ModMessages;
import xyz.kohara.adjcore.client.networking.packet.ShowRainbowMessageS2CPacket;

import java.util.Map;
import java.util.Optional;

public class PlayerLoadouts {

    private static final Map<Integer, String> ARMOR_SLOTS = Map.of(
            0, "head",
            1, "chest",
            2, "legs",
            3, "feet"
    );

    public static void changeLoadout(ServerPlayer player, int targetLoadout) {
        player.getCapability(ModCapabilities.PLAYER_LOADOUTS).ifPresent(loadouts -> {
            int currentLoadout = loadouts.getCurrentLoadout();

            if (currentLoadout == 0) {
                loadouts.setCurrentLoadout(1);
                currentLoadout = 1;
            }
            if (currentLoadout == targetLoadout) {
                return;
            }

            saveCurrentLoadout(loadouts, loadoutID(currentLoadout), player);
            setPlayerEquipment(getOrCreateLoadout(loadouts, loadoutID(targetLoadout)), player);
            loadouts.setCurrentLoadout(targetLoadout);
            playSFX(player, targetLoadout);
        });
    }

    private static String loadoutID(int i) {
        return "adj.loadout_" + i;
    }

    private static CompoundTag getOrCreateLoadout(IPlayerLoadouts loadouts, String key) {
        CompoundTag existing = loadouts.getLoadout(key);
        if (existing.isEmpty()) {
            CompoundTag loadout = new CompoundTag();

            CompoundTag emptyItem = ItemStack.EMPTY.save(new CompoundTag());

            loadout.put("head", emptyItem.copy());
            loadout.put("chest", emptyItem.copy());
            loadout.put("legs", emptyItem.copy());
            loadout.put("feet", emptyItem.copy());

            loadout.put("curios", new ListTag());

            loadouts.setLoadout(key, loadout);
            return loadout;
        }
        return existing;
    }

    private static void saveCurrentLoadout(IPlayerLoadouts parent, String key, ServerPlayer player) {
        CompoundTag loadout = new CompoundTag();


        for (int i = 0; i < player.getInventory().armor.size(); i++) {
            ItemStack stack = player.getInventory().getArmor(i);
            CompoundTag stackTag = new CompoundTag();
            stack.save(stackTag);
            loadout.put(ARMOR_SLOTS.get(i), stackTag);
        }

        ListTag curiosList = new ListTag();
        Optional<ICuriosItemHandler> curiosInv = CuriosApi.getCuriosInventory(player).resolve();

        curiosInv.ifPresent(iCuriosItemHandler -> iCuriosItemHandler.getCurios().forEach((curioSlot, handler) -> {
            if (curioSlot.equals("accessory")) {
                IDynamicStackHandler stackHandler = handler.getStacks();
                for (int i = 0; i < stackHandler.getSlots(); i++) {
                    ItemStack stack = stackHandler.getStackInSlot(i);
                    if (!stack.isEmpty()) {
                        CompoundTag stackTag = new CompoundTag();
                        stack.save(stackTag);
                        stackTag.putInt("slot", i);
                        curiosList.add(stackTag);
                    }
                }
            }
        }));
        loadout.put("curios", curiosList);

        parent.setLoadout(key, loadout);
    }

    private static void setPlayerEquipment(CompoundTag loadoutData, ServerPlayer player) {

        for (int i = 0; i < player.getInventory().armor.size(); i++) {
            player.getInventory().armor.set(i, ItemStack.of(loadoutData.getCompound(ARMOR_SLOTS.get(i))));
        }

        Optional<ICuriosItemHandler> curiosInv = CuriosApi.getCuriosInventory(player).resolve();
        if (curiosInv.isPresent()) {
            curiosInv.get().getCurios().forEach((slotId, handler) -> {
                if (slotId.equals("accessory")) {
                    IDynamicStackHandler stacks = handler.getStacks();
                    for (int i = 0; i < stacks.getSlots(); i++) {
                        stacks.setStackInSlot(i, ItemStack.EMPTY);
                    }
                }
            });

            ListTag curiosList = loadoutData.getList("curios", Tag.TAG_COMPOUND);
            for (int i = 0; i < curiosList.size(); i++) {
                CompoundTag stackTag = curiosList.getCompound(i);
                int slot = stackTag.getInt("slot");

                ItemStack stack = ItemStack.of(stackTag);

                curiosInv.ifPresent(iCuriosItemHandler -> iCuriosItemHandler.getCurios().forEach((curioSlot, handler) -> {
                    if (curioSlot.equals("accessory")) {
                        IDynamicStackHandler stackHandler = handler.getStacks();
                        stackHandler.setStackInSlot(slot, stack);
                    }
                }));
            }
        }
    }

    private static void playSFX(ServerPlayer player, int loadout) {
        RandomSource random = player.level().random;

        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.HORSE_ARMOR, SoundSource.PLAYERS, 0.8F, 0.8f + random.nextFloat() * 0.4F);
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.LEASH_KNOT_PLACE, SoundSource.PLAYERS, 0.5F, 0.8f + random.nextFloat() * 0.4F);

        ModMessages.sendToPlayer(new ShowRainbowMessageS2CPacket(Component.literal("Switched active loadout to Loadout " + loadout)), player);

        AABB box = player.getBoundingBox();

        for (int i = 0; i < 13; i++) {
            double x = Mth.lerp(random.nextDouble(), box.minX, box.maxX);
            double y = Mth.lerp(random.nextDouble(), box.minY, box.maxY);
            double z = Mth.lerp(random.nextDouble(), box.minZ, box.maxZ);
            ((ServerLevel) player.level()).sendParticles(
                    ParticleTypes.POOF,
                    x, y, z,
                    1, 0, 0, 0, 0.1
            );
        }
    }
}
