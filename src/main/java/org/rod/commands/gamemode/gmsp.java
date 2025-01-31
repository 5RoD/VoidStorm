package org.rod.commands.gamemode;

import net.minestom.server.command.builder.Command;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;

public class gmsp extends Command {

    public gmsp() {
        super("gmsp");

        setDefaultExecutor((sender, context) -> {
            sender.sendMessage("You are now in spectator mode");

            var senderPlayer = (Player) sender;
            senderPlayer.setGameMode(GameMode.SPECTATOR);
        });
    }
}
