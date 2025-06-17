package xyz.kohara.adjcore.curio;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.event.CurioEquipEvent;
import top.theillusivec4.curios.api.type.ISlotType;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;
import xyz.kohara.adjcore.ADJCore;
import xyz.kohara.adjcore.Config;

import java.util.*;
import java.util.stream.Collectors;

public class CurioControl {

    private static final String ACCESSORY_SLOT = Config.CURIO_TYPE_TO_KEEP.get();
    private static List<TagKey<Item>> EXCLUSION_LIST;

    @SubscribeEvent
    public static void onCurioEquipEvent(CurioEquipEvent event) {

        LivingEntity entity = event.getEntity();
        Optional<ICuriosItemHandler> curios = CuriosApi.getCuriosInventory(entity).resolve();

        if (curios.isEmpty()) return;

        // Can't equip same Curio twice
        if (curios.get().isEquipped(event.getStack().getItem())) {
            event.setResult(Event.Result.DENY);
            return;
        }

        // Exclusions
        for (ICurioStacksHandler curioStacksHandler : curios.get().getCurios().values()) {
            IDynamicStackHandler stackHandler = curioStacksHandler.getStacks();
            for (int i = 0; i < stackHandler.getSlots(); i++) {
                ItemStack stack = stackHandler.getStackInSlot(i);
                for (TagKey<Item> tag : EXCLUSION_LIST) {
                    if (event.getStack().is(tag) && stack.is(tag)) {
                        event.setResult(Event.Result.DENY);
                        return;
                    }
                }
            }
        }
    }


    // Only keep one type of curio slots
    @SubscribeEvent
    public static void onPlayerLoggedInEvent(PlayerEvent.PlayerLoggedInEvent event) {
        Map<String, ISlotType> slots = CuriosApi.getPlayerSlots(event.getEntity());
        for (ISlotType slot : slots.values()) {
            String id = slot.getIdentifier();
            if (!Objects.equals(id, ACCESSORY_SLOT) && !Objects.equals(id, "back")) { //why the fuck does that crash Backpacked
                CuriosApi.getSlotHelper().setSlotsForType(id, event.getEntity(), 0);
            }
        }
    }

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        Registry<Item> itemRegistry = BuiltInRegistries.ITEM;
        List<TagKey<Item>> exclusionList = new ArrayList<>();
        // Loop through all tags and create an exclusion list
        for (TagKey<Item> tagKey : itemRegistry.getTags().map(Pair::getFirst).collect(Collectors.toSet())) {
            if (tagKey.location().toString().indexOf(ADJCore.MOD_ID + ":curio_exclusions/") == 0) {
                exclusionList.add(tagKey);
            }
        }
        setExclusionList(exclusionList);
    }

    public static void setExclusionList(List<TagKey<Item>> exclusionList) {
        EXCLUSION_LIST = exclusionList;
    }
}
