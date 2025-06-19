package xyz.kohara.adjcore.mixins.items;

import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(CrossbowItem.class)
public interface CrossbowItemAccessor {

    @Invoker("addChargedProjectile")
    static void addChargedProjectile(ItemStack crossbow, ItemStack projectile) {
        throw new AssertionError();
    }
}