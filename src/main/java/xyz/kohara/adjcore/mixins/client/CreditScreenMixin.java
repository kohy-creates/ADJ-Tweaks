package xyz.kohara.adjcore.mixins.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.LogoRenderer;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.WinScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.kohara.adjcore.ADJCore;
import xyz.kohara.adjcore.misc.credits.Credits;
import xyz.kohara.adjcore.misc.credits.LoaderInfo;
import xyz.kohara.adjcore.misc.credits.ModInfo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Stream;

@Mixin(WinScreen.class)
public abstract class CreditScreenMixin extends Screen {

    @Shadow
    private float scroll;

    @Shadow
    @Final
    private static String OBFUSCATE_TOKEN;

    @Shadow
    protected abstract void addPoemLines(String text);

    @Shadow
    protected abstract void addEmptyLine();

    @Shadow
    protected abstract void addCreditsLine(Component creditsLine, boolean centered);

    @Shadow
    @Final
    private LogoRenderer logoRenderer;

    @Shadow
    protected abstract void renderBg(GuiGraphics guiGraphics);

    @Shadow
    private float scrollSpeed;

    @Shadow
    private IntSet centeredLines;

    @Shadow
    private List<FormattedCharSequence> lines;

    @Shadow
    @Final
    private static ResourceLocation VIGNETTE_LOCATION;

    protected CreditScreenMixin(Component title) {
        super(title);
    }

    @Unique
    private static final Component adj$SECTION_HEADING = Component.literal("                                      ")
            .withStyle(Style.EMPTY
                    .withStrikethrough(true)
                    .withColor(ChatFormatting.WHITE)
                    .withBold(true)
            );

    @Unique
    private static boolean adj$isPostCredits = false;

    @Inject(method = "init()V", at = @At(value = "INVOKE_ASSIGN", target = "Ljava/util/List;size()I"))
    protected void initInvoke(CallbackInfo ci) {
        this.scroll = 100;
        adj$isPostCredits = false;
    }


    @Inject(method = "addPoemFile", at = @At("HEAD"), cancellable = true)
    private void modifyPoemFile(Reader oldReader, CallbackInfo ci) throws IOException {

        Reader reader;

        if (adj$isPostCredits) {
            if (!Credits.Files.POST_CREDITS.exists()) {
                return;
            }
            reader = new BufferedReader(new FileReader(Credits.Files.POST_CREDITS.getFile(), StandardCharsets.UTF_8));
        } else {
            if (!Credits.Files.POEM.exists()) {
                return;
            }
            reader = new BufferedReader(new FileReader(Credits.Files.POEM.getFile(), StandardCharsets.UTF_8));
        }

        ci.cancel();


        BufferedReader bufferedReader = new BufferedReader(reader);
        RandomSource randomSource = RandomSource.create(8124371L);

        String string;
        while ((string = bufferedReader.readLine()) != null) {
            string = string.replaceAll("PLAYERNAME", this.minecraft.getUser().getName());

            int i;
            while ((i = string.indexOf(OBFUSCATE_TOKEN)) != -1) {
                String string2 = string.substring(0, i);
                String string3 = string.substring(i + OBFUSCATE_TOKEN.length());
                string = string2 + ChatFormatting.WHITE + ChatFormatting.OBFUSCATED + "XXXXXXXX".substring(0, randomSource.nextInt(4) + 3) + string3;
            }

            this.addPoemLines(string);
            if (adj$isPostCredits) this.centeredLines.add(this.lines.size());
            this.addEmptyLine();
        }

        for (int i = 0; i < ((adj$isPostCredits) ? 3 : 8); i++) {
            this.addEmptyLine();
        }

        adj$isPostCredits = true;
    }

