package com.gmail.trentech.pjp.commands.warp;

import com.gmail.trentech.helpme.help.Help;
import com.gmail.trentech.pjp.portal.Portal;
import com.gmail.trentech.pjp.utils.Teleport;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class CMDWarp implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (args.hasAny("name")) {
            if (!(src instanceof Player)) {
                throw new CommandException(Text.of(TextColors.RED, "Must be a player"));
            }
            Player player = ((Player) src);

            Portal portal = args.<Portal>getOne("name").get();

            if (!player.hasPermission("pjp.warps." + portal.getName())) {
                throw new CommandException(Text.of(TextColors.RED, "you do not have permission to warp here"));
            }

            if (args.hasAny("player")) {
                if (!src.hasPermission("pjp.cmd.warp.others")) {
                    throw new CommandException(Text.of(TextColors.RED, "you do not have permission to warp others"));
                }

                player = args.<Player>getOne("player").get();
            }

            Teleport.teleport(player, portal);

            return CommandResult.success();
        }

        src.sendMessage(Text.of(TextColors.YELLOW, " /warp <name> [player]"));

        Help.executeList(src, Help.get("warp").get().getChildren());

        return CommandResult.success();
    }

}
