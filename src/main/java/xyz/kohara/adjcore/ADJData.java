package xyz.kohara.adjcore;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class ADJData {

	private static final String BASE_CONFIG = "config/" + ADJCore.MOD_ID + "/";

	private static final String DEATH_TEXTS_FILE = BASE_CONFIG + "death_text.txt";
	private static final String STRUCTURES_IGNORE_MIN_DISTANCE_FILE = BASE_CONFIG + "structures_ignore_min_distance.txt";
	private static final String POTION_NAME_OVERRIDES_FILE = BASE_CONFIG + "potion_name_overrides.txt";
	private static final String WINDOW_TITLES_FILE = BASE_CONFIG + "window_titles.txt";
	private static final String ATTRIBUTES_TOOLTIP_ORDER_FILE = BASE_CONFIG + "attributes_tooltip_order.txt";
	private static final String EXTRA_HEART_DROP_RULES = BASE_CONFIG + "extra_heart_drop_rules.txt";
	private static final String CURIO_SLOTS_TO_KEEP = BASE_CONFIG + "curio_slots_to_keep.txt";

	private static final List<String> deathTexts = new ArrayList<>();
	public static final List<String> structuresIgnoreMinDistance = new ArrayList<>();
	public static final Map<String, String> potionNameOverrides = new HashMap<>();
	public static final List<String> windowTitles = new ArrayList<>();
	public static final List<ResourceLocation> attributesTooltipOrder = new ArrayList<>();
	public static final Map<ResourceLocation, HeartDropRule> heartDropRules = new HashMap<>();
	public static final List<String> curioSlotsToKeep = new ArrayList<>();

	static {
		reloadEverythingReloadable();
	}

	public static void reloadEverythingReloadable() {
		deathTexts.clear();
		structuresIgnoreMinDistance.clear();
		potionNameOverrides.clear();
		windowTitles.clear();
		attributesTooltipOrder.clear();

		deathTexts.addAll(readLines(DEATH_TEXTS_FILE));
		structuresIgnoreMinDistance.addAll(readLines(STRUCTURES_IGNORE_MIN_DISTANCE_FILE));
		potionNameOverrides.putAll(readMap(POTION_NAME_OVERRIDES_FILE, ":"));
		windowTitles.addAll(readLines(WINDOW_TITLES_FILE));

		readLines(ATTRIBUTES_TOOLTIP_ORDER_FILE).stream()
				.map(String::trim)
				.filter(s -> !s.isEmpty() && !s.startsWith("#"))
				.map(ResourceLocation::parse)
				.forEach(attributesTooltipOrder::add);

		readLines(EXTRA_HEART_DROP_RULES).stream()
				.map(String::trim)
				.filter(s -> !s.isEmpty() && !s.startsWith("#"))
				.forEach(s -> {
					String[] data = s.split("/");
					ResourceLocation loc = ResourceLocation.parse(data[0]);
					double chance = Double.parseDouble(data[2]);
					if (data[1].contains("-")) {
						String[] bounds = data[1].split("-");
						int min = Integer.parseInt(bounds[0]);
						int max = Integer.parseInt(bounds[1]);
						heartDropRules.put(loc, new HeartDropRule(min, max, chance));
					} else {
						int amount = Integer.parseInt(data[1]);
						heartDropRules.put(loc, new HeartDropRule(amount, chance));
					}

				});

		curioSlotsToKeep.addAll(readLines(CURIO_SLOTS_TO_KEEP));


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
		if (deathTexts.isEmpty())
			return "\"\"";

		return "\"" + deathTexts.get(new Random().nextInt(deathTexts.size())) + "\"";
	}

	public static Comparator<Attribute> attributeComparator() {

		return (a, b) -> {
			ResourceLocation aId = ForgeRegistries.ATTRIBUTES.getKey(a);
			ResourceLocation bId = ForgeRegistries.ATTRIBUTES.getKey(b);

			int aIndex = attributesTooltipOrder.indexOf(aId);
			int bIndex = attributesTooltipOrder.indexOf(bId);

			// both explicitly ordered
			if (aIndex != -1 && bIndex != -1)
				return Integer.compare(aIndex, bIndex);

			// one explicitly ordered
			if (aIndex != -1) return -1;
			if (bIndex != -1) return 1;

			// fallback alphabetical
			return aId.toString().compareTo(bId.toString());
		};
	}

	public static final class TooltipInfoOverrides {

		private static final String FOLDER = BASE_CONFIG + "item_traits_overrides/";
		private static final String DEFAULT_FOLDER = FOLDER + "default/";

		public static final List<String> defaultTraits = List.of(
				"consumable",
				"can_be_placed",
				"material",
				"equipable"
		);

		private static final Map<String, Set<String>> DEFAULT_OVERRIDES = new HashMap<>();
		private static final Map<String, Set<String>> CUSTOM_OVERRIDES = new HashMap<>();

		static {
			reloadOverrides();
		}

		public static void reloadOverrides() {
			DEFAULT_OVERRIDES.clear();
			CUSTOM_OVERRIDES.clear();

			loadDefaultOverrides();
			loadCustomOverrides();
		}

		private static void loadDefaultOverrides() {
			for (String trait : defaultTraits) {
				File file = new File(DEFAULT_FOLDER + trait + ".txt");
				DEFAULT_OVERRIDES.put(trait, readFileAsSet(file));
			}
		}

		private static void loadCustomOverrides() {
			File folder = new File(FOLDER);
			File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".txt"));

			if (files == null) return;

			for (File file : files) {
				List<String> lines = readFileAsList(file);
				if (lines.isEmpty()) continue;

				String name = lines.get(0);
				Set<String> entries = new HashSet<>(lines.subList(1, lines.size()));
				CUSTOM_OVERRIDES.put(name, entries);
			}
		}

		private static Set<String> readFileAsSet(File file) {
			return new HashSet<>(readFileAsList(file));
		}

		private static List<String> readFileAsList(File file) {
			try {
				file.getParentFile().mkdirs();
				file.createNewFile();
				return Files.readAllLines(file.toPath());
			} catch (IOException e) {
				throw new RuntimeException("Failed to read override file: " + file, e);
			}
		}

		@Nullable
		public static OverrideEntry getDefaultOverrideFor(Item item, String category) {
			if (!defaultTraits.contains(category))
				return null;

			String id = ForgeRegistries.ITEMS.getKey(item).toString();
			Set<String> entries = DEFAULT_OVERRIDES.get(category);

			if (entries == null)
				return null;

			for (String entry : entries) {
				if (matches(id, entry)) {
					return new OverrideEntry(category, entry.startsWith("-"), null);
				}
			}

			return null;
		}

		@Nullable
		public static OverrideEntry getCustomOverrideFor(Item item) {
			String id = ForgeRegistries.ITEMS.getKey(item).toString();

			LinkedHashSet<String> finalTraits = new LinkedHashSet<>();

			for (String trait : defaultTraits) {
				OverrideEntry def = getDefaultOverrideFor(item, trait);

				if (def != null && def.shouldRemove())
					continue;

				finalTraits.add(trait);
			}
			for (var entry : CUSTOM_OVERRIDES.entrySet()) {
				String traitName = entry.getKey();

				for (String rule : entry.getValue()) {
					if (!matches(id, rule))
						continue;

					if (rule.startsWith("-")) {
						finalTraits.remove(traitName);
					} else {
						finalTraits.add(traitName);
					}

					break;
				}
			}

			LinkedHashSet<String> base = new LinkedHashSet<>();
			for (String trait : defaultTraits) {
				OverrideEntry def = getDefaultOverrideFor(item, trait);
				if (def == null || !def.shouldRemove()) {
					base.add(trait);
				}
			}

			return finalTraits.equals(base) ? null : new OverrideEntry(id, null, finalTraits);

		}


		private static boolean matches(String id, String rule) {
			if (rule.isBlank() || rule.startsWith("#")) {
				return false;
			}
			boolean remove = rule.startsWith("-");
			if (remove) {
				rule = rule.substring(1);
			}

			if (rule.length() > 2 && rule.startsWith("\\") && rule.endsWith("\\")) {
				String regex = rule.substring(1, rule.length() - 1);
				return id.matches(regex);
			}

			return id.equals(rule);
		}


		public record OverrideEntry(
				String entry,
				@Nullable Boolean remove,
				@Nullable Set<String> names
		) {
			public boolean shouldRemove() {
				return Boolean.TRUE.equals(remove);
			}
		}
	}

	public static class HeartDropRule {

		private final int min;
		private final int max;
		private final double chance;

		private HeartDropRule(int amount, double chance) {
			this.min = amount;
			this.max = amount;
			this.chance = chance;
		}

		private HeartDropRule(int min, int max, double chance) {
			this.min = min;
			this.max = max;
			this.chance = chance;
		}

		public int getDropAmount() {
			if (this.min == this.max) {
				return this.max;
			}
			return new Random().nextInt(this.max - this.min + 1) + this.min;
		}

		public double getChance() {
			return this.chance;
		}
	}
}
