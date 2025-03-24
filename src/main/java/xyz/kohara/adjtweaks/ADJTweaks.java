package xyz.kohara.adjtweaks;

import com.mojang.logging.LogUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.slf4j.Logger;
import xyz.kohara.adjtweaks.effect.ModEffects;
import xyz.kohara.adjtweaks.potion.PotionsEditor;

import java.io.ObjectInputFilter;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ADJTweaks.MOD_ID)
public class ADJTweaks {
    public static final String MOD_ID = "adjtweaks";
    public static final Logger LOGGER = LogUtils.getLogger();

    public ADJTweaks() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);

        PotionsEditor.edit();
        ModEffects.register(modEventBus);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        ConfigHandler.register();
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {

        }
    }

    public static void log(Level logLevel, String string) {
        LogManager.getLogger(ADJTweaks.MOD_ID).log(logLevel,string);
    }
}