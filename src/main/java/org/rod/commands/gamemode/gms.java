package org.rod.commands.gamemode;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;

public class gms extends Command {

//TO-DO: Add target player argument
    public gms() {
        super("gms");

        setDefaultExecutor((sender, context) -> {
            sender.sendMessage(Component.text("You are now in survival mode")
                    .color(NamedTextColor.GREEN));

            var senderPlayer = (Player) sender;
            senderPlayer.setGameMode(GameMode.SURVIVAL);
        });}}
