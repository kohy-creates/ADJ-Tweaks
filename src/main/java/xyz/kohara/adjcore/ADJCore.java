package xyz.kohara.adjcore;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ServerChatEvent;
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
import xyz.kohara.adjcore.client.music.ADJMusicData;
import xyz.kohara.adjcore.client.music.ADJMusicManager;
import xyz.kohara.adjcore.client.networking.ADJMessages;
import xyz.kohara.adjcore.combat.DamageHandler;
import xyz.kohara.adjcore.combat.ExtraLivingDrops;
import xyz.kohara.adjcore.compat.ArsSpellPowerEdit;
import xyz.kohara.adjcore.compat.curios.CurioControl;
import xyz.kohara.adjcore.effecteditor.EffectsEditor;
import xyz.kohara.adjcore.entity.HardcoreTweaks;
import xyz.kohara.adjcore.entity.WanderingTraderEdits;
import xyz.kohara.adjcore.misc.DelayedTaskScheduler;
import xyz.kohara.adjcore.misc.LangGenerator;
import xyz.kohara.adjcore.misc.ParticleTextIndicators;
import xyz.kohara.adjcore.misc.credits.LoaderInfo;
import xyz.kohara.adjcore.misc.credits.ModCreditsBase;
import xyz.kohara.adjcore.misc.credits.ModInfo;
import xyz.kohara.adjcore.potions.PotionsEditor;
import xyz.kohara.adjcore.registry.*;
import xyz.kohara.adjcore.registry.capabilities.CapabilityEvents;
import xyz.kohara.adjcore.registry.effects.EffectsHandler;

import java.util.*;

import static com.hollingsworth.arsnouveau.client.jei.MultiInputCategory.rotatePointAbout;

@Mod(ADJCore.MOD_ID)
public class ADJCore {

    public static ModCreditsBase impl = initModCredits();

    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "adjcore";

    public ADJCore() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC, MOD_ID + ".toml");
        ADJSoundEvents.registerSounds();

        IEventBus MOD_BUS = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus FORGE_BUS = MinecraftForge.EVENT_BUS;
        MOD_BUS.addListener(this::commonSetup);
        MOD_BUS.addListener(this::clientSetup);
        MOD_BUS.addListener(ADJAttributes::addEntityAttributes);
        MOD_BUS.addListener(ADJCapabilities::register);
        MOD_BUS.addListener(LangGenerator::gatherData);

        FORGE_BUS.register(ADJCore.class);
        FORGE_BUS.register(DamageHandler.class);
        FORGE_BUS.register(DelayedTaskScheduler.class);
        FORGE_BUS.register(WanderingTraderEdits.class);
        FORGE_BUS.register(CurioControl.class);
        FORGE_BUS.register(CapabilityEvents.class);
        FORGE_BUS.register(ParticleTextIndicators.class);
        FORGE_BUS.register(HardcoreTweaks.class);
        FORGE_BUS.register(ArsSpellPowerEdit.class);
        FORGE_BUS.register(ExtraLivingDrops.class);
        FORGE_BUS.register(EffectsHandler.class);

        initRegistries(MOD_BUS);

        new ADJMusicManager();
    }

    private void initRegistries(IEventBus bus) {
        ADJBiomeModifiers.register(bus);
        ADJAttributes.register(bus);
        ADJEffects.register(bus);
        ADJSoundEvents.SOUND_EVENTS.register(bus);
        ADJMusicData.load(bus);
        ADJPlacementModifierTypes.register(bus);
        ADJParticles.register(bus);
        ADJFluidTypes.register(bus);
        ADJFluids.register(bus);
        ADJBlocks.register(bus);
        ADJItems.register(bus);
        ADJEntities.register(bus);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(ADJMessages::register);
        PotionsEditor.edit();
        EffectsEditor.edit();
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
    public static void onServerChat(ServerChatEvent event) {
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

    public static String deathMessageToFirstPerson(Player player, Component msg) {
        return deathMessageToFirstPerson(player, msg.getString());
    }

    public static String deathMessageToFirstPerson(Player player, String msg) {
        String name = player.getName().getString();

        if (msg.indexOf(name) == 0) {
            msg = msg.replaceFirst(name, "You");
            msg = msg.replaceFirst(name + "'s", "Your");
        } else {
            msg = msg.replaceFirst(name, "you");
            msg = msg.replaceFirst(name + "'s", "your");
        }
        msg = msg.replaceFirst("You was", "You were");
        msg = msg.replace("their", "your");

        return msg;
    }

    public static List<Player> getPlayersInRadius(Level level, BlockPos center, double radius) {
        AABB box = new AABB(
                center.getX() - radius, center.getY() - radius, center.getZ() - radius,
                center.getX() + radius, center.getY() + radius, center.getZ() + radius
        );

        List<Player> players = level.getEntitiesOfClass(Player.class, box);

        players.removeIf(p -> p.distanceToSqr(center.getX() + 0.5, center.getY() + 0.5, center.getZ() + 0.5) > radius * radius);
        return players;
    }

    public static Player getNearestPlayerWithinRadius(Entity entity, double radius) {
        return getPlayersInRadius(entity.level(), entity.blockPosition(), radius)
                .stream()
                .min(Comparator.comparingDouble(p -> p.distanceToSqr(entity)))
                .orElse(null);
    }

    public static Vec2
            point = new Vec2(48, 13),
            center = new Vec2(48, 45);

    @OnlyIn(Dist.CLIENT)
    public static void expandArsEMIGuis(GuiGraphics guiGraphics, int ingrAmount, int sourceCost) {

        double angleBetweenEach = 360.0 / ingrAmount;

        var pose = guiGraphics.pose();

        pose.pushPose();
        pose.translate(-1d, -1d, 1d);
        pose.scale(1.5f, 1.5f, 1.5f);

        for (int i = 0; i < ingrAmount; i++) {
            guiGraphics.blit(
                    ResourceLocation.fromNamespaceAndPath("ars_nouveau", "textures/block/arcane_pedestal.png"),
                    (int) ((int) point.x / 1.5f), (int) ((int) point.y / 1.5f),
                    0, 0,
                    12, 12,
                    32, 32
            );
            point = rotatePointAbout(point, center, angleBetweenEach);
        }

        pose.popPose();

        if (sourceCost > 0) {
            Font renderer = Minecraft.getInstance().font;
            guiGraphics.drawString(renderer, Component.literal("Mᴀɴᴀ Cᴏsᴛ: "), 0, 101 - renderer.lineHeight, 10, false);
            guiGraphics.drawString(renderer, Component.literal(sourceCost / 100 + "% ᴏғ ᴀ Mᴀɴᴀ Jᴀʀ"), 0, 101, 10, false);
        }
    }
}

