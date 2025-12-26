package xyz.kohara.adjcore.registry;

import dev.shadowsoffire.attributeslib.impl.PercentBasedAttribute;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import xyz.kohara.adjcore.ADJCore;
import xyz.kohara.adjcore.misc.LangGenerator;

public class ADJAttributes {

    private static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, ADJCore.MOD_ID);

    public static final RegistryObject<Attribute> DAMAGE_REDUCTION = register(
            new PercentBasedAttribute(id("generic", "damage_reduction"), 0.0, -1.0, 1.0).setSyncable(true),
            "Damage Reduction",
            "Reduces incoming damage. Stacks with other forms of damage reduction."
    );

    public static final RegistryObject<Attribute> PROJECTILE_DAMAGE_REDUCTION = register(
            new PercentBasedAttribute(id("generic", "projectile_damage_reduction"), 0.0, 0.0, 1.0).setSyncable(true),
            "Projectile Damage Reduction",
            "Reduces incoming damage from projectiles. Stacks with other forms of damage reduction."
    );

    public static final RegistryObject<Attribute> MANA_COST_REDUCTION = register(
            new PercentBasedAttribute(id("player", "mana_cost_reduction"), 0.0, -1.0, 1.0).setSyncable(true),
            "Mana Cost Reduction",
            "Reduces casting cost of spells."
    );

    public static final RegistryObject<Attribute> SAFE_FALL_DISTANCE = register(
            new RangedAttribute(id("generic", "safe_fall_distance"), 0.0, 0.0, 2048.0).setSyncable(true),
            "Safe Fall Distance",
            "Distance you can fall before fall damage starts accumulating."
    );

    public static final RegistryObject<Attribute> EXTRA_ORE_DROPS = register(
            new RangedAttribute(id("player", "extra_fortune_level"), 0.0, 0.0, 2048.0).setSyncable(true),
            "Extra Ore Drops",
            "Every points increases the odds to get extra drops from ores (similar to Fortune enchantment)."
    );

    public static final RegistryObject<Attribute> HEALTH_REGEN = register(
            new RangedAttribute(id("generic", "health_regeneration"), 0.0, -100.0, 100.0).setSyncable(true),
            "Health Regeneration",
            "Extra health regeneration in points per second. This is ticked separately outside of natural health regeneration."
    );

    private static RegistryObject<Attribute> register(Attribute attribute, String name, String description) {
        String descriptionID = attribute.getDescriptionId();
        LangGenerator.addAttributeTranslation(descriptionID, name, description);
        return ATTRIBUTES.register(descriptionID.replace("attribute.name.", ""), () -> attribute);
    }

    private static String id(String category, String name) {
        return "attribute.name." + category + "." + name;
    }

    public static void register(IEventBus eventBus) {
        ATTRIBUTES.register(eventBus);
    }
}
