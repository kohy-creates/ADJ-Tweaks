package xyz.kohara.adjcore.client.renderer.entity.model;

import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.VillagerHeadModel;
import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.LivingEntity;
import xyz.kohara.adjcore.registry.entities.VillagerEntity;

public class VillagerEntityModel<T extends VillagerEntity> extends PlayerModel<T> implements VillagerEntityHeadModel, VillagerHeadModel {

	private final ModelPart villagerHat;
	private final ModelPart villagerHatRim;
	private final ModelPart villagerJacket;

	public VillagerEntityModel(ModelPart root, boolean slim) {
		super(root, slim);

		this.villagerHat = root.getChild("head").getChild("villager_hat");
		this.villagerHatRim = this.villagerHat.getChild("villager_hat_rim");

		this.villagerJacket = root.getChild("body").getChild("villager_jacket");
	}

	public static MeshDefinition createMesh(CubeDeformation deformation, boolean slim) {
		MeshDefinition mesh = PlayerModel.createMesh(deformation, slim);
		PartDefinition root = mesh.getRoot();

		root.addOrReplaceChild(
				"left_arm", CubeListBuilder.create().texOffs(40, 16).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, deformation), PartPose.offset(5.0F, 2.0F, 0.0F)
		);

		// head
		PartDefinition head = root.getChild("head");

		PartDefinition hat = head.addOrReplaceChild(
				"villager_hat",
				CubeListBuilder.create()
						.texOffs(32, 0)
						.addBox(
								-4.0F, -9.0F, -4.0F,
								8.0F, 10.0F, 8.0F,
								new CubeDeformation(0.51F)
						),
				PartPose.ZERO
		);

		hat.addOrReplaceChild(
				"villager_hat_rim",
				CubeListBuilder.create()
						.texOffs(30, 47)
						.addBox(
								-8.0F, -8.0F, -6.0F,
								16.0F, 16.0F, 1.0F
						),
				PartPose.rotation((float) (-Math.PI / 2), 0.0F, 0.0F)
		);

		// villager jacket overlay
		PartDefinition body = root.getChild("body");

		body.addOrReplaceChild(
				"villager_jacket",
				CubeListBuilder.create()
						.texOffs(0, 32)
						.addBox(
								-4.0F, 0.0F, -3.0F,
								8.0F, 20.0F, 6.0F,
								new CubeDeformation(0.5F)
						),
				PartPose.ZERO
		);

		return mesh;
	}

	@Override
	public void hatVisible(boolean visible) {
		this.villagerHat.visible = visible;
		this.villagerHatRim.visible = visible;
	}
}