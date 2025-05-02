package xyz.kohara.adjtweaks;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xyz.kohara.adjtweaks.attributes.AttributeReplace;
import xyz.kohara.adjtweaks.potions.PotionsEditor;

@Mod.EventBusSubscriber(modid = ADJTweaks.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ReloadCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                CommandManager.literal("adjreload")
                        .requires(source -> source.hasPermissionLevel(2))
                        .executes(context -> {
                            PotionsEditor.edit();
                            AttributeReplace.loadConfig();
                            return 1;
                        })
        );
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        register(event.getDispatcher());
    }
}
