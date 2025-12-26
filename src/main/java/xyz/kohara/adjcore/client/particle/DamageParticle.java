package xyz.kohara.adjcore.client.particle;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import xyz.kohara.adjcore.registry.ADJParticles;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

// Adapted from https://github.com/MehVahdJukaar/DuMmmMmmy/blob/1.20/common/src/main/java/net/mehvahdjukaar/dummmmmmy/client/DamageNumberParticle.java
public class DamageParticle extends Particle {

    private static final List<Float> POSITIONS = new ArrayList<>(Arrays.asList(0f, -0.25f, 0.12f, -0.12f, 0.25f));

    private final Font fontRenderer = Minecraft.getInstance().font;

    private final Component text;
    private final List<Color> color;
    private float fadeout = 0;
    private float prevFadeout = 0;

    //visual offset
    private float visualDY = 0;
    private float visualDX = 0;

    private final int type;

    private float maxRotation;
    private float rotationSpeed;  // how fast it swings
    private float rotation;

    public DamageParticle(ClientLevel clientLevel,
                          double x,
                          double y,
                          double z,
                          double amount,
                          double type,
                          double unused
    ) {
        super(clientLevel, x, y, z);

        this.lifetime = 60;

        this.type = (int) type;
        List<List<Color>> COLORS = List.of(
                List.of(Color.decode("#F58E27"), Color.decode("#FAAE64")),
                List.of(Color.decode("#9C0909"), Color.decode("#E33B3B")),
                List.of(Color.decode("#3BE346"), Color.decode("#7EE686")),
                List.of(Color.decode("#FF3300"), Color.decode("#FF7E42"))
        );
        this.color = COLORS.get(this.type);

        this.yd = 1;

        int number = (int) Math.abs(amount);
        MutableComponent text = Component.literal(String.valueOf(number));
        if (this.type == 3) {
            text.append("!");
            text.withStyle(Style.EMPTY.withBold(true));
        }
        this.text = text;

        this.xd = POSITIONS.get(ThreadLocalRandom.current().nextInt(POSITIONS.size()));

        // speed of oscillation
        this.rotationSpeed = 0.1f;

        // Larger angles for crit hits
        if (this.type == 3) {
            this.maxRotation = 22.5f;
            this.rotationSpeed *= 1.5f;
        } else {
            this.maxRotation = 15f;
        }
    }

    @Override
    public void render(@NotNull VertexConsumer consumer, Camera camera, float partialTicks) {

        RenderSystem.disableDepthTest();

        Vec3 cameraPos = camera.getPosition();
        float particleX = (float) (Mth.lerp(partialTicks, this.xo, this.x) - cameraPos.x());
        float particleY = (float) (Mth.lerp(partialTicks, this.yo, this.y) - cameraPos.y());
        float particleZ = (float) (Mth.lerp(partialTicks, this.zo, this.z) - cameraPos.z());

        PoseStack poseStack = new PoseStack();
        poseStack.pushPose();
        poseStack.translate(particleX, particleY, particleZ);

        double distanceFromCam = new Vec3(particleX, particleY, particleZ).length();

        // rotate towards camera

        float fadeout = Mth.lerp(partialTicks, this.prevFadeout, this.fadeout);

        float defScale = (this.type == 3) ? 0.0125f : 0.01f;
        float scale = (float) (defScale * distanceFromCam);
        poseStack.mulPose(camera.rotation());

        poseStack.translate(0, this.bbHeight / 2, 0); // move pivot to center
        poseStack.mulPose(new Quaternionf().rotateZ((float) Math.toRadians(this.rotation)));
        poseStack.translate(0, -this.bbHeight / 2, 0); // move back pivo

        // scale depending on distance so size remains the same
        poseStack.scale(-scale, -scale, scale);
        poseStack.translate(0, (4d * (1 - fadeout)), 0);
        poseStack.scale(fadeout, fadeout, fadeout);
        poseStack.translate(0, -distanceFromCam / 10d, 0);

        MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();

        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(770, 771, 1, 0);

        float x1 = 0.5f - fontRenderer.width(text) / 2f;
        float y1 = 0.5f - fontRenderer.lineHeight;

        int color = flashColor(this.color.get(0), this.color.get(1));

        renderNumber(this.text, poseStack, x1, y1, color, buffer);

        buffer.endBatch();

        poseStack.popPose();
    }

