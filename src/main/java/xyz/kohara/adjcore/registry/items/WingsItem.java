package xyz.kohara.adjcore.registry.items;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.joml.Quaternionf;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;
import top.theillusivec4.curios.api.type.capability.ICurioItem;
import xyz.kohara.adjcore.registry.ADJAttributes;

import java.util.UUID;

public class WingsItem extends Item implements ICurioItem {

	public final int flightTime;
	public final float horizontalAccelerationMultiplier;
	public final float verticalMultiplier;
	public final float maxHorizontalSpeed;

	public final ResourceLocation model;

	public static final float GLIDE_DESCEND_SPEED = -0.12f;
	public static final float INITIAL_JUMP_IMPULSE = 0.09f;
	public static final float SUSTAIN_THRUST = 0.08f;

	public WingsItem(
			Properties properties,
			int flightTime,
			float horizontalAccelerationMultiplier,
			float verticalMultiplier,
			float maxHorizontalSpeed,
			ResourceLocation model
	) {
		super(properties.stacksTo(1));
		this.flightTime = flightTime;
		this.horizontalAccelerationMultiplier = horizontalAccelerationMultiplier;
		this.verticalMultiplier = verticalMultiplier;
		this.maxHorizontalSpeed = maxHorizontalSpeed;
		this.model = model;
	}

	@Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext slotContext, UUID identifier, ItemStack stack) {
		Multimap<Attribute, AttributeModifier> modifiers = ArrayListMultimap.create();
		modifiers.put(
				ADJAttributes.FLIGHT_TIME.get(),
				new AttributeModifier(identifier, "Wings flight time", this.flightTime, AttributeModifier.Operation.ADDITION)
		);
		return modifiers;
	}

	@Override
	public boolean canRightClickEquip(ItemStack stack) {
		return true;
	}

	@Override
	public boolean showAttributesTooltip(String identifier, ItemStack stack) {
		return false;
	}

	public static boolean hasEquipped(Player player) {
		return CuriosApi.getCuriosHelper().findFirstCurio(player, stack -> stack.getItem() instanceof WingsItem).isPresent();
	}

	@SubscribeEvent
	public static void onPlayerFall(LivingFallEvent event) {
		if (event.getEntity() instanceof Player player && hasEquipped(player)) {
			event.setDistance(0f);
			event.setDamageMultiplier(0f);
		}
	}

