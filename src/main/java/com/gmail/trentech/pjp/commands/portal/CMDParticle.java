package com.gmail.trentech.pjp.commands.portal;

import com.gmail.trentech.helpme.help.Help;
import com.gmail.trentech.pjp.effects.Particle;
import com.gmail.trentech.pjp.effects.ParticleColor;
import com.gmail.trentech.pjp.effects.Particles;
import com.gmail.trentech.pjp.portal.Portal;
import com.gmail.trentech.pjp.portal.Properties;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

public class CMDParticle implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (!args.hasAny("name")) {
            Help help = Help.get("portal particle").get();
            throw new CommandException(Text.builder().onClick(TextActions.executeCallback(help.execute())).append(help.getUsageText()).build(),
                    false);
        }
        Portal portal = args.<Portal>getOne("name").get();

        if (!args.hasAny("particle")) {
            Help help = Help.get("portal particle").get();
            throw new CommandException(Text.builder().onClick(TextActions.executeCallback(help.execute())).append(help.getUsageText()).build(),
                    false);
        }
        Particle particle = args.<Particles>getOne("particle").get().getParticle();

        Optional<ParticleColor> color = Optional.empty();

        if (args.hasAny("color")) {
            if (particle.isColorable()) {
                color = Optional.of(args.<ParticleColor>getOne("color").get());
            } else {
                src.sendMessage(Text.of(TextColors.YELLOW, "Colors currently only works with REDSTONE type"));
            }
        }

        Properties properties = portal.getProperties().get();
        properties.setParticle(particle);
        properties.setParticleColor(color);

        portal.setProperties(properties);
        portal.update();

        return CommandResult.success();
    }

}