    private void renderNumber(Component text, PoseStack poseStack, float x, float y, int color, MultiBufferSource.BufferSource buffer) {
        int light = LightTexture.FULL_BRIGHT;
        Matrix4f matrix = poseStack.last().pose();

        // Main bright text
        fontRenderer.drawInBatch(text, x, y, color, false, matrix, buffer, Font.DisplayMode.NORMAL, 0, light);

        // Dark outline
        int darkColor = darken(color, 0.75d);
        float[][] offsets = {
                { 1, 0 }, {-1, 0}, {0, 1}, {0, -1}
        };

        poseStack.translate(0, 0, 0.03);
        for (float[] o : offsets) {
            fontRenderer.drawInBatch(text, x + o[0], y + o[1], darkColor, false, matrix, buffer, Font.DisplayMode.NORMAL, 0, light);
        }
        poseStack.translate(0, 0, -0.03);
    }



    @Override
    public void tick() {

        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;



        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            // save previous fadeout for smooth interpolation
            this.prevFadeout = this.fadeout;

            // symmetrical fade-in/out
            float fadeOutLength = 6f; // how many ticks for fade-in and fade-out
            float fadeInLength = 2f;
            float fadeIn = Math.min(1f, this.age / fadeInLength);
            float fadeOut = (this.age > (lifetime - fadeOutLength)) ? ((lifetime - this.age) / fadeOutLength) : 1f;

            this.fadeout = fadeIn * fadeOut;

            float oscillation = (float) Math.sin(this.age * this.rotationSpeed);
            this.rotation = oscillation * maxRotation;

            // movement update
            this.visualDY += (float) this.yd;
            this.visualDX += (float) this.xd;

            // slow down movement near target
            if (Math.sqrt(Mth.square(this.visualDX * 1.5) + Mth.square(this.visualDY - 1)) < 0.9) {
                this.yd /= 2;
            } else {
                this.yd = 0;
                this.xd = 0;
            }
        }
    }

    public int flashColor(Color c1, Color c2) {
        float speed = 0.56666f;
        float t = (float) ((Math.sin(this.age * speed) * 0.5f) + 0.5f);

        int r = (int) (c1.getRed()   + (c2.getRed()   - c1.getRed())   * t);
        int g = (int) (c1.getGreen() + (c2.getGreen() - c1.getGreen()) * t);
        int b = (int) (c1.getBlue()  + (c2.getBlue()  - c1.getBlue())  * t);

        return new Color(r, g, b).getRGB();
    }

    public static int darken(int colorInt, double amount) {
        double factor = 1.0 - amount;

        int alpha = (colorInt >> 24) & 0xFF;
        int red   = (colorInt >> 16) & 0xFF;
        int green = (colorInt >> 8)  & 0xFF;
        int blue  = colorInt & 0xFF;

        red   = (int)(red * factor);
        green = (int)(green * factor);
        blue  = (int)(blue * factor);

        red   = Math.max(0, Math.min(255, red));
        green = Math.max(0, Math.min(255, green));
        blue  = Math.max(0, Math.min(255, blue));

        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.CUSTOM;
    }

    @OnlyIn(Dist.CLIENT)
    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class Factory implements ParticleProvider<SimpleParticleType> {
        public Factory(SpriteSet spriteSet) {
        }

        @SubscribeEvent
        public static void register(final RegisterParticleProvidersEvent event) {
            event.registerSpriteSet(ADJParticles.DAMAGE_PARTICLE.get(), Factory::new);
        }

        @Override
        public Particle createParticle(@NotNull SimpleParticleType typeIn, @NotNull ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new DamageParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
        }
    }
}