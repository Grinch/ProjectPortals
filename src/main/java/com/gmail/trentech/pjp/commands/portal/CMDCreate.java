package com.gmail.trentech.pjp.commands.portal;

import com.flowpowered.math.vector.Vector3d;
import com.gmail.trentech.helpme.help.Help;
import com.gmail.trentech.pjp.effects.Particle;
import com.gmail.trentech.pjp.effects.ParticleColor;
import com.gmail.trentech.pjp.effects.Particles;
import com.gmail.trentech.pjp.listeners.LegacyListener;
import com.gmail.trentech.pjp.listeners.PortalListener;
import com.gmail.trentech.pjp.portal.LegacyBuilder;
import com.gmail.trentech.pjp.portal.Portal;
import com.gmail.trentech.pjp.portal.Portal.PortalType;
import com.gmail.trentech.pjp.portal.Properties;
import com.gmail.trentech.pjp.rotation.Rotation;
import com.gmail.trentech.pjp.utils.ConfigManager;
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
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.world.World;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class CMDCreate implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (!(src instanceof Player)) {
            throw new CommandException(Text.of(TextColors.RED, "Must be a player"), false);
        }
        Player player = (Player) src;

        if (!args.hasAny("name")) {
            Help help = Help.get("portal create").get();
            throw new CommandException(Text.builder().onClick(TextActions.executeCallback(help.execute())).append(help.getUsageText()).build(),
                    false);
        }
        String name = args.<String>getOne("name").get().toLowerCase();

        if (Portal.get(name, PortalType.PORTAL).isPresent()) {
            throw new CommandException(Text.of(TextColors.RED, name, " already exists"), false);
        }

        String destination = args.<String>getOne("destination").get();

        Optional<Vector3d> vector3d = Optional.empty();
        AtomicReference<Rotation> rotation = new AtomicReference<>(Rotation.EAST);
        AtomicReference<Double> price = new AtomicReference<>(0.0);
        boolean bedRespawn = false;
        AtomicReference<Particle> particle = new AtomicReference<>(Particles.getDefaultEffect("portal"));
        AtomicReference<Optional<ParticleColor>> color = new AtomicReference<>(Particles.getDefaultColor("portal", particle.get().isColorable()));

        if (args.hasAny("price")) {
            price.set(args.<Double>getOne("price").get());
        }

        if (args.hasAny("particle")) {
            particle.set(args.<Particles>getOne("particle").get().getParticle());

            if (args.hasAny("color")) {
                color.set(Optional.of(args.<ParticleColor>getOne("color").get()));
            }
        }

        if (args.hasAny("b")) {
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

                    Portal.Server server = new Portal.Server(PortalType.PORTAL, destination, rotation.get(), price.get());
                    Properties properties = new Properties(particle.get(), color.get());
                    server.setProperties(properties);
                    server.setName(name);

                    if (ConfigManager.get().getConfig().getNode("options", "portal", "legacy_builder").getBoolean()) {
                        LegacyListener.builders.put(player.getUniqueId(), new LegacyBuilder(server));
                        player.sendMessage(
                                Text.builder().color(TextColors.DARK_GREEN).append(Text.of("Begin building your portal frame, followed by "))
                                        .onClick(TextActions.runCommand("/pjp:portal save"))
                                        .append(Text.of(TextColors.YELLOW, TextStyles.UNDERLINE, "/portal save")).build());
                    } else {
                        PortalListener.builders.put(player.getUniqueId(), server);

                        player.sendMessage(Text.of(TextColors.DARK_GREEN, "Right click bottom with empty hand similar to vanilla nether portals "));
                    }

                    player.sendMessage(Text.builder().color(TextColors.DARK_GREEN).append(Text.of("Begin building your portal frame, followed by "))
                            .onClick(TextActions.runCommand("/pjp:portal save"))
                            .append(Text.of(TextColors.YELLOW, TextStyles.UNDERLINE, "/portal save")).build());
                };

                SpongyCord.API.getServerName(consumer2, player);
            };

            SpongyCord.API.getServerList(consumer1, player);
        } else {
            Optional<World> world = Sponge.getServer().getWorld(destination);

            if (!world.isPresent()) {
                throw new CommandException(Text.of(TextColors.RED, destination, " is not loaded or does not exist"), false);
            }

            if (args.hasAny("x,y,z")) {
                String[] coords = args.<String>getOne("x,y,z").get().split(",");

                if (coords[0].equalsIgnoreCase("random")) {
                    vector3d = Optional.of(new Vector3d(0, 0, 0));
                } else if (coords[0].equalsIgnoreCase("bed")) {
                    bedRespawn = true;
                } else {
                    try {
                        vector3d = Optional.of(
                                new Vector3d(Double.parseDouble(coords[0]), Double.parseDouble(coords[1]), Double.parseDouble(coords[2])));
                    } catch (Exception e) {
                        throw new CommandException(Text.of(TextColors.RED, coords.toString(), " is not valid"), true);
                    }
                }
            }

            if (args.hasAny("rotation")) {
                rotation.set(args.<Rotation>getOne("direction").get());
            }

            Portal.Local local = new Portal.Local(PortalType.PORTAL, world.get(), vector3d, rotation.get(), price.get(), bedRespawn);
            Properties properties = new Properties(particle.get(), color.get());
            local.setProperties(properties);
            local.setName(name);

            if (ConfigManager.get().getConfig().getNode("options", "portal", "legacy_builder").getBoolean()) {
                LegacyListener.builders.put(player.getUniqueId(), new LegacyBuilder(local));
                player.sendMessage(Text.builder().color(TextColors.DARK_GREEN).append(Text.of("Begin building your portal frame, followed by "))
                        .onClick(TextActions.runCommand("/pjp:portal save")).append(Text.of(TextColors.YELLOW, TextStyles.UNDERLINE, "/portal save"))
                        .build());
            } else {
                PortalListener.builders.put(player.getUniqueId(), local);
                player.sendMessage(Text.of(TextColors.DARK_GREEN, "Right click bottom with empty hand similar to vanilla nether portals "));
            }
        }

        return CommandResult.success();
    }

}
