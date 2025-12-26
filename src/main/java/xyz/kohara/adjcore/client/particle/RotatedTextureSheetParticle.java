package xyz.kohara.adjcore.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

// From https://gitlab.com/modding-legacy/rediscovered/-/blob/1.20.x/src/main/java/com/legacy/rediscovered/client/particles/RotatedTextureSheetParticle.java?ref_type=heads
public abstract class RotatedTextureSheetParticle extends TextureSheetParticle {
    protected RotatedTextureSheetParticle(ClientLevel pLevel, double pX, double pY, double pZ) {
        super(pLevel, pX, pY, pZ);
    }

    protected RotatedTextureSheetParticle(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
        super(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed);
    }

    @Override
    public void render(VertexConsumer buffer, Camera camera, float partialTicks) {
        Vec3 vec3 = camera.getPosition();
        float f = (float) (Mth.lerp(partialTicks, this.xo, this.x) - vec3.x());
        float f1 = (float) (Mth.lerp(partialTicks, this.yo, this.y) - vec3.y());
        float f2 = (float) (Mth.lerp(partialTicks, this.zo, this.z) - vec3.z());

        // MOVED INTO IT'S OWN METHOD FOR EASY TRANSFORMATION
        Quaternionf quaternionf = this.getRotation(camera, partialTicks);

        Vector3f[] quads = new Vector3f[] { new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F) };
        float f3 = this.getQuadSize(partialTicks);

        for (int i = 0; i < 4; ++i)
        {
            Vector3f vector3f = quads[i];
            vector3f.rotate(quaternionf);
            vector3f.mul(f3);
            vector3f.add(f, f1, f2);
        }

        float u0 = this.getU0();
        float u1 = this.getU1();
        float v0 = this.getV0();
        float v1 = this.getV1();
        int light = this.getLightColor(partialTicks);

        buffer.vertex(quads[0].x(), quads[0].y(), quads[0].z()).uv(u1, v1).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(light).endVertex();
        buffer.vertex(quads[1].x(), quads[1].y(), quads[1].z()).uv(u1, v0).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(light).endVertex();
        buffer.vertex(quads[2].x(), quads[2].y(), quads[2].z()).uv(u0, v0).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(light).endVertex();
        buffer.vertex(quads[3].x(), quads[3].y(), quads[3].z()).uv(u0, v1).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(light).endVertex();

        // Added to render on the opposite side
        if (this.doubleSided(camera))
        {
            // Rendered in reverse quad order with u0 and u1 flipped for mirroring
            buffer.vertex(quads[3].x(), quads[3].y(), quads[3].z()).uv(u0, v1).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(light).endVertex();
            buffer.vertex(quads[2].x(), quads[2].y(), quads[2].z()).uv(u0, v0).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(light).endVertex();
            buffer.vertex(quads[1].x(), quads[1].y(), quads[1].z()).uv(u1, v0).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(light).endVertex();
            buffer.vertex(quads[0].x(), quads[0].y(), quads[0].z()).uv(u1, v1).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(light).endVertex();
        }
    }

    protected Quaternionf getRotation(Camera camera, float partialTicks)
    {
        Quaternionf quaternionf;
        if (this.roll == 0.0F)
        {
            quaternionf = camera.rotation();
        }
        else
        {
            quaternionf = new Quaternionf(camera.rotation());
            quaternionf.rotateZ(Mth.lerp(partialTicks, this.oRoll, this.roll));
        }
        return quaternionf;
    }

    protected boolean doubleSided(Camera camera)
    {
        return false;
    }
}
