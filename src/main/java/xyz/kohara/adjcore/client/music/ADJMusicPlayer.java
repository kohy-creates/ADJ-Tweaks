package xyz.kohara.adjcore.client.music;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.sounds.MusicManager;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.registries.ForgeRegistries;
import xyz.kohara.adjcore.ADJCore;
import xyz.kohara.adjcore.client.misc.events.ADJFindMusicEvent;
import xyz.kohara.adjcore.client.networking.ADJMessages;
import xyz.kohara.adjcore.client.networking.packet.RequestEntityTagsC2SPacket;
import xyz.kohara.adjcore.misc.events.PlayerTitleEvent;
import xyz.kohara.adjcore.mixins.client.music.MusicManagerAccessor;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class ADJMusicPlayer {
    // This will hold pre-built Music objects after load()
    public static final Music resolvedMenu = getTrack(ADJMusicData.get().menu);
    public static final Map<String, ResolvedEntry> resolvedDefaults = new HashMap<>();
    public static final Map<String, ResolvedEntry> resolvedBiomes = new HashMap<>();
    public static final Map<String, ResolvedBossMusic> resolvedBosses = new HashMap<>();

    static {
        buildResolvedMaps();
    }

    public static void buildResolvedMaps() {
        ADJMusicData.Config cfg = ADJMusicData.get();
        if (cfg.defaults != null) {
            for (Map.Entry<String, ADJMusicData.Config.MusicEntry> e : cfg.defaults.entrySet()) {
                resolvedDefaults.put(e.getKey(), resolveEntry(e.getValue()));
            }
        }
        if (cfg.biome != null) {
            for (Map.Entry<String, ADJMusicData.Config.MusicEntry> e : cfg.biome.entrySet()) {
                resolvedBiomes.put(e.getKey(), resolveEntry(e.getValue()));
            }
        }
        if (cfg.boss != null) {
            for (Map.Entry<String, ADJMusicData.Config.BossMusic> e : cfg.boss.entrySet()) {
                Map<String, ResolvedBossMusic.Phase> phases = new HashMap<>();
                for (Map.Entry<String, ADJMusicData.Config.BossMusic.BossMusicConfig> phaseEntry : e.getValue().phases.entrySet()) {
                    ADJMusicData.Config.BossMusic.BossMusicConfig cfgPhase = phaseEntry.getValue();
                    Music music = getTrack(cfgPhase.track);
                    int distance = 60;
                    if (cfgPhase.distance != null) {
                        distance = cfgPhase.distance;
                    }
                    phases.put(
                            phaseEntry.getKey(),
                            new ResolvedBossMusic.Phase(music, cfgPhase.title, cfgPhase.author, distance)
                    );
                }
                resolvedBosses.put(e.getKey(), new ResolvedBossMusic(phases));
            }
        }
    }

    private static ResolvedEntry resolveEntry(ADJMusicData.Config.MusicEntry entry) {
        if (entry == null) return null;
        Music track = getTrack(entry.track);
        Map<String, Music> conds = null;
        if (entry.conditions != null) {
            conds = new HashMap<>();
            for (Map.Entry<String, String> c : entry.conditions.entrySet()) {
                conds.put(c.getKey(), getTrack(c.getValue()));
            }
        }
        return new ResolvedEntry(track, conds);
    }

    public static Music findMusic() {
        Music track = resolvedMenu;
        LocalPlayer player = Minecraft.getInstance().player;
        ADJFindMusicEvent event = new ADJFindMusicEvent(player, Minecraft.getInstance().level);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.getMusic() != null) return event.getMusic();
        if (player != null) {
            ResolvedEntry overworld = resolvedDefaults.get("minecraft:overworld");
            Music bossTrack = getBossMusic(player);
            if (bossTrack != null) {
                track = bossTrack;
            } else {
                if (overworld != null && overworld.conditions != null) {
                    track = overworld.conditions.get(TrackVariant.getVariant(player.level()).getId());
                }
                String dimension = player.level().dimension().location().toString();
                String biomeName = player.level()
                        .getBiome(player.getOnPos())
                        .unwrapKey()
                        .map(ResourceKey::location)
                        .orElse(ResourceLocation.fromNamespaceAndPath("unknown", "unknown"))
                        .toString();
                // dimension-specific
                ResolvedEntry dimEntry = resolvedDefaults.get(dimension);
                if (dimEntry != null) {
                    if (dimEntry.track != null) {
                        track = dimEntry.track;
                    } else if (dimEntry.conditions != null) {
                        Music condTrack = dimEntry.conditions.get(TrackVariant.getVariant(player.level()).getId());
                        if (condTrack != null) track = condTrack;
                    }
                }
                // biome-specific
                for (String key : resolvedBiomes.keySet()) {
                    boolean found = false;
                    if (key.startsWith("#") && player.level().getBiome(player.getOnPos())
                            .is(TagKey.create(Registries.BIOME, ResourceLocation.parse(key.substring(1))))) {
                        found = true;
                    } else if (biomeName.equals(key)) {
                        found = true;
                    }
                    if (found) {
                        ResolvedEntry biomeEntry = resolvedBiomes.get(key);
                        if (biomeEntry.track != null) {
                            track = biomeEntry.track;
                        } else if (biomeEntry.conditions != null) {
                            Music condTrack = biomeEntry.conditions.get(TrackVariant.getVariant(player.level()).getId());
                            if (condTrack != null) track = condTrack;
                        }
                    }
                }
            }
        }
        return track;
    }

    private static Music getBossMusic(LocalPlayer player) {
        List<String> bossIDs = resolvedBosses.keySet().stream().toList();
        AtomicReference<String> id = new AtomicReference<>();
        List<LivingEntity> bosses = player.level().getEntities(
                EntityTypeTest.forClass(LivingEntity.class),
                player.getBoundingBox().inflate(120),
                livingEntity -> {
                    String entityID = livingEntity.getType().builtInRegistryHolder().key().location().toString();
                    if (bossIDs.contains(entityID)) {
                        id.set(entityID);
                        return true;
                    }
                    return false;
                }
        );
        LivingEntity boss = bosses.isEmpty() ? null : bosses.get(0);
        if (boss != null && id.get() != null) {
            ResolvedBossMusic bm = resolvedBosses.get(id.get());
            String phaseTag = detectBossPhase(boss, bm);
            ResolvedBossMusic.Phase phase = bm.phases().get(phaseTag);
            if (phaseTag == null || phase == null) return null;
            if (boss.distanceTo(player) <= phase.distance()) {
                if (ADJMusicManager.getInstance().getCurrentlyPlayingMusic() != phase.track()) { // only send when track changes
                    sendBossMusicMessage(phase);
                }
                return phase.track();
            }
        }
        return null;
    }

    private static void sendBossMusicMessage(ResolvedBossMusic.Phase phase) {
        if (phase.author != null) {
            Minecraft mc = Minecraft.getInstance();
            mc.gui.setOverlayMessage(Component.literal("Now playing: " + phase.author + " - " + phase.title), true);
        }
    }

    public static List<String> getSyncedTags(LivingEntity boss) {
        ADJMessages.sendToServer(new RequestEntityTagsC2SPacket(boss.getId()));
        String s = boss.getPersistentData().getString("adjcore_synced_tags");
        if (s.isEmpty()) return new ArrayList<>();
        return Arrays.stream(s.split(";"))
                .sorted()
                .toList();
    }

    private static String detectBossPhase(LivingEntity boss, ResolvedBossMusic bm) {
        List<String> tags = getSyncedTags(boss);
        List<String> phases = bm.phases().keySet().stream()
                .sorted()
                .toList();
        for (int i = phases.size() - 1; i >= 0; i--) {
            String tag = phases.get(i);
            if (tags.contains(tag)) {
                return tag;
            }
        }
        return null;
    }


    private static Music getTrack(String str) {
        if (str == null) return null;
        ResourceLocation id = ResourceLocation.parse(str);
        Optional<Holder<SoundEvent>> optional = ForgeRegistries.SOUND_EVENTS.getHolder(id);
        if (optional.isEmpty()) {
            ADJCore.LOGGER.error("Unknown sound event {}", str);
            return null;
        }
        Holder<SoundEvent> holder = optional.get();
        return new Music(holder, 10, 20, false);
    }

    private enum TrackVariant {
        DAY,
        NIGHT,
        RAIN,
        THUNDER,
        BLOOD_MOON;

        TrackVariant() {
        }

        public static TrackVariant getVariant(Level level) {
            if (level.isThundering()) return TrackVariant.THUNDER;
            else if (level.isRaining()) return TrackVariant.RAIN;
            long time = level.getDayTime();
            if (time % 24000L < 12550) {
                return TrackVariant.DAY;
            }
            return TrackVariant.NIGHT;
        }

        public String getId() {
            return this.name().toLowerCase();
        }
    }

    public record ResolvedEntry(Music track, Map<String, Music> conditions) {
    }

    public record ResolvedBossMusic(Map<String, Phase> phases) {
        public record Phase(Music track, String title, String author, int distance) {
        }
    }


}
