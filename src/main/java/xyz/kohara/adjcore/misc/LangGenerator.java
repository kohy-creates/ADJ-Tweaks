package xyz.kohara.adjcore.misc;

import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.data.event.GatherDataEvent;
import xyz.kohara.adjcore.ADJCore;

import java.util.HashMap;
import java.util.Map;

public class LangGenerator {

    private static final Map<String, String> LOCALS = new HashMap<>();

    private static String constructId(String category, String name) {
        return category + "." + ADJCore.MOD_ID + "." + name;
    }

    public static void gatherData(GatherDataEvent event) {
        event.getGenerator().addProvider(
                event.includeClient(),
                (DataProvider.Factory<ADJLanguageProvider>) output -> new ADJLanguageProvider(output, ADJCore.MOD_ID, "en_us")
        );
    }

    public static void addAttributeTranslation(String descriptionID, String name, String description) {
        LOCALS.put(descriptionID, name);
        LOCALS.put(descriptionID + ".desc", description);
    }

    public static void addEffectTranslation(String id, String name, String description) {
        LOCALS.put(constructId("effect", id), name);
        LOCALS.put(constructId("effect", id) + ".description", description);
        LOCALS.put(constructId("effect", id) + ".desc", description);
    }

    public static void addFluidTypeTranslation(String id, String name) {
        LOCALS.put(constructId("fluid_type", id), name);
    }

    public static void addItemTranslation(String id, String name) {
        LOCALS.put(constructId("item", id), name);
    }

    public static void addBlockTranslation(String id, String name) {
        LOCALS.put(constructId("block", id), name);
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
