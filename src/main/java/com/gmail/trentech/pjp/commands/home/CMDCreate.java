package com.gmail.trentech.pjp.commands.home;

import com.gmail.trentech.helpme.help.Help;
import com.gmail.trentech.pjp.data.Keys;
import com.gmail.trentech.pjp.data.mutable.HomeData;
import com.gmail.trentech.pjp.portal.Portal;
import com.gmail.trentech.pjp.portal.Portal.PortalType;
import com.gmail.trentech.pjp.rotation.Rotation;
import com.gmail.trentech.pjp.utils.ConfigManager;
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
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CMDCreate implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (!(src instanceof Player)) {
            throw new CommandException(Text.of(TextColors.RED, "Must be a player"), false);
        }
        Player player = (Player) src;

        if (!args.hasAny("name")) {
            Help help = Help.get("home create").get();
            throw new CommandException(Text.builder().onClick(TextActions.executeCallback(help.execute())).append(help.getUsageText()).build(),
                    false);
        }
        String name = args.<String>getOne("name").get().toLowerCase();

        Map<String, Portal> list = new HashMap<>();

        Optional<Map<String, Portal>> optionalList = player.get(Keys.PORTALS);

        if (optionalList.isPresent()) {
            list = optionalList.get();
        }

        int defaultAmount = ConfigManager.get().getConfig().getNode("options", "homes").getInt();

        int amount = list.size();

        int extra = 0;
        for (int i = 1; i <= 100; i++) {
            if (player.hasPermission("pjp.homes." + i)) {
                extra = i;
                break;
            }
        }

        if (!player.hasPermission("pjp.homes.unlimited")) {
            if (amount >= (defaultAmount + extra)) {
                throw new CommandException(Text.of(TextColors.RED, "You have reached the maximum number of homes you can have"), false);
            }
            amount++;
        }

        if (list.containsKey(name)) {
            throw new CommandException(Text.of(TextColors.RED, name, " already exists."), false);
        }

        Location<World> location = player.getLocation();

        list.put(name, new Portal.Local(PortalType.HOME, location.getExtent(), Optional.of(location.getPosition()),
                Rotation.getClosest(player.getRotation().getFloorY()), 0, false));

        DataTransactionResult result = player.offer(new HomeData(list));

        if (!result.isSuccessful()) {
            throw new CommandException(Text.of(TextColors.RED, "Could not create ", name), false);
        } else {
            player.sendMessage(Text.of(TextColors.DARK_GREEN, "Home ", name, " create"));
        }

        return CommandResult.success();
    }
}
