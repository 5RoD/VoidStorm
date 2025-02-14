package org.rod.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.damage.Damage;
import net.minestom.server.network.packet.server.ServerPacket;
import net.minestom.server.network.packet.server.play.EntityStatusPacket;
import net.minestom.server.utils.time.TimeUnit;

// Learning class test commands
public class test extends Command {

    public test() {
        super("test", "gg");


        setDefaultExecutor((sender, context) -> {

            var senderPlayer = (Player) sender;


            var scheduler = MinecraftServer.getSchedulerManager();
//weird learn
            scheduler.buildTask(() -> {

                Title title = Title.title(
                        Component.text("Hello Everybody")
                                .color(NamedTextColor.GOLD).decorate(TextDecoration.BOLD),
                        Component.text("hehe")
                                .color(NamedTextColor.RED));
                senderPlayer.showTitle(title);


            });
        });
    }
}


//
//
//        //Fade in
//        //Display
//        //Fade out
//
//
//
//


