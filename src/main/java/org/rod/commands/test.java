package org.rod.commands;

import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.Argument;
import net.minestom.server.command.builder.arguments.ArgumentType;

public class test extends Command {

    public test() {
            super("test", "gg");



        setDefaultExecutor((sender, context) -> {

         sender.sendMessage("test bro");
        });


        var manAmount = ArgumentType.Integer("manAmount");
        addSyntax((sender, context) -> {
            int amount = context.get("manAmount");

            for (var i = 0; i < amount; i++) {
                sender.sendMessage("Lets gooo");


            }
        }, manAmount);}}
