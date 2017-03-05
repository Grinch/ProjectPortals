package com.gmail.trentech.pjp.commands.portal;

import com.gmail.trentech.helpme.help.Help;
import com.gmail.trentech.pjp.portal.Portal;
import com.gmail.trentech.pjp.portal.Portal.PortalType;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

public class CMDRename implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (!args.hasAny("oldName")) {
            Help help = Help.get("portal rename").get();
            throw new CommandException(Text.builder().onClick(TextActions.executeCallback(help.execute())).append(help.getUsageText()).build(),
                    false);
        }
        Portal portal = args.<Portal>getOne("oldName").get();

        if (!args.hasAny("newName")) {
            Help help = Help.get("portal rename").get();
            throw new CommandException(Text.builder().onClick(TextActions.executeCallback(help.execute())).append(help.getUsageText()).build(),
                    false);
        }
        String newName = args.<String>getOne("newName").get().toLowerCase();

        if (Portal.get(newName, PortalType.PORTAL).isPresent()) {
            throw new CommandException(Text.of(TextColors.RED, newName, " already exists"), false);
        }

        portal.remove();
        portal.create(newName);

        src.sendMessage(Text.of(TextColors.DARK_GREEN, "Portal renamed to ", newName));

        return CommandResult.success();
    }

}
