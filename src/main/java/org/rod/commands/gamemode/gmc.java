package org.rod.commands.gamemode;

import net.minestom.server.command.builder.Command;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;

public class gmc extends Command {

    public gmc() {
        super("gmc");

        setDefaultExecutor((sender, context) -> {
            sender.sendMessage("You are now in creative mode");


            var senderPlayer = (Player) sender;

            senderPlayer.setGameMode(GameMode.CREATIVE);
        });
    }
}
