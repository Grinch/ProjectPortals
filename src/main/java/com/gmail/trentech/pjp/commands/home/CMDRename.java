package com.gmail.trentech.pjp.commands.home;

import com.gmail.trentech.helpme.help.Help;
import com.gmail.trentech.pjp.data.Keys;
import com.gmail.trentech.pjp.data.mutable.HomeData;
import com.gmail.trentech.pjp.portal.Portal;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CMDRename implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (!(src instanceof Player)) {
            throw new CommandException(Text.of(TextColors.RED, "Must be a player"));
        }
        Player player = (Player) src;

        if (!args.hasAny("oldName")) {
            Help help = Help.get("home rename").get();
            throw new CommandException(Text.builder().onClick(TextActions.executeCallback(help.execute())).append(help.getUsageText()).build(),
                    false);
        }
        String oldName = args.<String>getOne("oldName").get().toLowerCase();

        Map<String, Portal> list = new HashMap<>();

        Optional<Map<String, Portal>> optionalList = player.get(Keys.PORTALS);

        if (optionalList.isPresent()) {
            list = optionalList.get();
        }

        if (!list.containsKey(oldName)) {
            throw new CommandException(Text.of(TextColors.RED, oldName, " does not exist"));
        }
        Portal.Local local = (Portal.Local) list.get(oldName);

        if (!args.hasAny("newName")) {
            Help help = Help.get("home rename").get();
            throw new CommandException(Text.builder().onClick(TextActions.executeCallback(help.execute())).append(help.getUsageText()).build(),
                    false);
        }
        String newName = args.<String>getOne("newName").get().toLowerCase();

        if (list.containsKey(newName)) {
            throw new CommandException(Text.of(TextColors.RED, newName, " already exists"), false);
        }

        list.remove(oldName);
        list.put(newName, local);

        DataTransactionResult result = player.offer(new HomeData(list));

        if (!result.isSuccessful()) {
            throw new CommandException(Text.of(TextColors.RED, "Could not rename ", oldName), false);
        } else {
            player.sendMessage(Text.of(TextColors.DARK_GREEN, "Home renamed to ", newName));
        }

        return CommandResult.success();
    }

}
