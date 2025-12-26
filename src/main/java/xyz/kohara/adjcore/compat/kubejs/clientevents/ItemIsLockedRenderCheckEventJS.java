package xyz.kohara.adjcore.compat.kubejs.clientevents;

import dev.latvian.mods.kubejs.event.EventJS;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import xyz.kohara.adjcore.misc.events.ItemIsLockedRenderCheckEvent;

public class ItemIsLockedRenderCheckEventJS extends EventJS {

    private final ItemIsLockedRenderCheckEvent event;

    public ItemIsLockedRenderCheckEventJS(ItemIsLockedRenderCheckEvent event) {
        this.event = event;
    }

    public ItemStack getItemStack() {
        return this.event.getItemStack();
    }

    public LocalPlayer getPlayer() {
        return this.event.getPlayer();
    }
}
