package xyz.kohara.adjcore.mixins.compat.ars;

import com.hollingsworth.arsnouveau.api.mana.IManaCap;
import com.hollingsworth.arsnouveau.setup.registry.CapabilityRegistry;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = CapabilityRegistry.class, remap = false)
public class CapabilityRegistryMixin {

	@Shadow
	@Final
	public static Capability<IManaCap> MANA_CAPABILITY;

	@WrapMethod(method = "getMana", remap = false)
	private static LazyOptional<IManaCap> getMana(LivingEntity entity, Operation<LazyOptional<IManaCap>> original) {
		if (entity == null || !entity.isAlive()) return LazyOptional.empty();
		return entity.getCapability(MANA_CAPABILITY);
	}
}
