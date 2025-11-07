package xyz.kohara.adjcore.attributes;

import dev.shadowsoffire.attributeslib.impl.PercentBasedAttribute;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import xyz.kohara.adjcore.ADJCore;

public class ModAttributes {

    private static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, ADJCore.MOD_ID);

    public static final RegistryObject<Attribute> DAMAGE_REDUCTION = register(
            "generic.damage_reduction", new PercentBasedAttribute("attribute.name.generic.damage_reduction", 0.0, -1.0, 1.0).setSyncable(true)
    );

    public static final RegistryObject<Attribute> PROJECTILE_DAMAGE_REDUCTION = register(
            "generic.projectile_damage_reduction", new PercentBasedAttribute("attribute.name.generic.projectile_damage_reduction", 0.0, 0.0, 1.0).setSyncable(true)
    );

    public static final RegistryObject<Attribute> MANA_COST_REDUCTION = register(
            "player.mana_cost_reduction", new PercentBasedAttribute("attribute.name.player.mana_cost_reduction", 0.0, -1.0, 1.0).setSyncable(true)
    );

    private static RegistryObject<Attribute> register(String id, Attribute attribute) {
        return ATTRIBUTES.register(id, () -> attribute);
    }

    public static void register(IEventBus eventBus) {
        ATTRIBUTES.register(eventBus);
    }
}
