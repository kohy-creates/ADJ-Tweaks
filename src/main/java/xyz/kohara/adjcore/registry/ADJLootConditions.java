package xyz.kohara.adjcore.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import xyz.kohara.adjcore.ADJCore;
import xyz.kohara.adjcore.registry.lootconditions.IsChapter;
import xyz.kohara.adjcore.registry.lootconditions.IsHardcore;
import xyz.kohara.adjcore.registry.placementmodifiertypes.IsHardcorePlacement;

public class ADJLootConditions {

	public static final DeferredRegister<LootItemConditionType> LOOT_CONDITIONS = DeferredRegister.create(Registries.LOOT_CONDITION_TYPE, ADJCore.MOD_ID);

	public static final RegistryObject<LootItemConditionType> IS_HARDCORE =
			LOOT_CONDITIONS.register("is_hardcore", () -> new LootItemConditionType(new IsHardcore.Serializer()));

	public static final RegistryObject<LootItemConditionType> IS_CHAPTER =
			LOOT_CONDITIONS.register("is_chapter", () -> new LootItemConditionType(new IsChapter.Serializer()));

	public static void register(IEventBus eventBus) {
		LOOT_CONDITIONS.register(eventBus);
	}
}
