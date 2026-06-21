package xyz.kohara.adjcore.client.renderer.entity;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import xyz.kohara.adjcore.ADJCore;
import xyz.kohara.adjcore.client.renderer.entity.layers.LayerLocations;
import xyz.kohara.adjcore.client.renderer.entity.layers.VillagerEntityProfessionLayer;
import xyz.kohara.adjcore.client.renderer.entity.model.VillagerEntityModel;
import xyz.kohara.adjcore.registry.entities.VillagerEntity;

public class VillagerEntityRenderer extends MobRenderer<VillagerEntity, VillagerEntityModel<VillagerEntity>> {
	public VillagerEntityRenderer(EntityRendererProvider.Context arg) {
		super(arg, new VillagerEntityModel<>(arg.bakeLayer(LayerLocations.VILLAGER), false), 0.5F);
		this.addLayer(new CustomHeadLayer<>(this, arg.getModelSet(), arg.getItemInHandRenderer()));
		this.addLayer(new VillagerEntityProfessionLayer<>(this, arg.getResourceManager(), "villager"));
	}

	@Override
	public @NotNull ResourceLocation getTextureLocation(@NotNull VillagerEntity entity) {
		return ADJCore.of("textures/entity/villager/" + entity.gender + "_" + entity.variant + ".png");
	}
}
