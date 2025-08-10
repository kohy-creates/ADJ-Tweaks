package xyz.kohara.adjcore;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.kohara.adjcore.attributes.ModAttributes;
import xyz.kohara.adjcore.combat.CombatRules;
import xyz.kohara.adjcore.combat.DamageHandler;
import xyz.kohara.adjcore.combat.VariatedDamage;
import xyz.kohara.adjcore.combat.critevent.ApothCritStrikeEvent;
import xyz.kohara.adjcore.curio.CurioControl;
import xyz.kohara.adjcore.effects.ModEffects;
import xyz.kohara.adjcore.effects.editor.EffectsEditor;
import xyz.kohara.adjcore.entity.WanderingTraderEdits;
import xyz.kohara.adjcore.misc.DelayedTaskScheduler;
import xyz.kohara.adjcore.networking.ModMessages;
import xyz.kohara.adjcore.potions.PotionsEditor;
import xyz.kohara.adjcore.sounds.ModSoundEvents;

@Mod(ADJCore.MOD_ID)
public class ADJCore {

    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "adjcore";

    public ADJCore() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC, MOD_ID + ".toml");
        ModSoundEvents.registerSounds();

        IEventBus MOD_BUS = FMLJavaModLoadingContext.get().getModEventBus();
        MOD_BUS.addListener(this::commonSetup);
        MOD_BUS.addListener(this::clientSetup);

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new DamageHandler());
        MinecraftForge.EVENT_BUS.register(new VariatedDamage());
        MinecraftForge.EVENT_BUS.register(DelayedTaskScheduler.class);
        MinecraftForge.EVENT_BUS.register(WanderingTraderEdits.class);
        MinecraftForge.EVENT_BUS.register(CurioControl.class);
        MinecraftForge.EVENT_BUS.register(CombatRules.class);

        ModEffects.register(MOD_BUS);
        ModSoundEvents.SOUND_EVENTS.register(MOD_BUS);
        ModAttributes.register(MOD_BUS);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(ModMessages::register);
        PotionsEditor.edit();
        EffectsEditor.edit();
    }

    private void clientSetup(final FMLClientSetupEvent event) {
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onApothicCrit(ApothCritStrikeEvent event) {
        if (event.attacker instanceof Player player) {
            CriticalHitEvent e = new CriticalHitEvent(player, event.victim, event.multiplier, false);
            MinecraftForge.EVENT_BUS.post(e);
        }
    }
}
