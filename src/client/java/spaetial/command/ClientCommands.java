package spaetial.command;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import spaetial.Spaetial;
import spaetial.editing.ClientManager;

public final class ClientCommands {
    private ClientCommands() {}

    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal(Spaetial.MOD_ID)
                .then(ClientCommandManager.literal("on")
                    .executes(context -> {
                        ClientManager.getEditingState().receiveToggleModCommand(true);
                        return 1;
                    })
                )
                .then(ClientCommandManager.literal("off")
                    .executes(context -> {
                        ClientManager.getEditingState().receiveToggleModCommand(false);
                        return 1;
                    })
                )
            );
        });
    }
}
