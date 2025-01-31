package org.rod.commands;

import net.minestom.server.command.builder.Command;
import net.minestom.server.entity.Player;

public class heal extends Command {
    //TO-DO: Add target player argument
    public heal() {
        super("heal");

        setDefaultExecutor((sender, context) -> {
            sender.sendMessage("You have been healed");
            var senderPlayer = (Player) sender;

            senderPlayer.setHealth(200f);
        });
    }
}