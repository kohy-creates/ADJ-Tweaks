package xyz.kohara.adjcore.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.worldselection.WorldSelectionList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.LevelSummary;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xyz.kohara.adjcore.ADJCore;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class WorldIconRenderer {

    private static final List<ResourceLocation> WORLD_ICONS = List.of(
            ResourceLocation.fromNamespaceAndPath(ResourceLocation.DEFAULT_NAMESPACE, "textures/misc/unknown_server.png"),
            ADJCore.of("textures/gui/icon_chapter_0.png"),
            ADJCore.of("textures/gui/icon_chapter_1.png"),
            ADJCore.of("textures/gui/icon_chapter_2.png"),
            ADJCore.of("textures/gui/icon_chapter_3.png"),
            ADJCore.of("textures/gui/icon_chapter_4.png"),
            ADJCore.of("textures/gui/icon_chapter_5.png")
    );

    private static final List<ResourceLocation> BORDERS = List.of(
            ADJCore.of("textures/gui/world_border.png"),
            ADJCore.of("textures/gui/world_border_complete.png")
    );

    private static int getChapterNumber(String chapter) {
        return Integer.parseInt(chapter.split("_")[1]);
    }

    public static ResourceLocation getIcon(int chapter) {
        return WORLD_ICONS.get(chapter + 1);
    }

    public static ResourceLocation getBorder(boolean complete) {
        return BORDERS.get((complete) ? 1 : 0);
    }

    private static File getKubeJSSaveFile(WorldSelectionList.WorldListEntry entry) {
        LevelSummary summary = entry.summary;
        String folderName = summary.getLevelId();
        Path worldFolder = Minecraft.getInstance().gameDirectory.toPath().resolve("saves").resolve(folderName);

        Path kubeFile = worldFolder.resolve("kubejs_persistent_data.nbt");
        return kubeFile.toFile();
    }

    public static int getChapter(WorldSelectionList.WorldListEntry entry) {

        File kubeFile = getKubeJSSaveFile(entry);

        if (Files.exists(kubeFile.toPath())) {
            try {
                CompoundTag tag = NbtIo.readCompressed(kubeFile);
                String currentChapter = tag.getCompound("chapters").getString("current_stage");

                if (currentChapter.split("_").length == 2) {
                    return getChapterNumber(currentChapter);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return -1;
    }

    public static boolean isCompleted(WorldSelectionList.WorldListEntry entry) {
        File kubeFile = getKubeJSSaveFile(entry);

        if (Files.exists(kubeFile.toPath())) {
            try {
                CompoundTag tag = NbtIo.readCompressed(kubeFile);
                return tag.getBoolean("is_completed");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
