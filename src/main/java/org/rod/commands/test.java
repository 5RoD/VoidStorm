package org.rod.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.utils.Range;


//Learning class test commands
public class test extends Command {

    public test() {
        super("test", "gg");


        setDefaultExecutor((sender, context) -> {

            Title title = Title.title(
                    Component.text("Hello Everybody")
                            .color(NamedTextColor.GOLD).decorate(TextDecoration.BOLD),
                    Component.text("hehe")
                            .color(NamedTextColor.RED));

            sender.showTitle(title);

        });


        //Fade in
        //Display
        //Fade out




        var titleText = ArgumentType.String("TitleText");
        var subTitle = ArgumentType.String("SubtitleText");

        addSyntax((sender, context) -> {

            var titleString = context.get(titleText);
            var subTitleString = context.get(subTitle);

            Title title = Title.title(Component.text(titleString)
                    .color(NamedTextColor.GOLD), Component.text(subTitleString)
                    .color(NamedTextColor.GRAY)
                    .decorate(TextDecoration.ITALIC));
            sender.showTitle(title);


        }, titleText, subTitle);


    }
}
