package com.gmail.trentech.pjp.commands.portal;

import com.gmail.trentech.helpme.help.Help;
import com.gmail.trentech.pjp.portal.Portal;
import com.gmail.trentech.pjp.portal.Portal.Server;
import flavor.pie.spongycord.SpongyCord;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class CMDDestination implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (!(src instanceof Player)) {
            throw new CommandException(Text.of(TextColors.RED, "Must be a player"), false);
        }
        Player player = (Player) src;

        if (!args.hasAny("name")) {
            Help help = Help.get("portal destination").get();
            throw new CommandException(Text.builder().onClick(TextActions.executeCallback(help.execute())).append(help.getUsageText()).build(),
                    false);
        }
        Portal portal = args.<Portal>getOne("name").get();

        if (!args.hasAny("destination")) {
            Help help = Help.get("portal destination").get();
            throw new CommandException(Text.builder().onClick(TextActions.executeCallback(help.execute())).append(help.getUsageText()).build(),
                    false);
        }
        String destination = args.<String>getOne("destination").get();

        if (portal instanceof Portal.Server) {
            Portal.Server server = (Server) portal;

            Consumer<List<String>> consumer1 = (list) -> {
                if (!list.contains(destination)) {
                    try {
                        throw new CommandException(Text.of(TextColors.RED, destination, " does not exist"), false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                Consumer<String> consumer2 = (s) -> {
                    if (destination.equalsIgnoreCase(s)) {
                        try {
                            throw new CommandException(Text.of(TextColors.RED, "Destination cannot be the server you are currently on"), false);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };

                SpongyCord.API.getServerName(consumer2, player);
            };

            SpongyCord.API.getServerList(consumer1, player);

            server.setServer(destination);
        } else {
            Portal.Local local = (Portal.Local) portal;

            Optional<World> world = Sponge.getServer().getWorld(destination);

            if (!world.isPresent()) {
                throw new CommandException(Text.of(TextColors.RED, destination, " is not loaded or does not exist"), false);
            }

            local.setWorld(world.get());

            if (args.hasAny("location")) {
                Location location = args.<Location>getOne("location").get();

                local.setVector3d(location.getPosition());
            }
        }

        portal.update();

        src.sendMessage(Text.of(TextColors.DARK_GREEN, "Portal destination updated"));

        return CommandResult.success();
    }

}
