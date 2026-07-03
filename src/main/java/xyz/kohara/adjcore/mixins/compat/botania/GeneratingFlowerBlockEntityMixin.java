package xyz.kohara.adjcore.mixins.compat.botania;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import vazkii.botania.api.block_entity.GeneratingFlowerBlockEntity;
import xyz.kohara.adjcore.misc.events.BotaniaFlowerManaChangeEvent;

@Mixin(value = GeneratingFlowerBlockEntity.class, remap = false)
public class GeneratingFlowerBlockEntityMixin {

	@WrapMethod(method = "addMana", remap = false)
	private void wrapAddMana(int mana, Operation<Void> original) {
		var blockEntity = (GeneratingFlowerBlockEntity) (Object) this;

		var eventHandler = new BotaniaFlowerManaChangeEvent(blockEntity, mana);
		MinecraftForge.EVENT_BUS.post(eventHandler);

		original.call(eventHandler.getAmount());
	}

	@ModifyReturnValue(method = "getBindingRadius", at = @At("RETURN"), remap = false)
	private int increaseBindingRadius(int original) {
		return 10;
	}
}
