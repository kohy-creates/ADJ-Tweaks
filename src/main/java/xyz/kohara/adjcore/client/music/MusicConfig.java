package xyz.kohara.adjcore.client.music;

import com.google.gson.*;
import com.google.gson.annotations.SerializedName;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import xyz.kohara.adjcore.ADJCore;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MusicConfig {

    public static MusicConfig CONFIG;

    public String menu;

    @SerializedName("default")
    public Map<String, MusicEntry> defaults;

    public Map<String, MusicEntry> biome;
    public Map<String, BossMusic> boss;

    public static class MusicEntry {
        public String track;
        public Map<String, String> conditions;
    }

    public static class BossMusic {
        public String start;
        public String loop;
        public String stop;
    }

    public static class MusicEntryDeserializer implements JsonDeserializer<MusicEntry> {
        @Override
        public MusicEntry deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            MusicEntry entry = new MusicEntry();
            if (json.isJsonPrimitive()) {
                entry.track = json.getAsString();
            } else if (json.isJsonObject()) {
                entry.conditions = new HashMap<>();
                JsonObject obj = json.getAsJsonObject();
                for (Map.Entry<String, JsonElement> e : obj.entrySet()) {
                    entry.conditions.put(e.getKey(), e.getValue().getAsString());
                }
            }
            return entry;
        }
    }

    public static void load(IEventBus bus) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(MusicEntry.class, new MusicEntryDeserializer())
                .create();

        try (Reader reader = Files.newBufferedReader(Paths.get("config/adjcore/music.json"))) {
            CONFIG = gson.fromJson(reader, MusicConfig.class);

            // Collect all adj:music ResourceLocations from the config
            Set<ResourceLocation> toRegister = new HashSet<>();

            // Helper to check and add sound string
            java.util.function.Consumer<String> addIfAdjMusic = str -> {
                if (str != null && str.startsWith("adj:music.")) {
                    toRegister.add(new ResourceLocation(str));
                }
            };

            // Check 'menu' string
            addIfAdjMusic.accept(CONFIG.menu);

            // Check defaults map (values can be MusicEntry or String depending on deserialization)
            if (CONFIG.defaults != null) {
                for (MusicEntry entry : CONFIG.defaults.values()) {
                    if (entry != null) {
                        if (entry.track != null) addIfAdjMusic.accept(entry.track);
                        if (entry.conditions != null) entry.conditions.values().forEach(addIfAdjMusic);
                    }
                }
            }

            // Check biome map
            if (CONFIG.biome != null) {
                for (MusicEntry entry : CONFIG.biome.values()) {
                    if (entry != null) {
                        if (entry.track != null) addIfAdjMusic.accept(entry.track);
                        if (entry.conditions != null) entry.conditions.values().forEach(addIfAdjMusic);
                    }
                }
            }

            // Check boss map
            if (CONFIG.boss != null) {
                for (BossMusic bossMusic : CONFIG.boss.values()) {
                    if (bossMusic != null) {
                        addIfAdjMusic.accept(bossMusic.start);
                        addIfAdjMusic.accept(bossMusic.loop);
                        addIfAdjMusic.accept(bossMusic.stop);
                    }
                }
            }

            for (ResourceLocation soundId : toRegister) {
                if (!ForgeRegistries.SOUND_EVENTS.containsKey(soundId)) {
                    SOUNDS.register(soundId.getPath(), () -> SoundEvent.createVariableRangeEvent(soundId));
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            CONFIG = new MusicConfig();
        }
        SOUNDS.register(bus);
    }

    private static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, ADJCore.MOD_ID.replace("core", ""));
}
