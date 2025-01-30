package org.rod.commands;

import net.minestom.server.command.builder.Command;

public class test extends Command {

    public test() {
        super("test", "gg");



        setDefaultExecutor((sender, context) -> {

         sender.sendMessage("test bro");
        });

    }
}
