package com.gmail.trentech.pjp.commands.home;

import com.gmail.trentech.helpme.help.Help;
import com.gmail.trentech.pjp.data.Keys;
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
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CMDHome implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (!(src instanceof Player)) {
            throw new CommandException(Text.of(TextColors.RED, "Must be a player"));
        }
        Player player = (Player) src;

        if (args.hasAny("name")) {
            String name = args.<String>getOne("name").get().toLowerCase();

            Map<String, Portal> list = new HashMap<>();

            Optional<Map<String, Portal>> optionalList = player.get(Keys.PORTALS);

            if (optionalList.isPresent()) {
                list = optionalList.get();
            }

            if (!list.containsKey(name)) {
                throw new CommandException(Text.of(TextColors.RED, name, " does not exist"));
            }
            Portal.Local local = (Portal.Local) list.get(name);

            Optional<Location<World>> optionalLocation = local.getLocation();

            if (!optionalLocation.isPresent()) {
                throw new CommandException(Text.of(TextColors.RED, name, " has invalid location"));
            }

            if (args.hasAny("player")) {
                if (!src.hasPermission("pjp.cmd.home.others")) {
                    throw new CommandException(Text.of(TextColors.RED, "you do not have permission to warp others"));
                }

                player = args.<Player>getOne("player").get();
            }

            Teleport.teleport(player, local);

            return CommandResult.success();
        }

        src.sendMessage(Text.of(TextColors.YELLOW, " /home <name> [player]"));

        Help.executeList(src, Help.get("home").get().getChildren());

        return CommandResult.success();
    }

}
