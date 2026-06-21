package xyz.kohara.adjcore.registry;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.npc.Villager;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import xyz.kohara.adjcore.ADJCore;
import xyz.kohara.adjcore.misc.LangGenerator;
import xyz.kohara.adjcore.registry.entities.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ADJEntities {

	public static final List<Object> COLLECTIBLES = new ArrayList<>();

	private static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
			DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, ADJCore.MOD_ID);

	public static final RegistryObject<EntityType<ManaStarEntity>> MANA_STAR = registerCollectible(
			"mana_star",
			"Mana Star",
			ManaStarEntity::new
	);

	public static final RegistryObject<EntityType<HeartEntity>> HEART = registerCollectible(
			"heart",
			"Heart",
			HeartEntity::new
	);

	public static final RegistryObject<EntityType<TerraSlashEntity>> TERRA_SLASH = register(
			"terra_slash",
			"Terra Slash",
			() -> EntityType.Builder.of(TerraSlashEntity::new, MobCategory.MISC)
					.fireImmune()
					.sized(0.5f, 0.5f)
					.updateInterval(20)
					.build("terra_slash")
	);

	public static final RegistryObject<EntityType<VillagerEntity>> VILLAGER = register(
			"villager",
			"Villager",
			() -> EntityType.Builder.of(VillagerEntity::new, MobCategory.CREATURE)
					.sized(0.6F, 1.95F)
					.clientTrackingRange(10)
					.build("villager")
	);

	private static <T extends CollectibleEntity> RegistryObject<EntityType<T>> registerCollectible(String id, String name, EntityType.EntityFactory<T> factory) {
		var entityType = register(id, name, () -> EntityType.Builder.of(factory, MobCategory.MISC)
				.sized(0.25F, 0.25F)
				.clientTrackingRange(6)
				.updateInterval(20)
				.build(id));
		COLLECTIBLES.add(entityType);
		return entityType;
	}

	private static <T extends Entity> RegistryObject<EntityType<T>> register(String id, String name, Supplier<EntityType<T>> factory) {
		LangGenerator.addItemTranslation(id, name);
		return ENTITY_TYPES.register(id, factory);
	}

	public static void register(IEventBus bus) {
		ENTITY_TYPES.register(bus);
	}

}
