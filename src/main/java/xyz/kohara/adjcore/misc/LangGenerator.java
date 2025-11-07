package xyz.kohara.adjcore.misc;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import xyz.kohara.adjcore.ADJCore;

import java.util.HashMap;
import java.util.Map;

public class LangGenerator {

    private static final Map<String, String> LOCALS = new HashMap<>();

    public static void gatherData(GatherDataEvent event) {
        event.getGenerator().addProvider(
                event.includeClient(),
                (DataProvider.Factory<ADJLanguageProvider>) output -> new ADJLanguageProvider(output, ADJCore.MOD_ID, "en_us")
        );
    }

    public static void addAttributeTranslation(String id, String name, String description) {
        LOCALS.put(id, name);
        LOCALS.put(id + ".desc", description);
    }

    public static void addEffectTranslation(String id, String name, String description) {
        LOCALS.put(id, name);
        LOCALS.put(id + ".description", description);
        LOCALS.put(id + ".desc", description);
    }

    private static class ADJLanguageProvider extends LanguageProvider {

        public ADJLanguageProvider(PackOutput output, String modid, String locale) {
            super(output, modid, locale);
        }

        @Override
        protected void addTranslations() {
            LOCALS.forEach(this::add);
        }
    }
}
