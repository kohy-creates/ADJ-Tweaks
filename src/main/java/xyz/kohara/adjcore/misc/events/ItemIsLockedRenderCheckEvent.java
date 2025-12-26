package xyz.kohara.adjcore.misc.events;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import xyz.kohara.adjcore.compat.kubejs.ClientEvents;
import xyz.kohara.adjcore.compat.kubejs.clientevents.ItemIsLockedRenderCheckEventJS;

@Cancelable
public class ItemIsLockedRenderCheckEvent extends Event {

    private final ItemStack stack;
    private final LocalPlayer player;

    public ItemIsLockedRenderCheckEvent(ItemStack stack, LocalPlayer player) {
        this.stack = stack;
        this.player = player;

        if (ClientEvents.IS_LOCKED_RENDER_CHECK.hasListeners()) {
            var result = ClientEvents.IS_LOCKED_RENDER_CHECK.post(new ItemIsLockedRenderCheckEventJS(this));

            if (result.interruptFalse()) {
                this.setCanceled(true);
            }
        }
    }

    public ItemStack getItemStack() {
        return this.stack;
    }

    public LocalPlayer getPlayer() {
        return this.player;
    }
}
