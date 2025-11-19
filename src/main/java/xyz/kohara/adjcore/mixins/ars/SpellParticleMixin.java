package xyz.kohara.adjcore.mixins.ars;

import com.hollingsworth.arsnouveau.client.particle.GlowParticleData;
import com.hollingsworth.arsnouveau.common.entity.ColoredProjectile;
import com.hollingsworth.arsnouveau.common.entity.EntityProjectileSpell;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EntityProjectileSpell.class, remap = false)
public class SpellParticleMixin extends ColoredProjectile {



    public SpellParticleMixin(EntityType<? extends ColoredProjectile> type, Level worldIn) {
        super(type, worldIn);
    }

    @Inject(method = "playParticles", at = @At("HEAD"), cancellable = true)
    public void playParticles(CallbackInfo ci) {
        ci.cancel();

        double deltaX = getX() - xOld;
        double deltaY = getY() - yOld;
        double deltaZ = getZ() - zOld;
        double dist = Math.ceil(Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ) * 6);
        for (double j = 0; j < dist; j++) {
            double coeff = j / dist;
            level().addParticle(GlowParticleData.createData(getParticleColor()),
                    true,
                    (float) (xo + deltaX * coeff),
                    (float) (yo + deltaY * coeff) + 0.1, (float)
                            (zo + deltaZ * coeff),
                    0.0125f * (random.nextFloat() - 0.5f),
                    0.0125f * (random.nextFloat() - 0.5f),
                    0.0125f * (random.nextFloat() - 0.5f));
        }
    }

}
