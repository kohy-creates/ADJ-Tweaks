package xyz.kohara.adjcore;

import dev.ftb.mods.ftbquests.quest.QuestObject;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.kohara.adjcore.registry.*;
import xyz.kohara.adjcore.client.music.JukeboxTracker;
import xyz.kohara.adjcore.client.music.MusicConfig;
import xyz.kohara.adjcore.client.networking.ADJMessages;
import xyz.kohara.adjcore.combat.DamageHandler;
import xyz.kohara.adjcore.combat.DamageIndicators;
import xyz.kohara.adjcore.compat.curios.CurioControl;
import xyz.kohara.adjcore.registry.effects.editor.EffectsEditor;
import xyz.kohara.adjcore.entity.WanderingTraderEdits;
import xyz.kohara.adjcore.misc.DelayedTaskScheduler;
import xyz.kohara.adjcore.misc.LangGenerator;
import xyz.kohara.adjcore.registry.capabilities.CapabilityEvents;
import xyz.kohara.adjcore.misc.credits.LoaderInfo;
import xyz.kohara.adjcore.misc.credits.ModCreditsBase;
import xyz.kohara.adjcore.misc.credits.ModInfo;
import xyz.kohara.adjcore.potions.PotionsEditor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Mod(ADJCore.MOD_ID)
public class ADJCore {

    public static ModCreditsBase impl = initModCredits();

    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "adjcore";

