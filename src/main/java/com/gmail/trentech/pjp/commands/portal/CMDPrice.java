package com.gmail.trentech.pjp.commands.portal;

import com.gmail.trentech.helpme.help.Help;
import com.gmail.trentech.pjp.portal.Portal;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

public class CMDPrice implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (!args.hasAny("name")) {
            Help help = Help.get("portal price").get();
            throw new CommandException(Text.builder().onClick(TextActions.executeCallback(help.execute())).append(help.getUsageText()).build(),
                    false);
        }
        Portal portal = args.<Portal>getOne("name").get();

        if (!args.hasAny("price")) {
            Help help = Help.get("portal price").get();
            throw new CommandException(Text.builder().onClick(TextActions.executeCallback(help.execute())).append(help.getUsageText()).build(),
                    false);
        }
        double price = args.<Double>getOne("price").get();

        portal.setPrice(price);
        portal.update();

        src.sendMessage(Text.of(TextColors.DARK_GREEN, "Set price of portal ", portal.getName(), " to $", price));

        return CommandResult.success();
    }
}
