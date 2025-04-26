package xyz.kohara.adjtweaks;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.kohara.adjtweaks.combat.IFramesHandler;
import xyz.kohara.adjtweaks.effects.ModEffects;
import xyz.kohara.adjtweaks.potions.PotionsEditor;
import xyz.kohara.adjtweaks.sounds.ModSoundEvents;

@Mod("adjtweaks")
public class ADJTweaks {

    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "adjtweaks";

    public ADJTweaks() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC, "adjtweaks.toml");
        ModSoundEvents.registerSounds();

        IEventBus MOD_BUS = FMLJavaModLoadingContext.get().getModEventBus();
        MOD_BUS.addListener(this::commonSetup);
        MOD_BUS.addListener(this::clientSetup);

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new IFramesHandler());

        ModEffects.register(MOD_BUS);
        ModSoundEvents.SOUND_EVENTS.register(MOD_BUS);

    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        PotionsEditor.edit();
    }

    private void clientSetup(final FMLClientSetupEvent event) {}
}
