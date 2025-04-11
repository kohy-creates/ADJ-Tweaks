package xyz.kohara.adjtweaks.mixins.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Equipment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.kohara.adjtweaks.sounds.ModSoundEvents;

@Mixin(ShieldItem.class)
public abstract class ShieldSoundsMixin extends Item implements Equipment {
    public ShieldSoundsMixin(Settings settings) {
        super(settings);
    }

    // Plays a sound when the user raises their shield

    @Inject(at = @At("HEAD"), method = "use")
    private void auditory_blockSound(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
            world.playSound(null, user.getBlockPos(), ModSoundEvents.ITEM_SHIELD_RAISE.get(), SoundCategory.PLAYERS, 0.3F, 0.8f + world.random.nextFloat() * 0.4F);
    }

    // Shields now have a unique equipping sound

    @Override
    public SoundEvent getEquipSound() {
        return ModSoundEvents.ITEM_SHIELD_EQUIP.get();
    }
}
