package xyz.kohara.adjcore.mixins.compat.ars;

import com.hollingsworth.arsnouveau.api.mana.IManaCap;
import com.hollingsworth.arsnouveau.api.util.ManaUtil;
import com.hollingsworth.arsnouveau.common.event.ManaCapEvents;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketUpdateMana;
import com.hollingsworth.arsnouveau.setup.registry.CapabilityRegistry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ManaCapEvents.class, remap = false)
public class ManaCapEventsMixin {

    @Inject(method = "playerOnTick", at = @At("HEAD"), cancellable = true)
    private static void makeThisThingMakeMoreSense(TickEvent.PlayerTickEvent e, CallbackInfo ci) {
        ci.cancel();

        if (e.player == null
                || e.player.getCommandSenderWorld().isClientSide()
                || e.phase != TickEvent.Phase.END
                || !e.player.isAlive()
        ) return;

        var manaLazyOptional = CapabilityRegistry.getMana(e.player);
        var manaOptional = manaLazyOptional.resolve();
        IManaCap mana = null;
        if (manaOptional.isPresent()) {
            mana = manaOptional.get();
        }
        if (mana == null) return;

        boolean shouldIgnoreMax = e.player.level().getGameTime() % 60 == 0;

        if (mana.getCurrentMana() != mana.getMaxMana() || shouldIgnoreMax) {
            if (e.player.adjcore$getManaRegenDelay() <= 0) {
                int increaseCounterBy = (int) ManaUtil.getManaRegen(e.player);
                double regenAmount = (increaseCounterBy / 40d) + 1d;

                e.player.adjcore$increaseManaRegenCounter(increaseCounterBy);
                if (e.player.adjcore$getManaRegenCounter() >= 40) {
                    mana.addMana(regenAmount);
                    e.player.adjcore$increaseManaRegenCounter(-40);
                }
            }

            Networking.INSTANCE.send(
                    PacketDistributor.PLAYER.with(() -> (ServerPlayer) e.player),
                    new PacketUpdateMana(
                            mana.getCurrentMana(),
                            mana.getMaxMana(),
                            mana.getGlyphBonus(),
                            mana.getBookTier()
                    )
            );
        }

        ManaUtil.Mana maxmana = ManaUtil.calcMaxMana(e.player);
        int max = maxmana.getRealMax();

        if (mana.getMaxMana() != max || shouldIgnoreMax) {
            mana.setMaxMana(max);
            Networking.INSTANCE.send(
                    PacketDistributor.PLAYER.with(() -> (ServerPlayer) e.player),
                    new PacketUpdateMana(
                            mana.getCurrentMana(),
                            mana.getMaxMana(),
                            mana.getGlyphBonus(),
                            mana.getBookTier(),
                            maxmana.Reserve()
                    )
            );
        }
    }

}
