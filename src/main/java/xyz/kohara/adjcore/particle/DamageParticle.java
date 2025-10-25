package xyz.kohara.adjcore.particle;

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
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class DamageParticle extends Particle {

    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        DF1 = new DecimalFormat("#.##", symbols);
        DF2 = new DecimalFormat("#.#", symbols);
    }

    public static final DecimalFormat DF2;
    public static final DecimalFormat DF1;

    private static final List<Float> POSITIONS = new ArrayList<>(Arrays.asList(0f, -0.25f, 0.12f, -0.12f, 0.25f));

    private final Font fontRenderer = Minecraft.getInstance().font;

    private final Component text;
    private final List<Color> color;
    private float fadeout = -1;
    private float prevFadeout = -1;

    //visual offset
    private float visualDY = 0;
    private float prevVisualDY = 0;
    private float visualDX = 0;
    private float prevVisualDX = 0;

    private final int type;

    private final List<List<Color>> COLORS = List.of(
            List.of(Color.decode("#F58E27"), Color.decode("#FAAE64")),
            List.of(Color.decode("#9C0909"), Color.decode("#E33B3B")),
            List.of(Color.decode("#3BE346"), Color.decode("#7EE686")),
            List.of(Color.decode("#FF3300"), Color.decode("#FF7E42"))
    );

    public DamageParticle(ClientLevel clientLevel,
                          double x,
                          double y,
                          double z,
                          double amount,
                          double type,
                          double unused
    ) {
        super(clientLevel, x, y, z);
        this.lifetime = 45;
        this.type = (int) type;

        this.color = COLORS.get(this.type);

        double number = Math.abs(amount);
        this.yd = 1;

        MutableComponent text = Component.literal(String.valueOf(number));
        if (this.type == 4) {
            text.append("!");
        }
        this.text = text;

        this.xd = POSITIONS.get(ThreadLocalRandom.current().nextInt(POSITIONS.size()));
    }

    @Override
    public void render(@NotNull VertexConsumer consumer, Camera camera, float partialTicks) {

        Vec3 cameraPos = camera.getPosition();
        float particleX = (float) (Mth.lerp(partialTicks, this.xo, this.x) - cameraPos.x());
        float particleY = (float) (Mth.lerp(partialTicks, this.yo, this.y) - cameraPos.y());
        float particleZ = (float) (Mth.lerp(partialTicks, this.zo, this.z) - cameraPos.z());


        int light = LightTexture.FULL_BRIGHT;


        PoseStack poseStack = new PoseStack();
        poseStack.pushPose();
        poseStack.translate(particleX, particleY, particleZ);


        double distanceFromCam = new Vec3(particleX, particleY, particleZ).length();

        double inc = Mth.clamp(distanceFromCam / 32f, 0, 5f);

        // animation
//        poseStack.translate(0, (1 + inc / 4f) * Mth.lerp(partialTicks, this.prevVisualDY, this.visualDY), 0);
        // rotate towards camera

        float fadeout = Mth.lerp(partialTicks, this.prevFadeout, this.fadeout);

        float defScale = (this.type != 4) ? 0.0125f : 0.01f;
        float scale = (float) (defScale * distanceFromCam);
        poseStack.mulPose(camera.rotation());

        // animation
//        poseStack.translate((1 + inc) * Mth.lerp(partialTicks, this.prevVisualDX, this.visualDX), 0, 0);
        // scale depending on distance so size remains the same
        poseStack.scale(-scale, -scale, scale);
        poseStack.translate(0, (4d * (1 - fadeout)), 0);
        poseStack.scale(fadeout, fadeout, fadeout);
        poseStack.translate(0, -distanceFromCam / 10d, 0);

        var buffer = Minecraft.getInstance().renderBuffers().bufferSource();

        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(770, 771, 1, 0);

        float x1 = 0.5f - fontRenderer.width(text) / 2f;

        int color = flashColor(this.color.get(0), this.color.get(1));
        fontRenderer.drawInBatch(text, x1,
                0, color, false,
                poseStack.last().pose(), buffer, Font.DisplayMode.NORMAL, 0, light);
        poseStack.translate(1, 1, +0.03);
        fontRenderer.drawInBatch(text, x1,
                0, darken(color, 0.75d), false,
                poseStack.last().pose(), buffer, Font.DisplayMode.NORMAL, 0, light);

        buffer.endBatch();

        poseStack.popPose();
    }


    @Override
    public void tick() {

        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            float length = 6;
            this.prevFadeout = this.fadeout;
            this.fadeout = this.age > (lifetime - length) ? ((float) lifetime - this.age) / length : 1;

            this.prevVisualDY = this.visualDY;
            this.visualDY += (float) this.yd;
            this.prevVisualDX = this.visualDX;
            this.visualDX += (float) this.xd;

            if (Math.sqrt(Mth.square(this.visualDX * 1.5) + Mth.square(this.visualDY - 1)) < 1.9 - 1) {

                this.yd = this.yd / 2;
            } else {
                this.yd = 0;
                this.xd = 0;
            }
        }
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.CUSTOM;
    }


    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class Factory implements ParticleProvider<SimpleParticleType> {
        public Factory(SpriteSet spriteSet) {
        }

        @SubscribeEvent
        public static void register(final RegisterParticleProvidersEvent event) {
            event.registerSpriteSet(ModParticles.DAMAGE_PARTICLE.get(), Factory::new);
        }

        @Override
        public Particle createParticle(@NotNull SimpleParticleType typeIn, @NotNull ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new DamageParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
        }
    }

    public int flashColor(Color c1, Color c2) {
        float speed = 0.33f;
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
}