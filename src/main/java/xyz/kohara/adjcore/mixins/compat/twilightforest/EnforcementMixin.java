package xyz.kohara.adjcore.mixins.compat.twilightforest;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import twilightforest.init.custom.Enforcement;

import java.util.function.Supplier;

@Mixin(value = Enforcement.class, remap = false)
public class EnforcementMixin {

	@Redirect(
			method = "<clinit>",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraftforge/registries/DeferredRegister;register(Ljava/lang/String;Ljava/util/function/Supplier;)Lnet/minecraftforge/registries/RegistryObject;"
			)
	)
	private static RegistryObject<Enforcement> redirectDarkness(
			DeferredRegister<Enforcement> register,
			String name,
			Supplier<Enforcement> original
	) {
		if (name.equals("darkness")) {
			return register.register(
					"darkness",
					() -> new Enforcement((player, level, restriction) -> {
						if (player.tickCount % 60 == 0) {
							player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 100, 0, false, true));
							player.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 100, 2, false, true));
							player.addEffect(new MobEffectInstance(MobEffects.WITHER, 100, 1, false, true));
						}
					})
			);
		}

		return register.register(name, original);
	}
}