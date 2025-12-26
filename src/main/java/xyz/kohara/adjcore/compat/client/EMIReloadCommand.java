package xyz.kohara.adjcore.compat.client;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xyz.kohara.adjcore.ADJCore;
import xyz.kohara.adjcore.client.networking.ADJMessages;
import xyz.kohara.adjcore.client.networking.packet.EmiReloadS2CPacket;

import java.util.Collection;

@Mod.EventBusSubscriber(modid = ADJCore.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EMIReloadCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("adjreloademi")
                .requires(cs -> cs.hasPermission(2))
                .then(Commands.argument("targets", EntityArgument.players())
                        .executes(EMIReloadCommand::reloadEmi))
        );
    }

    private static int reloadEmi(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "targets");
        players.forEach(serverPlayer -> {
            ADJMessages.sendToPlayer(new EmiReloadS2CPacket(), serverPlayer);
        });
        return 1;
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        register(event.getDispatcher());
    }
}
