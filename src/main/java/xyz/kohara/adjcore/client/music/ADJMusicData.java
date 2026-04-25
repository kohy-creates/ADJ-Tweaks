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
import java.util.function.Consumer;

public class ADJMusicData {

    private static final Config INSTANCE;

    static {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Config.MusicEntry.class, new Config.MusicEntryDeserializer())
                .registerTypeAdapter(Config.BossMusic.class, new Config.BossMusicDeserializer())
                .create();
        try (Reader reader = Files.newBufferedReader(Paths.get("config/adjcore/music.json"))) {
            INSTANCE = gson.fromJson(reader, ADJMusicData.Config.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Config get() {
        return INSTANCE;
    }

    private static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(
            ForgeRegistries.SOUND_EVENTS,
            ADJCore.MOD_ID.replace("core", "")
    );

    public static void load(IEventBus bus) {

        // Collect all adj:music ResourceLocations from the config
        Set<ResourceLocation> toRegister = new HashSet<>();

        // Helper to check and add sound string
        Consumer<String> addIfAdjMusic = str -> {
            if (str != null && str.startsWith("adj:music.")) {
                toRegister.add(ResourceLocation.parse(str));
            }
        };

        // Check 'menu' string
        addIfAdjMusic.accept(get().menu);

        // Check defaults map (values can be MusicEntry or String depending on deserialization)
        if (get().defaults != null) {
            for (Config.MusicEntry entry : get().defaults.values()) {
                if (entry != null) {
                    if (entry.track != null) addIfAdjMusic.accept(entry.track);
                    if (entry.conditions != null) entry.conditions.values().forEach(addIfAdjMusic);
                }
            }
        }

        // Check biome map
        if (get().biome != null) {
            for (Config.MusicEntry entry : get().biome.values()) {
                if (entry != null) {
                    if (entry.track != null) addIfAdjMusic.accept(entry.track);
                    if (entry.conditions != null) entry.conditions.values().forEach(addIfAdjMusic);
                }
            }
        }

        // Check boss map
        if (get().boss != null) {
            for (Config.BossMusic bossMusic : get().boss.values()) {
                if (bossMusic != null) {
                    for (Config.BossMusic.BossMusicConfig cfg : bossMusic.phases.values()) {
                        if (cfg.track != null) addIfAdjMusic.accept(cfg.track);
                    }
                }
            }
        }

        for (ResourceLocation soundId : toRegister) {
            if (!ForgeRegistries.SOUND_EVENTS.containsKey(soundId)) {
                SOUNDS.register(soundId.getPath(), () -> SoundEvent.createVariableRangeEvent(soundId));
            }
        }

        SOUNDS.register(bus);

    }

    public static class Config {

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
            public Map<String, BossMusicConfig> phases = new HashMap<>();

            public static class BossMusicConfig {
                public String track;
                public String title;
                public String author;
                public Integer distance;
            }
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

        public static class BossMusicDeserializer implements JsonDeserializer<BossMusic> {
            @Override
            public BossMusic deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                BossMusic boss = new BossMusic();
                JsonObject obj = json.getAsJsonObject();

                for (Map.Entry<String, JsonElement> e : obj.entrySet()) {
                    BossMusic.BossMusicConfig cfg = context.deserialize(e.getValue(), BossMusic.BossMusicConfig.class);
                    boss.phases.put(e.getKey(), cfg);
                }

                return boss;
            }
        }
    }
}
