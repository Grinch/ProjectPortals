package com.gmail.trentech.pjp.commands;

import com.gmail.trentech.helpme.help.Help;
import com.gmail.trentech.pjp.utils.ConfigManager;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;

import java.util.ArrayList;
import java.util.List;

public class CMDPjp implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        ConfigurationNode node = ConfigManager.get().getConfig().getNode("settings", "modules");

        if (Sponge.getPluginManager().isLoaded("helpme")) {
            List<Help> commands = new ArrayList<>();

            if (node.getNode("portals").getBoolean()) {
                commands.add(Help.get("portal").get());
            }
            if (node.getNode("plates").getBoolean()) {
                commands.add(Help.get("plate").get());
            }
            if (node.getNode("levers").getBoolean()) {
                commands.add(Help.get("lever").get());
            }
            if (node.getNode("signs").getBoolean()) {
                commands.add(Help.get("sign").get());
            }
            if (node.getNode("doors").getBoolean()) {
                commands.add(Help.get("door").get());
            }
            if (node.getNode("buttons").getBoolean()) {
                commands.add(Help.get("button").get());
            }
            if (node.getNode("homes").getBoolean()) {
                commands.add(Help.get("home").get());
            }
            if (node.getNode("warps").getBoolean()) {
                commands.add(Help.get("warp").get());
            }
            if (node.getNode("back").getBoolean()) {
                commands.add(Help.get("back").get());
            }

            Help.executeList(src, commands);

            return CommandResult.success();
        }

        return CommandResult.success();
    }

}
