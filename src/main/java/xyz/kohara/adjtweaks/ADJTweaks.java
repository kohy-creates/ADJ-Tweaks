package xyz.kohara.adjtweaks;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.kohara.adjtweaks.effects.ModEffects;
import xyz.kohara.adjtweaks.potions.PotionsEditor;

@Mod("adjtweaks")
public class ADJTweaks {

    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "adjtweaks";

    public ADJTweaks() {

        IEventBus MOD_BUS = FMLJavaModLoadingContext.get().getModEventBus();
        MOD_BUS.addListener(this::commonSetup);
        MOD_BUS.addListener(this::clientSetup);

        MinecraftForge.EVENT_BUS.register(this);

        Config.register();
        ModEffects.register(MOD_BUS);

    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // fires after registries are done
        PotionsEditor.edit();
    }

    private void clientSetup(final FMLClientSetupEvent event) {}
}
