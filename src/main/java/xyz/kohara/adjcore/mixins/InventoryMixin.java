package xyz.kohara.adjcore.mixins;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.kohara.adjcore.Config;

@Mixin(Inventory.class)
public class InventoryMixin {

    @Unique
    private final float DAMAGE_PER_POINT = Config.ARMOR_DURABILITY_DAMAGE_FACTOR.get().floatValue();

    @Inject(method = "hurtArmor", at = @At("HEAD"), cancellable = true)
    private void hurtArmor(DamageSource source, float damage, int[] armorPieces, CallbackInfo ci) {
        ci.cancel();
        Inventory inventory = (Inventory) (Object) this;
        if (!(damage <= 0.0F)) {
            damage /= DAMAGE_PER_POINT;
            if (damage < 1.0F) {
                damage = 1.0F;
            }

            for (int i : armorPieces) {
                ItemStack itemstack = inventory.armor.get(i);
                if ((!source.is(DamageTypeTags.IS_FIRE) || !itemstack.getItem().isFireResistant()) && itemstack.getItem() instanceof ArmorItem) {
                    itemstack.hurtAndBreak((int) damage, inventory.player, (arg) -> arg.broadcastBreakEvent(EquipmentSlot.byTypeAndIndex(EquipmentSlot.Type.ARMOR, i)));
                }
            }
        }

    }
}
