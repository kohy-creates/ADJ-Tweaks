package xyz.kohara.adjcore;

import net.minecraftforge.fml.loading.FMLLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class MixinTransformerPlugin implements IMixinConfigPlugin {
    @Override
    public void onLoad(String s) {
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    private boolean isModLoaded(String modId) {
        return FMLLoader.getLoadingModList().getModFileById(modId) != null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (mixinClassName.contains("client.GuiItemRenderingMixin")) {
            if (mixinClassName.contains("BiggerStacks")) {
                return isModLoaded("biggerstacks");
            } else {
                return !isModLoaded("biggerstacks");
            }
        } else if (mixinClassName.contains("Embeddium")) {
            return isModLoaded("sodium");
        } else if (mixinClassName.contains("fnafplushies")) {
            return isModLoaded("fnaf_plushie_remastered");
        } else if (mixinClassName.contains("LegendaryTabs")) {
            return isModLoaded("legendarytabs");
        } else if (mixinClassName.contains("aether")) {
            return isModLoaded("aether");
        } else if (mixinClassName.contains("twilightforest")) {
            return isModLoaded("twilightforest");
        } else if (mixinClassName.contains("evilcraft")) {
            return isModLoaded("evilcraft");
        } else if (mixinClassName.contains("botania")) {
            return isModLoaded("botania");
        }
        else if (mixinClassName.contains("LootJournal")) {
            return isModLoaded("loot_journal");
        }
        return true;
    }

    @Override
    public void acceptTargets(Set<String> set, Set<String> set1) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String s, ClassNode classNode, String s1, IMixinInfo iMixinInfo) {
    }

    @Override
    public void postApply(String s, ClassNode classNode, String s1, IMixinInfo iMixinInfo) {
    }
}
