package xyz.kohara.adjcore.mixins.compat;

import dev.ftb.mods.ftbquests.quest.QuestObject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = QuestObject.class, remap = false)
public interface FTBQuestObjectAccessor {

    @Accessor("disableToast")
    boolean adj$isToastDisabled();
}
