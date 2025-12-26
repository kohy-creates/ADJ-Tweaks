package xyz.kohara.adjcore.compat.curios;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import org.confluence.mod.item.curio.ILavaImmune;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import java.util.Optional;

public class FireImmunityCurios {

    private static final int BASE_DURATION = 140;

    public static void applyEffect(Player player) {
        if (player.isInLava() || player.isOnFire()) return;

        Optional<ICuriosItemHandler> curios = CuriosApi.getCuriosInventory(player).resolve();
        if (curios.isEmpty()) return;
        int duration = 3;
        for (int i = 0; i < curios.get().getSlots(); i++) {
            Item item = curios.get().getEquippedCurios().getStackInSlot(i).getItem();
            if (item instanceof ILavaImmune) {
                duration += BASE_DURATION;
            }
        }
        if (duration == 3) return;
        player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, duration, 0, false, false, true));
    }

}
