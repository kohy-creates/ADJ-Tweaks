package xyz.kohara.adjcore.registry.effects.editor;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import net.minecraft.resources.ResourceLocation;
import xyz.kohara.adjcore.ADJCore;

import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EffectEditorConfig {

    private static final String CONFIG_PATH = "config/" + ADJCore.MOD_ID + "/effect_attributes.json";

    public static class ConfigData {
        public boolean replace;
        public List<String> attributes;

        // Optional: Debug-friendly constructor
        public ConfigData() {
            // Initialized via Gson, no need to do anything here
        }

        @Override
        public String toString() {
            return "ConfigData{" +
                    "replace=" + replace +
                    ", attributes=" + attributes +
                    '}';
        }
    }

    public static class AttributeModifierEntry {
        public final ResourceLocation attribute;
        public final double value;

        public AttributeModifierEntry(String input) {
            String[] parts = input.split(";");
            if (parts.length != 2) {
                throw new IllegalArgumentException("Invalid attribute entry: " + input);
            }

            this.attribute = ResourceLocation.parse(parts[0]);
            this.value = Double.parseDouble(parts[1]);
        }

        @Override
        public String toString() {
            return attribute + " -> " + value;
        }
    }

    public static Map<ResourceLocation, ConfigData> parseConfig() {
        Gson gson = new Gson();
        Map<ResourceLocation, ConfigData> result = new HashMap<>();

        try (FileReader reader = new FileReader(CONFIG_PATH)) {
            Type type = new TypeToken<Map<String, ConfigData>>() {}.getType();
            Map<String, ConfigData> rawMap = gson.fromJson(reader, type);

            System.out.println("[EffectEditorConfig] Loaded raw config map with " + rawMap.size() + " entries");

            for (Map.Entry<String, ConfigData> entry : rawMap.entrySet()) {
                ResourceLocation effectId = ResourceLocation.parse(entry.getKey());
                ConfigData config = entry.getValue();

                System.out.println("[EffectEditorConfig] Parsed: " + effectId + " => " + config);

                result.put(effectId, config);
            }

        } catch (Exception e) {
            System.err.println("[EffectEditorConfig] Failed to load config: " + e.getMessage());
            e.printStackTrace();
        }

        return result;
    }
}
