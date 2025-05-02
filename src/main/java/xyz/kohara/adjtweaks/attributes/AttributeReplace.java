package xyz.kohara.adjtweaks.attributes;

import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import xyz.kohara.adjtweaks.ADJTweaks;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.util.*;

@Mod.EventBusSubscriber(modid = ADJTweaks.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AttributeReplace {

    private static final String CONFIG_FILE = "config/adjtweaks/attribute_replace.json";
    private static AttributeConfig REPLACEMENTS;

    static {
        loadConfig();
    }

    public static void loadConfig() {
        File file = new File(CONFIG_FILE);

        try {
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
                try (FileWriter writer = new FileWriter(file)) {
                    writer.write("{}");
                }
            }
            try (Reader reader = new FileReader(file)) {
                REPLACEMENTS = new Gson().fromJson(reader, AttributeConfig.class);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load attribute replacement config", e);
        }
        System.out.println("Global:");
        REPLACEMENTS.global.forEach((k, v) -> System.out.println(k + " -> " + v));

        System.out.println("\nItems:");
        REPLACEMENTS.items.forEach((itemId, itemConfig) -> {
            System.out.println("Item: " + itemId);
            if (itemConfig.replace != null) {
                System.out.println("  Replace:");
                itemConfig.replace.forEach((k, v) -> System.out.println("    " + k + " -> " + v));
            }
            if (itemConfig.add != null) {
                System.out.println("  Add:");
                itemConfig.add.forEach(a -> System.out.println("    " + a));
            }
            if (itemConfig.remove != null) {
                System.out.println("  Remove:");
                itemConfig.remove.forEach(a -> System.out.println("    " + a));
            }
        });
    }


    private static EntityAttribute attributeFromString(String string) {
        String[] s = string.split(":");
        return ForgeRegistries.ATTRIBUTES.getValue(Identifier.of(s[0], s[1]));
    }

    private static Identifier itemID(Item item) {
        return ForgeRegistries.ITEMS.getKey(item);
    }

    private static String[] parseEntry(String entry) {
        return entry.split(";");
    }

    @SubscribeEvent
    public static void onItemAttributeModifier(ItemAttributeModifierEvent event) {
        Multimap<EntityAttribute, EntityAttributeModifier> originalModifiers = event.getModifiers();
        List<Map<EntityAttribute, EntityAttributeModifier>> replacementModifiers = new ArrayList<>();

        // Turn the multimap into a list
        // Will be used later
        for (Map.Entry<EntityAttribute, EntityAttributeModifier> entry : originalModifiers.entries()) {
            Map<EntityAttribute, EntityAttributeModifier> singleMap = new HashMap<>();
            singleMap.put(entry.getKey(), entry.getValue());
            replacementModifiers.add(singleMap);
        }

        // Per item configuration
        String itemKey = itemID(event.getItemStack().getItem()).toString();
        if (REPLACEMENTS.items.containsKey(itemKey)) {
            AttributeConfig.ItemConfig config = REPLACEMENTS.items.get(itemKey);
            if (config.replace != null) {
                config.replace.forEach((configKey, configEntry) -> {
                    String[] key = parseEntry(configKey);
                    EntityAttribute targetAttribute = attributeFromString(key[0]);
                    if (originalModifiers.keySet().contains(targetAttribute)) {
                        ListIterator<Map<EntityAttribute, EntityAttributeModifier>> iterator = replacementModifiers.listIterator();
                        while (iterator.hasNext()) {
                            Map<EntityAttribute, EntityAttributeModifier> map = iterator.next();
                            EntityAttributeModifier modifier = map.get(targetAttribute);
                            if (modifier != null && modifier.getOperation().getId() == Integer.parseInt(key[1])) {
                                iterator.remove();

                                String[] configNew = parseEntry(configEntry);
                                EntityAttributeModifier newModifier = new EntityAttributeModifier(
                                        UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3"),
                                        modifier.getName(),
                                        modifier.getValue(),
                                        EntityAttributeModifier.Operation.fromId(Integer.parseInt(configNew[1])));
                                EntityAttribute newAttribute = attributeFromString(configNew[0]);

                                Map<EntityAttribute, EntityAttributeModifier> newMap = new HashMap<>();
                                newMap.put(newAttribute, newModifier);

                                iterator.add(newMap);
                            }
                        }
                    }
                });
            }
            if (config.remove != null) {
                config.remove.forEach(configEntry -> {
                    ListIterator<Map<EntityAttribute, EntityAttributeModifier>> iterator = replacementModifiers.listIterator();
                    EntityAttribute targetAttribute = attributeFromString(configEntry);
                    while (iterator.hasNext()) {
                        Map<EntityAttribute, EntityAttributeModifier> map = iterator.next();
                        for (EntityAttribute attr : map.keySet()) {
                            if (attr == targetAttribute) {
                                iterator.remove();
                            }
                        }
                    }
                });
            }
            if (config.add != null) {
                config.add.forEach(configEntry -> {
                    EquipmentSlot slot = event.getSlotType();
                    String[] entry = parseEntry(configEntry);
                    if (slot == EquipmentSlot.byName(entry[3].toLowerCase())) {
                        EntityAttributeModifier newModifier = new EntityAttributeModifier(
                                UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3"),
                                "ADJ Tweaks",
                                Double.parseDouble(entry[2]),
                                EntityAttributeModifier.Operation.fromId(Integer.parseInt(entry[1])));
                        EntityAttribute newAttribute = attributeFromString(entry[0]);

                        Map<EntityAttribute, EntityAttributeModifier> newMap = new HashMap<>();
                        newMap.put(newAttribute, newModifier);

                        replacementModifiers.add(newMap);
                    }
                });
            }
        }

        // Global configuration
        REPLACEMENTS.global.forEach((configKey, configEntry) -> {
            String[] key = parseEntry(configKey);
            EntityAttribute targetAttribute = attributeFromString(key[0]);

            ListIterator<Map<EntityAttribute, EntityAttributeModifier>> iterator = replacementModifiers.listIterator();
            while (iterator.hasNext()) {
                Map<EntityAttribute, EntityAttributeModifier> map = iterator.next();
                EntityAttributeModifier modifier = map.get(targetAttribute);
                if (modifier != null && modifier.getOperation().getId() == Integer.parseInt(key[1])) {
                    iterator.remove();

                    String[] configNew = parseEntry(configEntry);
                    EntityAttributeModifier newModifier = new EntityAttributeModifier(
                            UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3"),
                            modifier.getName(),
                            modifier.getValue(),
                            EntityAttributeModifier.Operation.fromId(Integer.parseInt(configNew[1])));
                    EntityAttribute newAttribute = attributeFromString(configNew[0]);

                    Map<EntityAttribute, EntityAttributeModifier> newMap = new HashMap<>();
                    newMap.put(newAttribute, newModifier);

                    iterator.add(newMap);
                }
            }
        });

        List<Map.Entry<EntityAttribute, EntityAttributeModifier>> toRemove = new ArrayList<>(originalModifiers.entries());
        for (Map.Entry<EntityAttribute, EntityAttributeModifier> entry : toRemove) {
            event.removeModifier(entry.getKey(), entry.getValue());
        }

        for (Map<EntityAttribute, EntityAttributeModifier> map : replacementModifiers) {
            for (Map.Entry<EntityAttribute, EntityAttributeModifier> entry : map.entrySet()) {
                event.addModifier(entry.getKey(), entry.getValue());
            }
        }
    }

    public static class AttributeConfig {
        public Map<String, String> global;
        public Map<String, ItemConfig> items;

        public static class ItemConfig {
            public Map<String, String> replace;
            public List<String> add;
            public List<String> remove;
        }
    }
}
