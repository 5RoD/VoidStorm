package org.rod.commands.gamemode;

import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;

public class gms extends Command {

//TO-DO: Add target player argument
    public gms() {
        super("gms");

        setDefaultExecutor((sender, context) -> {
            sender.sendMessage("You are now in survival mode");

            var senderPlayer = (Player) sender;
            senderPlayer.setGameMode(GameMode.SURVIVAL);
        });}}