    @Inject(method = "addCreditsFile", at = @At("HEAD"), cancellable = true)
    private void modifyCreditsFile(Reader oldReader, CallbackInfo ci) throws IOException {

        if (!Credits.Files.CREDITS.exists()) {
            return;
        }

        ci.cancel();

        Reader reader = new BufferedReader(new FileReader(Credits.Files.CREDITS.getFile(), StandardCharsets.UTF_8));

        for (JsonElement jsonElement : GsonHelper.parseArray(reader)) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            String string = jsonObject.get("section").getAsString();
            this.addCreditsLine(
                    Component.literal(string).withStyle(
                            Style.EMPTY
                                    .withColor(ChatFormatting.YELLOW)
                                    .withUnderlined(true)
                                    .withBold(true)
                    ), true
            );
            this.addEmptyLine();
            this.addEmptyLine();

            for (JsonElement jsonElement2 : jsonObject.getAsJsonArray("disciplines")) {
                JsonObject jsonObject2 = jsonElement2.getAsJsonObject();
                String string2 = jsonObject2.get("discipline").getAsString();
                if (StringUtils.isNotEmpty(string2)) {
                    adj$addCreditTitle(string2);
                }

                for (JsonElement jsonElement3 : jsonObject2.getAsJsonArray("titles")) {
                    JsonObject jsonObject3 = jsonElement3.getAsJsonObject();
                    String string3 = jsonObject3.get("title").getAsString();
                    JsonArray jsonArray4 = jsonObject3.getAsJsonArray("names");
                    this.addCreditsLine(
                            Component.literal(string3).withStyle(Style.EMPTY
                                    .withColor(ChatFormatting.GRAY)
                            ),
                            true
                    );
                    this.addEmptyLine();

                    for (JsonElement jsonElement4 : jsonArray4) {
                        String string4 = jsonElement4.getAsString();
                        boolean addEmpty = adj$addPlayerTitle(string4);
                        this.addCreditsLine(Component.literal(adj$getPlayerSkin(string4)).append(string4).withStyle(ChatFormatting.WHITE), true);
                        if (addEmpty) this.addEmptyLine();
                    }

                    this.addEmptyLine();
                    this.addEmptyLine();
                }
            }
        }
        try {
            LoaderInfo loaderInfo = ADJCore.impl.getLoaderInfo();

            adj$addCreditTitle("Loader");
            adj$addCreditSection(loaderInfo.name(), Stream.concat(
                    loaderInfo.authors().stream(),
                    loaderInfo.contributors().stream()
            ).toList());

            adj$addCreditTitle("Mods");
            try {
                for (ModInfo modInfo : ADJCore.impl.getMods()) {
                    adj$addCreditSection("[mod+" + modInfo.modId() + "] " + modInfo.modName() + "", Stream.concat(
                            modInfo.authors().stream(),
                            modInfo.contributors().stream()
                    ).toList());
                }
            } catch (Exception e) {
                ADJCore.LOGGER.error("Could not add mod credits", e);
            }
        } catch (Exception e) {
            ADJCore.LOGGER.error("Could not add loader credits", e);
        }
        this.addEmptyLine();
        this.addEmptyLine();
        this.addEmptyLine();
    }

    @Unique
    private String adj$getPlayerSkin(String name) {
        String playerName = name;
        if (Credits.NAME_TO_SKIN.containsKey(name)) {
            playerName = Credits.NAME_TO_SKIN.get(name);
        }
        return (playerName.equals("NONE")) ? "" : "[face:" + playerName + "] ";
    }

    @Unique
    private void adj$addCreditTitle(String title) {
        this.addCreditsLine(adj$SECTION_HEADING, true);
        this.addCreditsLine(
                Component.literal(title).withStyle(Style.EMPTY
                        .withBold(true)
                        .withColor(TextColor.parseColor("#FFE136"))
                ),
                true
        );
        this.addCreditsLine(adj$SECTION_HEADING, true);
        this.addEmptyLine();
        this.addEmptyLine();
    }

    @Unique
    private void adj$addCreditSection(String title, List<String> contributors) {
        this.addCreditsLine(
                Component.literal(title).withStyle(Style.EMPTY
                        .withColor(ChatFormatting.GRAY)
                ),
                true
        );
        this.addEmptyLine();

        for (String author : contributors) {
            boolean addEmpty = adj$addPlayerTitle(author);
            this.addCreditsLine(Component.literal(adj$getPlayerSkin(author)).append(author).withStyle(ChatFormatting.WHITE), true);
            if (addEmpty) this.addEmptyLine();
        }

        this.addEmptyLine();
        this.addEmptyLine();
    }

    @Unique
    private boolean adj$addPlayerTitle(String player) {
        if (Credits.PLAYER_TITLES == null) return false;
        boolean has = Credits.PLAYER_TITLES.containsKey(player);
        if (has) {
            this.addCreditsLine(
                    Component.literal(ADJCore.toSmallUnicode(Credits.PLAYER_TITLES.get(player)))
                            .withStyle(Style.EMPTY
                                    .withColor(ChatFormatting.GRAY)
                            ),
                    true
            );
        }
        return has;
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void modifyRender(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        this.scroll = Math.max(0.0F, this.scroll + partialTick * this.scrollSpeed);
        this.renderBg(guiGraphics);
        int i = this.width / 2 - 128;
        int j = this.height + 50;
        float f = -this.scroll;
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0.0F, f, 0.0F);
        this.logoRenderer.renderLogo(guiGraphics, this.width, 1.0F, j);
        int k = j + 100;

        for (int l = 0; l < this.lines.size(); l++) {
            if (l == this.lines.size() - 1) {
                float g = k + f - (this.height / 2 - 6);
                if (g < 0.0F) {
                    guiGraphics.pose().translate(0.0F, -g, 0.0F);
                }
            }

            if (k + f + 12.0F + 8.0F > 0.0F && k + f < this.height) {
                FormattedCharSequence formattedCharSequence = this.lines.get(l);
                if (this.centeredLines.contains(l)) {
                    guiGraphics.drawCenteredString(this.font, formattedCharSequence, i + 128, k, 16777215);
                } else {
                    guiGraphics.drawString(this.font, formattedCharSequence, i, k, 16777215);
                }
            }

            k += 12;
        }

        guiGraphics.pose().popPose();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR);
        guiGraphics.blit(VIGNETTE_LOCATION, 0, 0, 0, 0.0F, 0.0F, this.width, this.height, this.width, this.height);
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }
}
