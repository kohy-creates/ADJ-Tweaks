package xyz.kohara.adjcore.mixins.compat.botania;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import vazkii.botania.api.block_entity.FunctionalFlowerBlockEntity;
import xyz.kohara.adjcore.misc.events.BotaniaFlowerManaChangeEvent;

@Mixin(value = FunctionalFlowerBlockEntity.class, remap = false)
public class FunctionalFlowerBlockEntityMixin {

	@WrapMethod(method = "addMana", remap = false)
	private void wrapAddMana(int mana, Operation<Void> original) {
		var blockEntity = (FunctionalFlowerBlockEntity) (Object) this;

		boolean isGeneration = mana > 0;
		mana = Math.abs(mana);

		var eventHandler = new BotaniaFlowerManaChangeEvent(blockEntity, mana, isGeneration);
		MinecraftForge.EVENT_BUS.post(eventHandler);

		original.call(eventHandler.getAmount() * (isGeneration ? 1 : -1));
	}
}
