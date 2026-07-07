package xyz.kohara.adjcore.mixins.entity;

import com.hollingsworth.arsnouveau.api.event.SpellCostCalcEvent;
import com.hollingsworth.arsnouveau.api.mana.IManaCap;
import com.hollingsworth.arsnouveau.api.util.ManaUtil;
import com.hollingsworth.arsnouveau.common.capability.ManaCap;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.NotEnoughManaPacket;
import com.hollingsworth.arsnouveau.common.network.PacketUpdateMana;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.setup.registry.CapabilityRegistry;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.level.NoteBlockEvent;
import net.minecraftforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.kohara.adjcore.Config;
import xyz.kohara.adjcore.compat.ArsManaShenanigans;
import xyz.kohara.adjcore.misc.ParticleTextIndicators;

import java.util.List;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity implements ArsManaShenanigans {

	protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
		super(entityType, level);
	}

	@ModifyExpressionValue(
			method = "attack",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/player/Player;onClimbable()Z"
			)
	)
	private boolean adjUtils$canCrit(boolean isOnClimbable) {
		return Config.Combat.disableCrits || isOnClimbable;
	}

	@ModifyExpressionValue(
			method = "attack",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/Level;getEntitiesOfClass(Ljava/lang/Class;Lnet/minecraft/world/phys/AABB;)Ljava/util/List;"
			)
	)
	private List<LivingEntity> adjUtils$modifyListOfSweepAttacks(List<LivingEntity> listOfSweepAttacks) {
		return Config.Combat.disableSweepAttacks ? List.of() : listOfSweepAttacks;
	}

	@Redirect(
			method = "drop(Lnet/minecraft/world/item/ItemStack;ZZ)Lnet/minecraft/world/entity/item/ItemEntity;",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/item/ItemEntity;setDeltaMovement(DDD)V",
					ordinal = 0
			)
	)
	private void changeDropVelocity(
			ItemEntity instance,
			double x, double y, double z,
			@Local(name = "itementity") ItemEntity itementity
	) {
		float f = this.random.nextFloat() * 0.15F;
		float f1 = this.random.nextFloat() * (float) (Math.PI * 2);
		itementity.setDeltaMovement(-Mth.sin(f1) * f, 0.2F, Mth.cos(f1) * f);
	}

	@Inject(
			method = "drop(Lnet/minecraft/world/item/ItemStack;ZZ)Lnet/minecraft/world/entity/item/ItemEntity;",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/item/ItemEntity;setDeltaMovement(DDD)V", ordinal = 0)
	)
	private void makeDespawnLonger(
			ItemStack droppedItem,
			boolean dropAround,
			boolean includeThrowerName,
			CallbackInfoReturnable<ItemEntity> cir,
			@Local(name = "itementity") ItemEntity itementity
	) {
		itementity.lifespan *= 3;
	}

	@Unique
	public int adjcore$manaRegenDelay;

	@Unique
	public int adjcore$manaRegenTimer;

	@Override
	public int adjcore$getManaRegenDelay() {
		return adjcore$manaRegenDelay;
	}

	@Override
	public void adjcore$setManaRegenDelay(int cooldown) {
		adjcore$manaRegenDelay = cooldown;
	}

	@Override
	public void adjcore$increaseManaRegenCounter(int amount) {
		adjcore$manaRegenTimer += amount;
	}

	@Override
	public int adjcore$getManaRegenCounter() {
		return adjcore$manaRegenTimer;
	}

	@Inject(
			method = "tick",
			at = @At("HEAD")
	)
	private void onTick(CallbackInfo ci) {
		Player player = (Player) (Object) this;

		if (player.level().isClientSide()) return;

		if (adjcore$manaRegenDelay > 0) {
			adjcore$manaRegenDelay--;
		}
	}

	@Override
	public void adjcore$restoreMana(int amount) {
		final Player player = (Player) (Object) this;
		CapabilityRegistry.getMana(player).ifPresent(mana -> {

			mana.addMana(amount);

			Networking.INSTANCE.send(
					PacketDistributor.PLAYER.with(() -> (ServerPlayer) player),
					new PacketUpdateMana(
							mana.getCurrentMana(),
							mana.getMaxMana(),
							mana.getGlyphBonus(),
							mana.getBookTier()
					)
			);

			ParticleTextIndicators.showIndicator(player, null, amount, ParticleTextIndicators.Type.MANA, 0);
		});
	}

	@Redirect(
			method = "jumpFromGround",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/player/Player;causeFoodExhaustion(F)V"
			)
	)
	private void redirectJumpExhaustion(Player instance, float original) {
		float multiplier = (float) Config.Exhaustion.jumpMul;
		float modified = original * multiplier;
		instance.causeFoodExhaustion(modified);
	}

	@Redirect(
			method = "checkMovementStatistics",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/player/Player;causeFoodExhaustion(F)V",
					ordinal = 0
			)
	)
	private void redirectSwimExhaustion(Player player, float original) {
		float multiplier = (float) Config.Exhaustion.swimMul;
		float modified = original * multiplier;
		player.causeFoodExhaustion(modified);
	}

	@Redirect(
			method = "checkMovementStatistics",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/player/Player;causeFoodExhaustion(F)V",
					ordinal = 1
			)
	)
	private void redirectUnderwaterWalkExhaustion(Player player, float original) {
		float multiplier = (float) Config.Exhaustion.underwaterWalkMul;
		float modified = original * multiplier;
		player.causeFoodExhaustion(modified);
	}

	@Redirect(
			method = "checkMovementStatistics",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/player/Player;causeFoodExhaustion(F)V",
					ordinal = 2
			)
	)
	private void redirectShallowWaterWalkExhaustion(Player player, float original) {
		float multiplier = (float) Config.Exhaustion.shallowWaterWalkMul;
		float modified = original * multiplier;
		player.causeFoodExhaustion(modified);
	}

	@Redirect(
			method = "checkMovementStatistics",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/player/Player;causeFoodExhaustion(F)V",
					ordinal = 3
			)
	)
	private void redirectSprintExhaustion(Player player, float original) {
		float multiplier = (float) Config.Exhaustion.sprintMul;
		float modified = original * multiplier;
		player.causeFoodExhaustion(modified);
	}

	@Override
	public boolean adjcore$tryCastSpell(int manaCost) {
		var player = (Player) (Object) this;
		IManaCap manaCap = CapabilityRegistry.getMana(player).orElse(null);
		if (manaCap == null)
			return false;
		boolean canCast = manaCost <= manaCap.getCurrentMana() || player.isCreative();
		if (!canCast) {
			if (!player.getCommandSenderWorld().isClientSide) {
//				PortUtil.sendMessageNoSpam(player, Component.translatable("ars_nouveau.spell.no_mana"));
				if (player instanceof ServerPlayer serverPlayer) {
					Networking.sendToPlayerClient(new NotEnoughManaPacket(manaCost), serverPlayer);
				}
			}
		} else {
			manaCap.removeMana(manaCost);
		}
		return canCast;
	}
}
