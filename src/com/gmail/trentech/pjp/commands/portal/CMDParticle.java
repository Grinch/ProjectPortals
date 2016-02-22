package com.gmail.trentech.pjp.commands.portal;

import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.effect.particle.ParticleType.Colorable;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import com.gmail.trentech.pjp.portals.Portal;
import com.gmail.trentech.pjp.utils.ConfigManager;
import com.gmail.trentech.pjp.utils.Help;
import com.gmail.trentech.pjp.utils.Particle;
import com.gmail.trentech.pjp.utils.Particles;

public class CMDParticle implements CommandExecutor {

	public CMDParticle(){
		String alias = new ConfigManager().getConfig().getNode("settings", "commands", "portal").getString();
		
		Help help = new Help("particle", "particle", " change a portals particle effect. Color currently only available for REDSTONE");
		help.setSyntax(" /portal particle <name> <type> [color]\n /" + alias + " p <name> <type> [color]");
		help.setExample(" /portal particle MyPortal CRIT\n /portal particle MyPortal REDSTONE BLUE");
		help.save();
	}
	
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if(!(src instanceof Player)){
			src.sendMessage(Text.of(TextColors.DARK_RED, "Must be a player"));
			return CommandResult.empty();
		}

		if(!args.hasAny("name")) {
			src.sendMessage(invalidArg());
			return CommandResult.empty();
		}
		String name = args.<String>getOne("name").get();

		if(!Portal.getByName(name).isPresent()){
			src.sendMessage(Text.of(TextColors.DARK_RED, name, " does not exist"));
			return CommandResult.empty();
		}	
		Portal portal = Portal.getByName(name).get();
		
		if(!args.hasAny("type")) {
			src.sendMessage(invalidArg());
			return CommandResult.empty();
		}
		String type = args.<String>getOne("type").get().toUpperCase();
		
		Optional<Particles> optionalParticle = Particles.get(type);
		
		if(!optionalParticle.isPresent()) {
			src.sendMessage(Text.of(TextColors.DARK_RED, "Invalid particle"));
			src.sendMessage(invalidArg());
			return CommandResult.empty();
		}
		Particles particle = optionalParticle.get();
		
		if(args.hasAny("color")) {
			if(particle.getType() instanceof Colorable){
				String color = args.<String>getOne("color").get().toUpperCase();
	    		if(Particle.getColor(color).isPresent()){
	    			type = type + ":" + color;
	    		}else{
	    			src.sendMessage(Text.of(TextColors.DARK_RED, "Invalid color"));
	    			src.sendMessage(invalidArg());
	    			return CommandResult.empty();
	    		}
			}else{
				src.sendMessage(Text.of(TextColors.YELLOW, "Colors currently only works with REDSTONE type"));
			}
		}

		portal.setParticle(type);

		return CommandResult.success();
	}
	
	private Text invalidArg(){
		Text t1 = Text.of(TextColors.YELLOW, "/portal particle <name> ");
		Text t2 = Text.builder().color(TextColors.YELLOW).onHover(TextActions.showText(Text.of("CLOUD\nCRIT\nCRIT_MAGIC\nENCHANTMENT_TABLE\nFLAME\nHEART\nNOTE\nPORTAL"
				+ "\nREDSTONE\nSLIME\nSNOWBALL\nSNOW_SHOVEL\nSMOKE_LARGE\nSPELL\nSPELL_WITCH\nSUSPENDED_DEPTH\nVILLAGER_HAPPY\nWATER_BUBBLE\nWATER_DROP\nWATER_SPLASH\nWATER_WAKE"))).append(Text.of("<type> ")).build();
		Text t3 = Text.builder().color(TextColors.YELLOW).onHover(TextActions.showText(Text.of("REDSTONE ONLY\n", TextColors.DARK_GRAY, "BLACK\n", TextColors.GRAY, "GRAY\n", TextColors.WHITE, "WHITE\n",
				TextColors.BLUE, "BLUE\n", TextColors.GREEN, "GREEN\n", TextColors.GREEN, "LIME\n", TextColors.RED, "RED\n", TextColors.YELLOW, "YELLOW\n", TextColors.LIGHT_PURPLE, "MAGENTA\n",
				TextColors.DARK_PURPLE, "PURPLE\n", TextColors.DARK_AQUA, "DARK_CYAN\n", TextColors.DARK_GREEN, "DARK_GREEN\n", TextColors.DARK_PURPLE, "DARK_MAGENTA\n",
				TextColors.AQUA, "CYAN\n", TextColors.DARK_BLUE, "NAVY\n", TextColors.LIGHT_PURPLE, "PINK\n",
				TextColors.RED,"R",TextColors.YELLOW,"A",TextColors.GREEN,"I",TextColors.BLUE,"N",TextColors.DARK_PURPLE,"B",TextColors.RED,"O",TextColors.YELLOW,"W")))
				.append(Text.of("[color]")).build();
		return Text.of(t1,t2,t3);
	}

}