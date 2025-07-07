package xyz.kohara.adjcore;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xyz.kohara.adjcore.attributes.AttributeReplace;
import xyz.kohara.adjcore.effects.editor.EffectsEditor;
import xyz.kohara.adjcore.potions.PotionsEditor;

@Mod.EventBusSubscriber(modid = ADJCore.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ReloadCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("adjreload")
                        .requires(source -> source.hasPermission(2))
                        .executes(context -> {
                            PotionsEditor.edit();
                            AttributeReplace.loadConfig();
                            EffectsEditor.edit();
                            return 1;
                        })
        );
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        register(event.getDispatcher());
    }
}