    public ADJCore() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC, MOD_ID + ".toml");
        ADJSoundEvents.registerSounds();

        IEventBus MOD_BUS = FMLJavaModLoadingContext.get().getModEventBus();
        MOD_BUS.addListener(this::commonSetup);
        MOD_BUS.addListener(this::clientSetup);
        MOD_BUS.addListener(this::addEntityAttributes);
        MOD_BUS.addListener(ADJCapabilities::register);
        MOD_BUS.addListener(LangGenerator::gatherData);

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new DamageHandler());
        MinecraftForge.EVENT_BUS.register(DelayedTaskScheduler.class);
        MinecraftForge.EVENT_BUS.register(WanderingTraderEdits.class);
        MinecraftForge.EVENT_BUS.register(CurioControl.class);
        MinecraftForge.EVENT_BUS.register(CapabilityEvents.class);
        MinecraftForge.EVENT_BUS.register(new DamageIndicators());

        JukeboxTracker.init();

        initRegistries(MOD_BUS);

    }

    private void initRegistries(IEventBus bus) {
        ADJBiomeModifiers.register(bus);
        ADJEffects.register(bus);
        ADJSoundEvents.SOUND_EVENTS.register(bus);
        ADJAttributes.register(bus);
        MusicConfig.load(bus);
        ADJPlacementModifierTypes.register(bus);
        ADJParticles.register(bus);
        ADJFluidTypes.register(bus);
        ADJFluids.register(bus);
        ADJBlocks.register(bus);
        ADJItems.register(bus);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(ADJMessages::register);
        PotionsEditor.edit();
        EffectsEditor.edit();

        QuestObject object = null;
    }

    private void clientSetup(final FMLClientSetupEvent event) {

    }

    public static ResourceLocation of(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    private static ModCreditsBase initModCredits() {
        return new ModCreditsBase() {
            @Override
            public List<ModInfo> getMods() {
                List<ModInfo> mods = new ArrayList<>();

                ModList.get().forEachModContainer((id, container) -> {
                    if (!(id.equals("minecraft")
                            || id.equals("forge")
                            || id.startsWith("generated_"))) {
                        String[] authors = new String[]{};
                        Optional<Object> modAuthors = container.getModInfo().getConfig().getConfigElement("authors");
                        if (modAuthors.isPresent())
                            authors = modAuthors.get().toString().split("[,\\s]+");
                        mods.add(new ModInfo(
                                container.getModInfo().getDisplayName(),
                                List.of(authors),
                                container.getModId()
                        ));
                    }
                });

                return mods;
            }

            @Override
            public LoaderInfo getLoaderInfo() {
                return new LoaderInfo(
                        "Forge Loader",
                        "Forge",
                        List.of("Forge Team"));
            }
        };
    }

    public static Component formatDeathMessage(Component deathMessage) {
        return Component.empty()
                .append(Component.literal("s").withStyle(Style.EMPTY.withFont(ResourceLocation.parse("adjcore:icons"))))
                .append(Component.literal(" "))
                .append(deathMessage.copy().withStyle(Style.EMPTY.withColor(TextColor.parseColor("#FF1919"))));
    }

    @SubscribeEvent
    public void onServerChat(ServerChatEvent event) {
        event.setCanceled(true);

        Component message = event.getMessage();
        ServerPlayer player = event.getPlayer();
        MinecraftServer server = player.getServer();

        Component newMessage = Component.empty()
                .append(Component.literal("c").withStyle(Style.EMPTY.withFont(ResourceLocation.parse("adjcore:icons"))))
                .append(Component.literal(" [").withStyle(Style.EMPTY.withColor(TextColor.parseColor("#8A8A8A"))))
                .append(player.getName())
                .append(Component.literal("] » ").withStyle(Style.EMPTY.withColor(TextColor.parseColor("#8A8A8A"))))
                .append(message);

        server.getPlayerList().getPlayers().forEach(serverPlayer -> {
            if (serverPlayer.acceptsChatMessages()) {
                serverPlayer.sendSystemMessage(newMessage);
            }
        });
    }

    public void addEntityAttributes(EntityAttributeModificationEvent event) {
        for (EntityType<? extends LivingEntity> type : event.getTypes()) {
            event.add(type, ADJAttributes.DAMAGE_REDUCTION.get());
            event.add(type, ADJAttributes.PROJECTILE_DAMAGE_REDUCTION.get());
            event.add(type, ADJAttributes.SAFE_FALL_DISTANCE.get());
            event.add(type, ADJAttributes.HEALTH_REGEN.get());
        }

        event.add(EntityType.PLAYER, ADJAttributes.MANA_COST_REDUCTION.get());
        event.add(EntityType.PLAYER, ADJAttributes.EXTRA_ORE_DROPS.get());
    }

    private static final String deathTextsFile = "config/" + ADJCore.MOD_ID + "/death_text.txt";
    private static final List<String> deathTexts = new ArrayList<>();

    private static final String structuresIgnoreMinDistanceFile = "config/" + ADJCore.MOD_ID + "/structures_ignore_min_distance.txt";
    public static final List<String> structuresIgnoreMinDistance = new ArrayList<>();

    private static final String potionNameOverridesFile = "config/" + ADJCore.MOD_ID + "/potion_name_overrides.txt";
    public static final Map<String, String> potionNameOverrides = new HashMap<>();

    private static final String windowTitlesFile = "config/" + ADJCore.MOD_ID + "/window_titles.txt";
    public static final List<String> windowTitles = new ArrayList<>();

    static {
        deathTexts.addAll(
                readLines(deathTextsFile)
        );

        structuresIgnoreMinDistance.addAll(
                readLines(structuresIgnoreMinDistanceFile)
        );

        potionNameOverrides.putAll(
                readMap(potionNameOverridesFile, ":")
        );

        windowTitles.addAll(
                readLines(windowTitlesFile)
        );
    }

    private static List<String> readLines(String path) {
        try {
            Path file = Paths.get(path);
            Files.createDirectories(file.getParent());
            if (Files.notExists(file)) {
                Files.createFile(file);
            }
            return Files.readAllLines(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read config: " + path, e);
        }
    }

    private static Map<String, String> readMap(String path, String separator) {
        Map<String, String> map = new HashMap<>();
        for (String line : readLines(path)) {
            if (line.isBlank() || !line.contains(separator)) continue;

            String[] parts = line.split(separator, 2);
            map.put(parts[0].trim(), parts[1].trim());
        }
        return map;
    }

    public static String getRandomDeathText() {
        Random rand = new Random();
        return "\"" + deathTexts.get(rand.nextInt(deathTexts.size())) + "\"";
    }

    public static String toSmallUnicode(String s) {
        Map<Character, Character> map = new HashMap<>();
        String[] mappings = {"aᴀ", "bʙ", "cᴄ", "dᴅ", "eᴇ", "fꜰ", "gɢ", "hʜ", "iɪ", "jᴊ", "kᴋ", "lʟ", "mᴍ", "nɴ", "oᴏ", "pᴘ", "rʀ", "sѕ", "tᴛ", "uᴜ", "wᴡ", "xх", "yʏ", "zᴢ"};
        for (String pair : mappings) {
            map.put(pair.charAt(0), pair.charAt(1));
        }
        StringBuilder result = new StringBuilder();
        for (char c : s.toLowerCase().toCharArray()) {
            result.append(map.getOrDefault(c, c));
        }
        return result.toString();
    }
}
