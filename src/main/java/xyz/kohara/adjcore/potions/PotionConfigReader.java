package xyz.kohara.adjcore.potions;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.NotNull;
import xyz.kohara.adjcore.ADJCore;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class PotionConfigReader {
    private static final Gson gson = new Gson();

    public static class PotionConfig {
        public String effectID;
        public int minutes, seconds, level;
        public PotionConfig(String effectID, int minutes, int seconds, int level) {
            this.effectID = effectID;
            this.minutes = minutes;
            this.seconds = seconds;
            this.level = level;
        }
    }

    private static final Path POTION_CONFIG = FMLPaths.CONFIGDIR.get().resolve("adjtweaks/potions_config.json");

    public static Map<String, PotionConfig> loadPotionConfig() {

        File configFile = POTION_CONFIG.toFile();
        if (!configFile.exists()) {
            ADJCore.LOGGER.log(Level.INFO,"No potion configuration found, skipping");
            return new HashMap<>();
        }

        Map<String, PotionConfig> potionConfigs = new HashMap<>();

        try (BufferedReader  reader = Files.newBufferedReader(POTION_CONFIG, StandardCharsets.UTF_8)) {
            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
            for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                String potionId = entry.getKey();
                JsonArray effectsArray = entry.getValue().getAsJsonArray();
                for (JsonElement effectElement : effectsArray) {
                    PotionConfig config = getPotionConfig(effectElement);
                    potionConfigs.put(potionId, config);
                    ADJCore.LOGGER.log(Level.INFO,
                            "Found changes for potion '" + potionId +
                                    "': effect = " + config.effectID +
                                    "duration = " + config.minutes + ":" + config.seconds +
                                    " level = " + config.level
                    );
                }
            }

        }
        catch (IOException e) {
            ADJCore.LOGGER.log(Level.ERROR, "Unhandled exception!");
            e.printStackTrace();
        }
        return potionConfigs;
    }

    private static @NotNull PotionConfig getPotionConfig(JsonElement effectElement) {
        JsonObject effectConfig = effectElement.getAsJsonObject();
        String effectId = effectConfig.get("effect").getAsString();
        String[] durationParts = effectConfig.get("duration").getAsString().split(":");
        int minutes = Integer.parseInt(durationParts[0]);
        int seconds = Integer.parseInt(durationParts[1]);
        int level = effectConfig.get("level").getAsInt();

        return new PotionConfig(effectId, minutes, seconds, level);
    }
}
