package xyz.kohara.adjcore.music;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.sounds.MusicManager;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.registries.ForgeRegistries;
import xyz.kohara.adjcore.ADJCore;

import java.util.Optional;
import java.util.function.Predicate;

public class MusicPlayer {

    private static final Predicate<WitherBoss> WITHER_PREDICATE = (entity) -> true;
    private static final Predicate<LivingEntity> ENTITY_PREDICATE = (entity) -> true;
    private static Music[] bossMusic;

    public static Music CURRENT_TRACK = null;

    private static boolean isPlayingBossMusic(MusicManager musicManager) {
        for (Music music : bossMusic) {
            if (musicManager.isPlayingMusic(music)) {
                return true;
            }
        }

        return false;
    }

    private static String getMonsterName(LivingEntity e) {
        return e.getName().toString().replace("translation{key='", "").replace("', args=[]}", "");
    }

    public static Music findMusic(MusicManager musicManager) {
        MusicConfig config = MusicConfig.CONFIG;
        boolean isBoss = false;
        String track = config.menu;

        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            track = config.defaults.get("minecraft:overworld").conditions.get("day");

            String dimension = player.level().dimension().location().toString();
            Holder<Biome> biome = player.level().getBiome(player.getOnPos());
            ResourceLocation biomeRL = biome.unwrapKey().map(ResourceKey::location).orElse(ResourceLocation.fromNamespaceAndPath("unknown", "unknown"));
            String biomeName = biomeRL.toString();

            for (String key : config.defaults.keySet()) {
                if (dimension.equals(key)) {
                    MusicConfig.MusicEntry entry = config.defaults.get(key);

                    if (entry.track != null) {
                        track = entry.track;
                    } else {
                        track = entry.conditions.get(getVariant(player.level()).id);
                    }
                    break;
                }
            }

            for (String key : config.biome.keySet()) {
                boolean found = false;
                if (key.indexOf("#") == 0 && biome.is(TagKey.create(Registries.BIOME, ResourceLocation.parse(key.replace("#", ""))))) {
                    System.out.println("is in tag " + key);
                    found = true;
                } else if (biomeName.equals(key)) {
                    System.out.println("in biome " + key);
                    found = true;
                }
                if (found) {
                    MusicConfig.MusicEntry entry = config.biome.get(key);
                    if (entry.track != null) {
                        track = entry.track;
                    } else {
                        String checkTrack = null;
                        checkTrack = entry.conditions.get(getVariant(player.level()).id);

                        if (checkTrack != null) {
                            track = checkTrack;
                        }
                    }
                }
            }

        }
        System.out.println(track);
        Music music = getTrack(track, isBoss);
        if (CURRENT_TRACK == null || CURRENT_TRACK != music) {
            if (CURRENT_TRACK != null) musicManager.stopPlaying(CURRENT_TRACK);
            CURRENT_TRACK = music;
        }
        return music;
    }

    private static Music getTrack(String str, boolean replace) {
        ResourceLocation id = ResourceLocation.parse(str);
        Optional<Holder<SoundEvent>> optional = ForgeRegistries.SOUND_EVENTS.getHolder(id);
        if (optional.isEmpty()) {
            ADJCore.LOGGER.error("Unknown sound event {}", str);
            return null;
        }
        Holder<SoundEvent> holder = optional.get();
        return new Music(holder, 0, 0, replace);
    }

    public static boolean shouldPlayMusic(MusicManager musicManager) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return true;
        } else {
            String dimension = player.level().dimension().location().toString();
            double i = player.getY();
            double j = player.getX();
            double k = player.getZ();
            float f = 50.0F;
            AABB box = new AABB((float) i - f, (float) j - f, (float) k - f, (float) (i + (double) 1.0F) + f, (float) (j + (double) 1.0F) + f, (float) (k + (double) 1.0F) + f);

            for (LivingEntity e : player.level().getEntities(EntityTypeTest.forClass(LivingEntity.class), box, ENTITY_PREDICATE)) {
                if (!dimension.equals("javd:void") && (getMonsterName(e).contains("entity.witherstormmod.witherstorm") || getMonsterName(e).contains("entity.aquamirae.captain_cornelia") || getMonsterName(e).contains("entity.blue_skies.summoner") || getMonsterName(e).contains("entity.blue_skies.alchemist") || getMonsterName(e).contains("entity.unusualend.endstone_golem"))) {
                    return false;
                }
            }

            return true;
        }
    }

    private static TrackVariant getVariant(Level level) {
        if (level.isThundering()) return TrackVariant.THUNDER;
        else if (level.isRaining()) return TrackVariant.RAIN;

        long time = level.getDayTime();
        if (time % 24000L < 12550) {
            return TrackVariant.DAY;
        } /* else if (time % 24000L > 23000L && time % 24000L < 0L) {
            return TrackVariant.SUNRISE;
        } else if (time % 24000L > 12000L && time % 24000L < 13000L) {
            return TrackVariant.SUNSET;
        } */
        return TrackVariant.NIGHT;
    }

    private enum TrackVariant {
        //SUNRISE("sunrise"),
        DAY("day"),
        //SUNSET("sunset"),
        NIGHT("night"),

        RAIN("rain"),
        THUNDER("thunder");

        public String id;

        TrackVariant(String text) {
            this.id = text;
        }

    }
}
