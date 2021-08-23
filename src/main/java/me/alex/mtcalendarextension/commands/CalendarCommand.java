package me.alex.mtcalendarextension.commands;

import me.alex.mtcalendarextension.CalendarExtension;
import me.alex.mtcalendarextension.builder.CalendarInventoryHolderBuilder;
import me.alex.mtcalendarextension.inventories.CalendarInventoryHolder;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.Template;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;

public class CalendarCommand extends Command {

    private final CalendarExtension extension;


    public CalendarCommand(@NotNull String name, CalendarExtension extension) {
        super(name);
        this.extension = extension;

        this.setDefaultExecutor((sender, context) -> {
            sender.sendMessage("Please use: /calendar open <player>");
        });

        var open = ArgumentType.Literal("open");
        var player = ArgumentType.Entity("players").onlyPlayers(true).singleEntity(true);
        var date = ArgumentType.Integer("date").between(0, 12);
        var aligned = ArgumentType.Boolean("aligned");

        aligned.setCallback((sender, exception) -> {
            sender.sendMessage("This is not a boolean!");
        });

        date.setCallback((sender, exception) -> {
            sender.sendMessage("That is not between 1-12 or it is not a number!");
        });

        player.setCallback((sender, exception) -> {
            sender.sendMessage(MiniMessage.get().parse("<gradient:dark_red:red>The Player <p> is not a valid player!</gradient>", Template.of("p", exception.getInput())));
        });

        this.addSyntax((sender, context) -> {
            Player p = context.get(player).findFirstPlayer(sender);
            if (p == null) {
                sender.sendMessage(MiniMessage.get().parse("<gradient:dark_red:red>The Player <p> is not a valid player!</gradient>", Template.of("p", context.getRaw(player))));
                return;
            }
            computeInput(p, 1, context.get(aligned));
        }, open, player, aligned);

        this.addSyntax((sender, context) -> {
            Player p = context.get(player).findFirstPlayer(sender);
            if (p == null) {
                sender.sendMessage(MiniMessage.get().parse("<gradient:dark_red:red>The Player <p> is not a valid player!</gradient>", Template.of("p", context.getRaw(player))));
                return;
            }
            computeInput(p, context.get(date), context.get(aligned));
        }, open, player, aligned, date);
    }

    private void computeInput(Player firstPlayer, Integer intRange, Boolean aligned) {
        extension.getContainer().get(firstPlayer.getUuid()).ifPresentOrElse(CalendarInventoryHolder::open, () -> {
            CalendarInventoryHolder holder = new CalendarInventoryHolderBuilder(firstPlayer.getUuid()).setAligned(aligned).setLocaleDate(LocalDate.now().withMonth(intRange)).build();
            extension.getContainer().add(holder);
            extension.getInventory().initInventory(holder);
            holder.open();
        });
    }
}
