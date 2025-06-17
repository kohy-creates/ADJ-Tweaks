package xyz.kohara.adjcore.effects.editor;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Attr;
import xyz.kohara.adjcore.attributes.AttributeReplace;
import xyz.kohara.adjcore.mixins.effect.MobEffectAccessor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EffectsEditor {

    private static Map<ResourceLocation, EffectEditorConfig.ConfigData> config;

    static {
        loadConfig();
    }

    public static void loadConfig() {
        config = EffectEditorConfig.parseConfig();
    }

    public static void edit() {
        for (MobEffect effect : BuiltInRegistries.MOB_EFFECT) {
            String[] name = effect.getDescriptionId().split("\\.");

            ResourceLocation id = ResourceLocation.fromNamespaceAndPath(name[1], name[2]);

            if (config.containsKey(id)) {
                EffectEditorConfig.ConfigData data = config.get(id);

                MobEffectAccessor accessor = (MobEffectAccessor) effect;
                Map<Attribute, AttributeModifier> oldAttr = accessor.getAttributeModifiers();
                System.out.println(oldAttr);
                Map<Attribute, AttributeModifier> attributeModifierMap;
                if (!data.replace && !oldAttr.isEmpty()) {
                    attributeModifierMap = oldAttr;
                } else {
                    attributeModifierMap = new HashMap<>();
                }
                System.out.println(attributeModifierMap);
                data.attributes.forEach(s -> {
                    String[] entry = s.split(";");
                    Attribute attribute = ForgeRegistries.ATTRIBUTES.getValue(ResourceLocation.parse(entry[0]));

                    AttributeModifier modifier = new AttributeModifier(
                            UUID.randomUUID(),
                            effect.getDescriptionId(),
                            Double.parseDouble(entry[1]),
                            AttributeModifier.Operation.fromValue(Integer.parseInt(entry[2]))
                    );
                    attributeModifierMap.put(attribute, modifier);
                });
                System.out.println(attributeModifierMap);
                accessor.setAttributeModifiers(attributeModifierMap);
            }
        }
    }

}
