package xyz.kohara.adjcore.registry.entities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class VillagerEntity extends Villager {

	public int variant;
	public int gender;

	private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(VillagerEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> GENDER = SynchedEntityData.defineId(VillagerEntity.class, EntityDataSerializers.INT);

	public VillagerEntity(EntityType<? extends Villager> entityType, Level level) {
		super(entityType, level);
		this.variant = 1;
		this.gender = 0;
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(VARIANT, this.variant);
		this.entityData.define(GENDER, this.gender);
	}

	@Override
	public void addAdditionalSaveData(@NotNull CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putInt("variant", this.variant);
		compound.putInt("gender", this.gender);
	}

	@Override
	public void readAdditionalSaveData(@NotNull CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		this.variant = compound.getInt("variant");
		this.gender = compound.getInt("gender");
	}

	public static AttributeSupplier.@NotNull Builder createAttributes() {
		return Villager.createMobAttributes()
				.add(Attributes.MAX_HEALTH, 20.0D)
				.add(Attributes.MOVEMENT_SPEED, 0.5D)
				.add(Attributes.FOLLOW_RANGE, 16.0D);
	}
}
