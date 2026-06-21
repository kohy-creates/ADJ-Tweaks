package xyz.kohara.adjcore.mixins.compat.botania.windowbox;

import amaryllis.window_box.tree.Alfthorne;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import vazkii.botania.client.fx.SparkleParticleData;
import vazkii.botania.client.fx.WispParticleData;
import vazkii.botania.common.block.block_entity.BotaniaBlockEntity;
import vazkii.botania.xplat.BotaniaConfig;

import java.util.Random;

import static vazkii.botania.common.block.PylonBlock.Variant.NATURA;

@Mixin(value = Alfthorne.SaplingBlockEntity.class, remap = false)
public class AlfthorneTreeMixin extends BotaniaBlockEntity {

	public AlfthorneTreeMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	/**
	 * @author me
	 * @reason fix multiplayer Neruina crash
	 */
	@Overwrite(remap = false)
	protected void lightPylon(BlockPos pos) {
		if (level != null && level.isClientSide()) {
			// Simulate pylon activated particles
			Vec3 center = new Vec3(pos.getX() + 0.5, pos.getY() + 0.75 + (Math.random() - 0.5 * 0.25), pos.getZ() + 0.5);

			if (BotaniaConfig.client().elfPortalParticlesEnabled()) {
				double time = level.getGameTime();
				time += new Random(worldPosition.hashCode()).nextInt(1000);
				time /= 5;

				double r = 0.75 + Math.random() * 0.05;
				double x = worldPosition.getX() + 0.5 + Math.cos(time) * r;
				double z = worldPosition.getZ() + 0.5 + Math.sin(time) * r;

				Vec3 ourCoords = new Vec3(x, worldPosition.getY() + 0.25, z);
				center = center.subtract(0, 0.5, 0);
				Vec3 movementVector = center.subtract(ourCoords).normalize().scale(0.2);

				WispParticleData data = WispParticleData.wisp(0.25F + (float) Math.random() * 0.1F, (float) Math.random() * 0.25F, 0.75F + (float) Math.random() * 0.25F, (float) Math.random() * 0.25F, 1);
				level.addParticle(data, x, worldPosition.getY() + 0.25, z, 0, -(-0.075F - (float) Math.random() * 0.015F), 0);
				if (level.random.nextInt(3) == 0) {
					WispParticleData data1 = WispParticleData.wisp(0.25F + (float) Math.random() * 0.1F, (float) Math.random() * 0.25F, 0.75F + (float) Math.random() * 0.25F, (float) Math.random() * 0.25F);
					level.addParticle(data1, x, worldPosition.getY() + 0.25, z, (float) movementVector.x, (float) movementVector.y, (float) movementVector.z);
				}
			}

			if (level.random.nextBoolean()) {
				SparkleParticleData data = SparkleParticleData.sparkle((float) Math.random(), NATURA.r, NATURA.g, NATURA.b, 2);
				level.addParticle(data, worldPosition.getX() + Math.random(), worldPosition.getY() + Math.random() * 1.5, worldPosition.getZ() + Math.random(), 0, 0, 0);
			}
		}
	}
}
