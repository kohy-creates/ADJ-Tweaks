package xyz.kohara.adjtweaks.attributes;

import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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
import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = ADJTweaks.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AttributeReplace {

    private static final String CONFIG_FILE = "config/adjtweaks/attribute_replace.json";
    private static Map<String, String> REPLACEMENTS = new HashMap<>();

    static {
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
                REPLACEMENTS = new Gson().fromJson(reader, new TypeToken<Map<String, String>>() {
                }.getType());
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load attribute replacement config", e);
        }
    }

    @SubscribeEvent
    public static void onItemAttributeModifier(ItemAttributeModifierEvent event) {
        Multimap<EntityAttribute, EntityAttributeModifier> modifiers = event.getModifiers();
        for (Map.Entry<EntityAttribute, EntityAttributeModifier> entry : modifiers.entries()) {
            Identifier id1 = ForgeRegistries.ATTRIBUTES.getKey(entry.getKey());
            if (REPLACEMENTS.containsKey(id1.toString())) {

                String s[] = REPLACEMENTS.get(id1.toString()).split(":");
                Identifier id2 = Identifier.of(s[0], s[1]);

                EntityAttribute replacement = ForgeRegistries.ATTRIBUTES.getValue(id2);

                EntityAttributeModifier modifier = entry.getValue();

                event.removeModifier(entry.getKey(), modifier);
                event.addModifier(replacement, modifier);
            }
        }
    }
}
