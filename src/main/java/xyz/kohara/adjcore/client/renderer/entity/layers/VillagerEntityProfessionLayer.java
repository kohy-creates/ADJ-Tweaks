package xyz.kohara.adjcore.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.io.IOException;
import java.util.Optional;
import java.util.function.UnaryOperator;

import net.minecraft.Util;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.VillagerHeadModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.layers.VillagerProfessionLayer;
import net.minecraft.client.resources.metadata.animation.VillagerMetaDataSection;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerDataHolder;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import xyz.kohara.adjcore.client.renderer.entity.VillagerEntityRenderer;

@OnlyIn(Dist.CLIENT)
public class VillagerEntityProfessionLayer<T extends LivingEntity & VillagerDataHolder, M extends EntityModel<T> & VillagerHeadModel> extends RenderLayer<T, M> {

	private static final Int2ObjectMap<ResourceLocation> LEVEL_LOCATIONS = Util.make(new Int2ObjectOpenHashMap<>(), int2ObjectOpenHashMap -> {
		int2ObjectOpenHashMap.put(1, ResourceLocation.withDefaultNamespace("stone"));
		int2ObjectOpenHashMap.put(2, ResourceLocation.withDefaultNamespace("iron"));
		int2ObjectOpenHashMap.put(3, ResourceLocation.withDefaultNamespace("gold"));
		int2ObjectOpenHashMap.put(4, ResourceLocation.withDefaultNamespace("emerald"));
		int2ObjectOpenHashMap.put(5, ResourceLocation.withDefaultNamespace("diamond"));
	});
	private final Object2ObjectMap<VillagerType, VillagerMetaDataSection.Hat> typeHatCache = new Object2ObjectOpenHashMap<>();
	private final Object2ObjectMap<VillagerProfession, VillagerMetaDataSection.Hat> professionHatCache = new Object2ObjectOpenHashMap<>();
	private final ResourceManager resourceManager;
	private final String path;

	public VillagerEntityProfessionLayer(RenderLayerParent<T, M> renderer, ResourceManager resourceManager, String path) {
		super(renderer);
		this.resourceManager = resourceManager;
		this.path = path;
	}

	public void render(
			@NotNull PoseStack matrixStack,
			@NotNull MultiBufferSource buffer,
			int packedLight,
			T livingEntity,
			float limbSwing,
			float limbSwingAmount,
			float partialTicks,
			float ageInTicks,
			float netHeadYaw,
			float headPitch
	) {
		if (!livingEntity.isInvisible()) {
			VillagerData villagerData = livingEntity.getVillagerData();
			VillagerType villagerType = villagerData.getType();
			VillagerProfession villagerProfession = villagerData.getProfession();
			VillagerMetaDataSection.Hat hat = this.getHatData(this.typeHatCache, "type", BuiltInRegistries.VILLAGER_TYPE, villagerType);
			VillagerMetaDataSection.Hat hat2 = this.getHatData(this.professionHatCache, "profession", BuiltInRegistries.VILLAGER_PROFESSION, villagerProfession);
			M entityModel = this.getParentModel();
			entityModel.hatVisible(hat2 == VillagerMetaDataSection.Hat.NONE || hat2 == VillagerMetaDataSection.Hat.PARTIAL && hat != VillagerMetaDataSection.Hat.FULL);
			ResourceLocation resourceLocation = this.getResourceLocation("type", BuiltInRegistries.VILLAGER_TYPE.getKey(villagerType));
			renderColoredCutoutModel(entityModel, resourceLocation, matrixStack, buffer, packedLight, livingEntity, 1.0F, 1.0F, 1.0F);
			entityModel.hatVisible(true);
			if (villagerProfession != VillagerProfession.NONE && !livingEntity.isBaby()) {
				ResourceLocation resourceLocation2 = this.getResourceLocation("profession", BuiltInRegistries.VILLAGER_PROFESSION.getKey(villagerProfession));
				renderColoredCutoutModel(entityModel, resourceLocation2, matrixStack, buffer, packedLight, livingEntity, 1.0F, 1.0F, 1.0F);
				if (villagerProfession != VillagerProfession.NITWIT) {
					ResourceLocation resourceLocation3 = this.getResourceLocation(
							"profession_level", LEVEL_LOCATIONS.get(Mth.clamp(villagerData.getLevel(), 1, LEVEL_LOCATIONS.size()))
					);
					renderColoredCutoutModel(entityModel, resourceLocation3, matrixStack, buffer, packedLight, livingEntity, 1.0F, 1.0F, 1.0F);
				}
			}
		}
	}

	private ResourceLocation getResourceLocation(String string, ResourceLocation arg) {
		return arg.withPath(string2 -> "textures/entity/" + this.path + "/" + string + "/" + string2 + ".png");
	}

	public <K> VillagerMetaDataSection.Hat getHatData(
			Object2ObjectMap<K, VillagerMetaDataSection.Hat> object2ObjectMap, String string, DefaultedRegistry<K> villagerTypeRegistry, K object
	) {
		return object2ObjectMap.computeIfAbsent(
				object,
				object2 -> this.resourceManager
						.getResource(this.getResourceLocation(string, villagerTypeRegistry.getKey(object)))
						.flatMap(argx -> {
							try {
								return argx.metadata().getSection(VillagerMetaDataSection.SERIALIZER).map(VillagerMetaDataSection::getHat);
							} catch (IOException var2) {
								return Optional.empty();
							}
						})
						.orElse(VillagerMetaDataSection.Hat.NONE)
		);
	}
}