//	@SubscribeEvent
//	public static void onLivingKnockback(LivingKnockBackEvent event) {
//		var entity = event.getEntity();
//		if (entity instanceof Player player) {
//			if (hasEquipped(player) && isFlying(player)) event.setCanceled(true);
//		}
//	}

	public static boolean isFlying(Player player) {
		CompoundTag data = player.getPersistentData();
		return data.getBoolean("IsWingFlying");
	}

	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (event.phase != TickEvent.Phase.END) return;

		Player player = event.player;

		var curio = CuriosApi.getCuriosHelper().findFirstCurio(player, stack -> stack.getItem() instanceof WingsItem);
		if (curio.isEmpty()) return;

		if (curio.get().stack().getItem() instanceof WingsItem wings) {
			CompoundTag data = player.getPersistentData();

			if (player.onGround()) {
				data.putInt("WingFlightTicks", (int) player.getAttributeValue(ADJAttributes.FLIGHT_TIME.get()));
				data.putBoolean("IsWingGliding", false);
				data.putBoolean("IsWingFlying", false);
				data.putBoolean("WasJumpingBefore", false);
				return;
			}

			int flightTicks = data.getInt("WingFlightTicks");
			boolean isJumping = player.jumping;
			boolean wasFlying = data.getBoolean("IsWingFlying");

			boolean hitCeiling = player.verticalCollision && player.getDeltaMovement().y <= 0.0D;
			boolean isInitialActivation = (isJumping && !wasFlying) || hitCeiling;

			if (isJumping && !player.getAbilities().flying) {
				Vec3 motion = player.getDeltaMovement();

				if (flightTicks > 0) {
					data.putInt("WingFlightTicks", flightTicks - 1);
					data.putBoolean("IsWingFlying", true);
					data.putBoolean("IsWingGliding", false);

					player.resetFallDistance();

					float verticalMult = getVerticalSpeed(player, wings);

					double newY = motion.y;

					if (isInitialActivation) {
						double cancelFallDrag = (motion.y < 0) ? Math.abs(motion.y) * 0.5D : 0.0D;
						newY = Math.max(motion.y, 0.05D) + (INITIAL_JUMP_IMPULSE * verticalMult * 2.5D) + cancelFallDrag;
					} else {
						newY += (0.08D + (SUSTAIN_THRUST * verticalMult * 0.6D));
					}

					double maxAscentSpeed = 0.45D * verticalMult;
					if (newY > maxAscentSpeed) {
						newY = maxAscentSpeed;
					}

					double newX = motion.x;
					double newZ = motion.z;

					if (player.level().isClientSide() && player instanceof LocalPlayer localPlayer) {
						float forward = localPlayer.input.forwardImpulse;
						float left = localPlayer.input.leftImpulse;

						if (forward != 0 || left != 0) {
							float yaw = player.getYRot();
							double rad = Math.toRadians(yaw);

							double dirX = -(forward * Math.sin(rad) - left * Math.cos(rad));
							double dirZ = (forward * Math.cos(rad) + left * Math.sin(rad));

							Vec3 moveDir = new Vec3(dirX, 0, dirZ).normalize();
							double accel = 0.04D * wings.horizontalAccelerationMultiplier;

							newX += moveDir.x * accel;
							newZ += moveDir.z * accel;

							double baseGroundSpeed = player.getAttributeValue(Attributes.MOVEMENT_SPEED);
							double maxSpeed = baseGroundSpeed * wings.maxHorizontalSpeed * 1.5D;

							Vec3 horiz = new Vec3(newX, 0, newZ);
							if (horiz.length() > maxSpeed) {
								horiz = horiz.normalize().scale(maxSpeed);
								newX = horiz.x;
								newZ = horiz.z;
							}
						}
					}

					player.setDeltaMovement(newX, newY, newZ);

				} else {
					data.putBoolean("IsWingFlying", false);
					data.putBoolean("IsWingGliding", true);

					player.resetFallDistance();

					double clampedGlideY = Math.max(motion.y, GLIDE_DESCEND_SPEED);
					player.setDeltaMovement(motion.x, clampedGlideY, motion.z);
				}
				player.hasImpulse = true;
			} else {
				data.putBoolean("IsWingFlying", false);
				data.putBoolean("IsWingGliding", false);
			}

			data.putBoolean("WasJumpingBefore", isJumping);
		}
	}

	public static float getVerticalSpeed(Player player, WingsItem wingsItem) {
		return (wingsItem.verticalMultiplier + wingsItem.verticalMultiplier * player.getJumpBoostPower()) * player.getBlockJumpFactor();
	}

	// Credits for this part: Botania mod
	// Please don't sue me I am begging you
	public static class WingsCurioRenderer implements ICurioRenderer {

		@Override
		public <T extends LivingEntity, M extends EntityModel<T>> void render(
				ItemStack stack,
				SlotContext slotContext,
				PoseStack poseStack,
				RenderLayerParent<T, M> renderLayerParent,
				MultiBufferSource renderTypeBuffer,
				int light,
				float limbSwing,
				float limbSwingAmount,
				float partialTicks,
				float ageInTicks,
				float netHeadYaw,
				float headPitch
		) {
			poseStack.pushPose();

			if (stack.getItem() instanceof WingsItem wings) {

				BakedModel model = Minecraft.getInstance().getModelManager().getModel(wings.model);

				var entity = slotContext.entity();
				var bipedModel = (HumanoidModel<LivingEntity>) renderLayerParent.getModel();

//				ICurioRenderer.followBodyRotations(slotContext.entity(), bipedModel);
//				ICurioRenderer.translateIfSneaking(poseStack, slotContext.entity());

				boolean isMidAir = !entity.onGround() && !entity.isInWater() && !entity.isPassenger();
				var flap = getFlap(partialTicks, entity, isMidAir);

				renderBasic(bipedModel, model, stack, poseStack, renderTypeBuffer, light, flap);

				poseStack.popPose();
			}
		}

		private float prevFlap = 15.0F;
		private float getFlap(float partialTicks, LivingEntity entity, boolean isMidAir) {
			Vec3 motion = entity.getDeltaMovement();

			boolean isFlying = isMidAir && motion.y() > GLIDE_DESCEND_SPEED + 0.05D;
			boolean isGliding = isMidAir && !isFlying && motion.y() <= GLIDE_DESCEND_SPEED + 0.05D;

			float targetFlap;

			if (isGliding) {
				targetFlap = 30.0F;
			} else if (isFlying) {
				targetFlap = 20.0F + (float) ((Math.sin((double) (entity.tickCount + partialTicks) * 0.4F) + 0.5F) * 35.0F);
			} else {
				targetFlap = 45.0F + (float) ((Math.sin((double) (entity.tickCount + partialTicks) * 0.1F) + 0.5F) * 5.0F);
			}

			// Lerp smoothly toward target value to prevent snapping
			float smoothedFlap = net.minecraft.util.Mth.lerp(0.2F, prevFlap, targetFlap);
			prevFlap = smoothedFlap;

			return smoothedFlap;
		}

		private static void renderBasic(HumanoidModel<?> bipedModel, BakedModel model, ItemStack stack, PoseStack ms, MultiBufferSource buffers, int light, float flap) {
			ms.pushPose();

			// attach to body
			bipedModel.body.translateAndRotate(ms);

			// position on body
			ms.translate(0, 0.5, 0.2);

			for (int i = 0; i < 2; i++) {
				ms.pushPose();
				ms.mulPose(rotateY(i == 0 ? flap : 180 - flap));
				// move so flapping about the edge instead of center of texture
				ms.translate(-1, 0, 0);

				// rotate since the textures are stored rotated
				ms.mulPose(rotateZ(-60));
				ms.scale(1.5F, -1.5F, -1.5F);
				Minecraft.getInstance().getItemRenderer().render(stack, ItemDisplayContext.NONE, false, ms, buffers, light, OverlayTexture.NO_OVERLAY, model);
				ms.popPose();
			}

			ms.popPose();
		}

		private static float toRadians(float degrees) {
			return (float) (degrees / 180F * Math.PI);
		}

		private static Quaternionf rotateX(float degrees) {
			return new Quaternionf().rotateX(toRadians(degrees));
		}

		private static Quaternionf rotateY(float degrees) {
			return new Quaternionf().rotateY(toRadians(degrees));
		}

		private static Quaternionf rotateZ(float degrees) {
			return new Quaternionf().rotateZ(toRadians(degrees));
		}
	}

}