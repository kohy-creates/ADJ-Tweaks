package xyz.kohara.adjcore.combat;

import dev.shadowsoffire.attributeslib.api.ALObjects;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xyz.kohara.adjcore.ADJCore;

import java.lang.Math;
import java.util.UUID;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = ADJCore.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CritChance {

    private static final UUID CRIT_CHANCE_MODIFIER = UUID.fromString("290504cc-7633-4667-a091-75a50f3efd81");
    public static final Supplier<Attribute> CRITICAL_CHANCE = ALObjects.Attributes.CRIT_CHANCE;

//    private static final UUID CRIT_DAMAGE_MODIFIER = UUID.fromString("b70dc46e-6652-4a37-8886-21c3e3c85bdf");
//    public static final Supplier<Attribute> CRITICAL_DAMAGE = ALObjects.Attributes.CRIT_DAMAGE;

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {

        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide()) return;

        Player player = event.player;

        AttributeInstance crChance = player.getAttribute(CRITICAL_CHANCE.get());
        if (crChance != null) {
            crChance.removeModifier(CRIT_CHANCE_MODIFIER);
            AttributeModifier modifier = new AttributeModifier(
                    CRIT_CHANCE_MODIFIER,
                    "Level based crit chance",
                    scaleWithExperience(player, 3),
                    AttributeModifier.Operation.ADDITION
            );
            crChance.addTransientModifier(modifier);
        }
//        AttributeInstance crDamage = player.getAttribute(CRITICAL_DAMAGE.get());
//        if (crDamage != null) {
//            crDamage.removeModifier(CRIT_DAMAGE_MODIFIER);
//            AttributeModifier modifier = new AttributeModifier(
//                    CRIT_DAMAGE_MODIFIER,
//                    "Level based crit damage",
//                    scaleWithExperience(player, 10),
//                    AttributeModifier.Operation.MULTIPLY_TOTAL
//            );
//            crDamage.addTransientModifier(modifier);
//        }
    }

    private static double scaleWithExperience(Player player, double step) {
        return floored2DecimalPlacesOnGodWhyIsntItInBaseJava(player.experienceLevel / step / 100d);
    }

    private static double floored2DecimalPlacesOnGodWhyIsntItInBaseJava(double d) {
        return Math.floor(d * 100) / 100.0;
    }
}
