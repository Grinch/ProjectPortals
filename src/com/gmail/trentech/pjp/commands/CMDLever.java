package com.gmail.trentech.pjp.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.gmail.trentech.pjp.Main;
import com.gmail.trentech.pjp.listeners.LeverListener;
import com.gmail.trentech.pjp.utils.ConfigManager;
import com.gmail.trentech.pjp.utils.Help;
import com.gmail.trentech.pjp.utils.Utils;

public class CMDLever implements CommandExecutor {

	public CMDLever(){
		String alias = new ConfigManager().getConfig().getNode("Options", "Command-Alias", "lever").getString();
		
		Help help = new Help("lever", "lever", " Use this command to create a lever that will teleport you to other worlds");
		help.setSyntax(" /lever <world> [x] [y] [z]\n /" + alias + " <world> [x] [y] [z]");
		help.setExample(" /lever MyWorld\n /lever MyWorld -100 65 254\n /lever MyWorld random");
		help.save();
	}
	
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if(!(src instanceof Player)){
			src.sendMessage(Text.of(TextColors.DARK_RED, "Must be a player"));
			return CommandResult.empty();
		}
		Player player = (Player) src;
		
		if(!args.hasAny("name")) {
			src.sendMessage(Text.of(TextColors.YELLOW, "/lever <world> [x] [y] [z]"));
			return CommandResult.empty();
		}
		String worldName = Utils.getBaseName(args.<String>getOne("name").get());

		if(!Main.getGame().getServer().getWorld(worldName).isPresent()){
			src.sendMessage(Text.of(TextColors.DARK_RED, Utils.getPrettyName(worldName), " does not exist"));
			return CommandResult.empty();
		}

		String destination;
		
		if(args.hasAny("coords")) {
			String coords = args.<String>getOne("coords").get();
			if(coords.equalsIgnoreCase("random")){
				destination = worldName + ":random";
			}else{
				String[] testInt = coords.split(" ");
				try{
					Integer.parseInt(testInt[0]);
					Integer.parseInt(testInt[1]);
					Integer.parseInt(testInt[2]);
				}catch(Exception e){
					src.sendMessage(Text.of(TextColors.YELLOW, "/lever <world> [x] [y] [z]"));
					return CommandResult.empty();
				}
				destination = worldName + ":" + testInt[0] + "." + testInt[1] + "." + testInt[2];
			}
		}else{
			destination = worldName + ":spawn";
		}
		
		LeverListener.creators.put(player, destination);
		
		player.sendMessage(Text.of(TextColors.DARK_GREEN, "Place button to create button portal"));

		return CommandResult.success();
	}
}