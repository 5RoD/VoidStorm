package org.rod.commands.gamemode;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.builder.Command;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;

public class gmsp extends Command {
    //TO-DO: Add target player argument
    public gmsp() {
        super("gmsp");

        setDefaultExecutor((sender, context) -> {
            sender.sendMessage(Component.text("You are now in spectator mode!")
                    .color(NamedTextColor.GREEN));

            var senderPlayer = (Player) sender;
            senderPlayer.setGameMode(GameMode.SPECTATOR);
        });
    }
}
