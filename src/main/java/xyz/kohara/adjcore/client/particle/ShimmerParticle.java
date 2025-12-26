package xyz.kohara.adjcore.client.particle;

import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Quaternionf;
import xyz.kohara.adjcore.registry.ADJParticles;

public class ShimmerParticle extends RotatedTextureSheetParticle {

        final SpriteSet spriteSet;
        final float rotationSpeed;
        final float initialRotation;

        private ShimmerParticle(ClientLevel level, double x, double y, double z, double dx, double dy, double dz, SpriteSet spriteSet) {
            super(level, x, y, z);
            this.spriteSet = spriteSet;
            this.xd = dx;
            this.yd = dy;
            this.zd = dz;
            this.x = x;
            this.y = y;
            this.z = z;
            this.quadSize = 0.1F * (this.random.nextFloat() * 0.2F + 0.5F);
            float f = this.random.nextFloat() + 0.8F;
            this.rCol *= Mth.clamp(f, 0.8F, 1.0F);
            this.gCol *= Mth.clamp(f, 0.9F, 1.0F);
            this.bCol *= Mth.clamp(f, 0.9F, 1.0F);
            this.lifetime = (int) (Math.random() * 10.0D) + 60;
            this.rotationSpeed = (this.random.nextFloat() * 200 + 200) * (this.random.nextBoolean() ? 1 : -1);
            this.initialRotation = random.nextFloat() * 360;
            this.setSpriteFromAge(spriteSet);

        }

        @Override
        public ParticleRenderType getRenderType() {
            return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
        }

        @Override
        protected Quaternionf getRotation(Camera camera, float partialTicks) {
            float a = (this.age + partialTicks) / this.lifetime;
            return Axis.XP.rotationDegrees(90).rotateZ((this.initialRotation + (this.rotationSpeed * a) * Mth.DEG_TO_RAD));
        }

        @Override
        protected boolean doubleSided(Camera camera)
        {
            return true;
        }

        @Override
        public void move(double x, double y, double z)
        {
            this.setBoundingBox(this.getBoundingBox().move(x, y, z));
            this.setLocationFromBoundingbox();
        }

        @Override
        public float getQuadSize(float partialTick)
        {
            float f = ((float) this.age + partialTick) / (float) this.lifetime;
            f = 1.0F - f;
            f = f * f;
            f = 1.0F - f;
            return this.quadSize * f * 3;
        }

        @Override
        public int getLightColor(float partialTick)
        {
            int i = super.getLightColor(partialTick);
            float f = (float) this.age / (float) this.lifetime;
            f = f * f;
            f = f * f;
            int j = i & 255;
            int k = i >> 16 & 255;
            k = k + (int) (f * 15.0F * 16.0F);
            if (k > 240)
            {
                k = 240;
            }

            return j | k << 16;
        }

        @Override
        public void tick()
        {
            this.xo = this.x;
            this.yo = this.y;
            this.zo = this.z;
            if (this.age++ >= this.lifetime)
            {
                this.remove();
            }
            else
            {
                this.setSpriteFromAge(spriteSet);
                this.move(this.xd, this.yd, this.zd);
                this.xd *= 0.98;
                this.zd *= 0.98;
                this.yd = this.yd * 1.115;

                float a = 1.0F - this.age / (float) this.lifetime;
                this.alpha = (float) (Math.sin((a * Math.PI) / 2));
            }
        }

    @OnlyIn(Dist.CLIENT)
    public static class ShimmerParticleFactory implements ParticleProvider<SimpleParticleType> {

        private final SpriteSet spriteSet;

        public ShimmerParticleFactory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle createParticle(
                SimpleParticleType type,
                ClientLevel level,
                double x, double y, double z,
                double dx, double dy, double dz
        ) {
            return new ShimmerParticle(level, x, y, z, dx, dy, dz, this.spriteSet);
        }
    }
}
