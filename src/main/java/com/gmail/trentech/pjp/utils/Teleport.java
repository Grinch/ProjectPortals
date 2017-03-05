package com.gmail.trentech.pjp.utils;

import com.flowpowered.math.vector.Vector3d;
import com.gmail.trentech.pjp.events.TeleportEvent;
import com.gmail.trentech.pjp.events.TeleportEvent.Local;
import com.gmail.trentech.pjp.portal.Portal;
import flavor.pie.spongycord.SpongyCord;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.util.RespawnLocation;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.TeleportHelper;
import org.spongepowered.api.world.World;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class Teleport {

    private static ThreadLocalRandom random = ThreadLocalRandom.current();

    public static Optional<Location<World>> getSafeLocation(Location<World> location) {
        TeleportHelper teleportHelper = Sponge.getGame().getTeleportHelper();

        first:
        for (int i = 0; i < 10; i++) {
            Optional<Location<World>> optionalLocation = teleportHelper.getSafeLocation(location);

            if (!optionalLocation.isPresent()) {
                continue;
            }
            Location<World> unsafeLocation = optionalLocation.get();

            BlockType blockType = unsafeLocation.getBlockType();

            if (!blockType.equals(BlockTypes.AIR) || !unsafeLocation.getRelative(Direction.UP).getBlockType().equals(BlockTypes.AIR)) {
                continue;
            }

            Location<World> floorLocation = unsafeLocation.getRelative(Direction.DOWN);

            for (int i2 = 0; i2 < 3; i2++) {
                BlockType floorBlockType = floorLocation.getBlockType();

                if (floorBlockType.equals(BlockTypes.WATER) || floorBlockType.equals(BlockTypes.LAVA) || floorBlockType
                        .equals(BlockTypes.FLOWING_WATER) || floorBlockType.equals(BlockTypes.FLOWING_LAVA) || floorBlockType
                        .equals(BlockTypes.FIRE)) {
                    continue first;
                }
                floorLocation = floorLocation.getRelative(Direction.DOWN);
            }

            unsafeLocation.getExtent().loadChunk(unsafeLocation.getChunkPosition(), true);

            return optionalLocation;
        }

        return Optional.empty();
    }

    public static Optional<Location<World>> getRandomLocation(World world) {
        Location<World> spawnLocation = world.getSpawnLocation();

        int radius = ConfigManager.get().getConfig().getNode("options", "random_spawn_radius").getInt() / 2;

        for (int i = 0; i < 20; i++) {
            double x = (random.nextDouble() * (radius * 2) - radius) + spawnLocation.getBlockX();
            double y = random.nextDouble(59, 200 + 1);
            double z = (random.nextDouble() * (radius * 2) - radius) + spawnLocation.getBlockZ();

            Optional<Location<World>> optionalLocation = getSafeLocation(world.getLocation(x, y, z));

            if (!optionalLocation.isPresent()) {
                continue;
            }
            return optionalLocation;
        }

        return Optional.empty();
    }

    public static Consumer<CommandSource> unsafe(Location<World> location) {
        return (CommandSource src) -> {
            Player player = (Player) src;
            player.setLocation(location);
        };
    }

    public static boolean teleport(Player player, Portal portal) {
        AtomicReference<Boolean> bool = new AtomicReference<>(false);

        if (portal instanceof Portal.Server) {
            Portal.Server server = (Portal.Server) portal;

            Consumer<String> consumer = (serverName) -> {
                TeleportEvent.Server teleportEvent =
                        new TeleportEvent.Server(player, serverName, server.getServer(), server.getPrice(), Cause.of(NamedCause.source(server)));

                if (!Sponge.getEventManager().post(teleportEvent)) {
                    SpongyCord.API.connectPlayer(player, teleportEvent.getDestination());

                    player.setLocation(player.getWorld().getSpawnLocation());

                    bool.set(true);
                }
            };

            SpongyCord.API.getServerName(consumer, player);
        } else {
            Portal.Local local = (Portal.Local) portal;

            if (local.isBedSpawn()) {
                Optional<Map<UUID, RespawnLocation>> optionalLocations = player.get(Keys.RESPAWN_LOCATIONS);

                if (optionalLocations.isPresent()) {
                    Map<UUID, RespawnLocation> respawnLocations = optionalLocations.get();

                    if (respawnLocations.containsKey(local.getWorld().getUniqueId())) {
                        Optional<Location<World>> optionalLocation = respawnLocations.get(local.getWorld().getUniqueId()).asLocation();

                        if (optionalLocation.isPresent()) {
                            Location<World> spawnLocation = optionalLocation.get();

                            Local teleportEvent = new TeleportEvent.Local(player, player.getLocation(), spawnLocation, local.getPrice(),
                                    Cause.of(NamedCause.source(local)));

                            if (!Sponge.getEventManager().post(teleportEvent)) {
                                spawnLocation = teleportEvent.getDestination();

                                Vector3d rotation = local.getRotation().toVector3d();

                                player.setLocationAndRotation(spawnLocation, rotation);

                                return true;
                            }
                        }
                    }
                }
            }

            Optional<Location<World>> optionalSpawnLocation = local.getLocation();

            if (optionalSpawnLocation.isPresent()) {
                Location<World> spawnLocation = optionalSpawnLocation.get();

                Local teleportEvent =
                        new TeleportEvent.Local(player, player.getLocation(), spawnLocation, local.getPrice(), Cause.of(NamedCause.source(local)));

                if (!Sponge.getEventManager().post(teleportEvent)) {
                    spawnLocation = teleportEvent.getDestination();

                    Vector3d rotation = local.getRotation().toVector3d();

                    player.setLocationAndRotation(spawnLocation, rotation);

                    bool.set(true);
                }
            } else {
                player.sendMessage(Text.of(TextColors.RED, "Could not find location"));
            }
        }

        return bool.get();
    }
}
