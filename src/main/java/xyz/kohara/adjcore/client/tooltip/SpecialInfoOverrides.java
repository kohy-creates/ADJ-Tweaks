package xyz.kohara.adjcore.client.tooltip;

import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import xyz.kohara.adjcore.ADJCore;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.*;

@OnlyIn(Dist.CLIENT)
public class SpecialInfoOverrides {

    private static final String folder = "config/" + ADJCore.MOD_ID + "/item_traits_overrides/";
    public static final List<String> defaultTraits = List.of(
            "consumable",
            "can_be_placed",
            "material",
            "equipable"
    );
    private static String configPath(String name) {
        return folder + "default/" + name + ".txt";
    }

    private static final Map<String, Set<String>> DEFAULT_OVERRIDES = new HashMap<>();
    private static final Map<String, Set<String>> CUSTOM_OVERRIDES = new HashMap<>();

    static {
        for (String defaultTrait : defaultTraits) {
            File config = new File(configPath(defaultTrait));
            Set<String> entries = new HashSet<>();
            try {
                if (!config.exists()) {
                    //noinspection ResultOfMethodCallIgnored
                    config.createNewFile();
                }
                Scanner reader = new Scanner(config);
                while (reader.hasNext()) {
                    String line = reader.nextLine();
                    entries.add(line);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            DEFAULT_OVERRIDES.put(defaultTrait, entries);
        }

        File[] files = new File(folder).listFiles((dir, name) -> name.toLowerCase().endsWith(".txt"));
        if (files != null) {
            for (File file : files) {
                String name = null;
                Set<String> entries = new HashSet<>();
                try {
                    Scanner reader = new Scanner(file);
                    int i = 0;
                    while (reader.hasNext()) {
                        String line = reader.nextLine();
                        if (i == 0) {
                            name = line;
                        }
                        else {
                            entries.add(line);
                        }
                        i++;
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                if (name != null) {
                    CUSTOM_OVERRIDES.put(name, entries);
                }
            }
        }
    }

    @Nullable
    public static OverrideEntry getDefaultOverrideFor(Item item, String category) {

        if (!defaultTraits.contains(category)) return null;

        String id = ForgeRegistries.ITEMS.getKey(item).toString();

        if (DEFAULT_OVERRIDES.containsKey(category)) {
            Set<String> entries = DEFAULT_OVERRIDES.get(category);
            String override = null;
            for (String entry : entries) {
                boolean pseudoRegex = false;
                if (entry.startsWith("/")) {
                    pseudoRegex = true;
                    entry = entry.substring(1);
                }
                String check = entry;
                if (entry.startsWith("-")) {
                    check = check.substring(1);
                }

                if ((id.contains(check) && pseudoRegex) || id.equals(check)) {
                    override = entry;
                    break;
                }
            }
            if (override == null) return null;
            return new OverrideEntry(category, override.startsWith("-"), null);
        }
        return null;
    }

    @Nullable
    public static OverrideEntry getCustomOverrideFor(Item item) {

        String id = ForgeRegistries.ITEMS.getKey(item).toString();

        Set<String> customTraits = new HashSet<>();
        for (String name : CUSTOM_OVERRIDES.keySet()) {
            for (String s : CUSTOM_OVERRIDES.get(name)) {
                boolean pseudoRegex = false;
                if (s.startsWith("/")) {
                    pseudoRegex = true;
                    s = s.substring(1);
                }
                if ((id.contains(s) && pseudoRegex) || id.equals(s)) {
                    customTraits.add(name);
                    break;
                }
            }
        }
        return (customTraits.isEmpty()) ? null : new OverrideEntry(id, null, customTraits);
    }

    public record OverrideEntry(String entry, @Nullable Boolean remove, @Nullable Set<String> names) {

        public boolean shouldRemove() {
            return (remove != null) ? this.remove : false;
        }

        public String getName() {
            return this.entry;
        }

        public Set<String> getNames() {
            return this.names;
        }
    }
}
