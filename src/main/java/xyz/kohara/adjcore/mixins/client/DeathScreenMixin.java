package xyz.kohara.adjcore.mixins.client;

import net.minecraft.client.CameraType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.level.GameType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.kohara.adjcore.ADJCore;
import xyz.kohara.adjcore.client.sounds.ModSoundEvents;

import javax.annotation.Nullable;
import java.util.List;

@Mixin(DeathScreen.class)
public abstract class DeathScreenMixin extends Screen {

    @Shadow
    private int delayTicker;

    @Shadow
    protected abstract void setButtonsActive(boolean active);

    @Shadow
    @Nullable
    private Button exitToTitleButton;

    @Shadow
    @Final
    private List<Button> exitButtons;

    @Shadow
    @Final
    private Component causeOfDeath;

    @Shadow
    @Final
    private boolean hardcore;

    protected DeathScreenMixin(Component title) {
        super(title);
    }

    @Unique
    private int adj$respawnTimer;

    @Unique
    private boolean adj$wasInFirstPerson = false;

    @Unique
    private String adj$deathText;

    @Unique
    private boolean adj$wasInit = false;

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void tick(CallbackInfo ci) {
        ci.cancel();

        super.tick();
        this.delayTicker++;
        if (this.delayTicker > 20) {
            this.setButtonsActive(true);
        }

        if (adj$respawnTimer < 0) {
            Component component = Component.literal((hardcore) ? "Cancel this moratory" : "Respawn");

            if (this.exitButtons.size() < 2) {
                this.exitButtons.add(this.addRenderableWidget(Button.builder(component, arg -> {
                    this.minecraft.player.respawn();
                    if (adj$wasInFirstPerson) {
                        minecraft.options.setCameraType(CameraType.FIRST_PERSON);
                    }
                    adj$wasInit = false;
                }).bounds(this.width / 2 - 100, this.height / 4 + 150, 200, 25).build()));
            }
        }
        adj$respawnTimer--;
    }

    @Inject(method = "init", at = @At("HEAD"), cancellable = true)
    protected void init(CallbackInfo ci) {
        ci.cancel();
        this.exitButtons.clear();
        this.setButtonsActive(false);

        if (adj$wasInit) return;
        adj$wasInit = true;

        if (minecraft.options.getCameraType() == CameraType.FIRST_PERSON) {
            adj$wasInFirstPerson = true;
            minecraft.options.setCameraType(CameraType.THIRD_PERSON_BACK);
        }

        this.delayTicker = 0;

        // Default respawn time = 7 seconds
        // x2 in hardcore, x1.5 in multiplayer
        adj$respawnTimer = (int) (140 * ((hardcore) ? 2 : 1) * (!minecraft.isSingleplayer() ? 1.5 : 1));

        adj$deathText = ADJCore.getRandomDeathText();
        minecraft.getSoundManager().play(
                SimpleSoundInstance.forUI(
                        ModSoundEvents.DEATH_SCREEN.get(),
                        1F,
                        1F
                )
        );
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        ci.cancel();

        int width = this.width;
        int height = this.height;

        guiGraphics.fillGradient(0, 0, width, height, 1615855616, -1602211792);
        guiGraphics.pose().pushPose();

        guiGraphics.pose().scale(4.0F, 4.0F, 4.0F);
        String title = Component.translatable("deathScreen.title").getString().toUpperCase();
        Component screenTitle = Component.translatable(title).withStyle(Style.EMPTY.withBold(true));
        guiGraphics.drawCenteredString(this.font, screenTitle, width / 8, 60 / 4, 0xFFFFFFFF);
        guiGraphics.pose().popPose();

        if (this.causeOfDeath != null) {
            String deathCause = this.causeOfDeath.getString().substring(2)
                    .replace(minecraft.player.getName().getString(), "You")
                    .replace("was", "were");
            guiGraphics.drawCenteredString(this.font, Component.literal(deathCause).withStyle(Style.EMPTY.withColor(TextColor.parseColor("#9E9E9E"))), width / 2, 130, 0xFFFFFFFF);
        }

        Component deathText = Component.literal(adj$deathText).withStyle(Style.EMPTY.withItalic(true).withColor(TextColor.parseColor("#7D7D7D")));
        guiGraphics.drawCenteredString(this.font, deathText, width / 2, 150, 0xFFFFFFFF);

        if (adj$respawnTimer > -1) {
            guiGraphics.pose().pushPose();
            guiGraphics.pose().scale(2.0F, 2.0F, 2.0F);

            String timerString = String.valueOf((adj$respawnTimer / 20) + 1);
            Component timerText = Component.literal(timerString).withStyle(Style.EMPTY.withItalic(false).withColor(TextColor.parseColor("#C2C2C2")).withBold(true));

            guiGraphics.drawCenteredString(this.font, timerText, width / 4, height / 4 + 34, 0xFFFFFFFF);
            guiGraphics.pose().popPose();
        }

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

}
