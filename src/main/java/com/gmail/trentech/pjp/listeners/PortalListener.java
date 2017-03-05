package com.gmail.trentech.pjp.listeners;

import com.flowpowered.math.vector.Vector3d;
import com.gmail.trentech.pjp.Main;
import com.gmail.trentech.pjp.events.ConstructPortalEvent;
import com.gmail.trentech.pjp.portal.Portal;
import com.gmail.trentech.pjp.portal.Portal.PortalType;
import com.gmail.trentech.pjp.rotation.PlayerRotation;
import com.gmail.trentech.pjp.utils.ConfigManager;
import com.gmail.trentech.pjp.utils.Teleport;
import com.gmail.trentech.pjp.utils.Timings;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.filter.type.Exclude;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

public class PortalListener {

    public static ConcurrentHashMap<UUID, Portal> builders = new ConcurrentHashMap<>();

    private Timings timings;

    public PortalListener(Timings timings) {
        this.timings = timings;
    }

    @Listener
    public void onConnectionEvent(ClientConnectionEvent.Login event, @Root Player player) {
        Location<World> location = event.getToTransform().getLocation();

        while (Portal.get(location, PortalType.PORTAL).isPresent() || Portal.get(location, PortalType.DOOR).isPresent()) {
            ThreadLocalRandom random = ThreadLocalRandom.current();

            int x = (random.nextInt(3 * 2) - 3) + location.getBlockX();
            int z = (random.nextInt(3 * 2) - 3) + location.getBlockZ();

            Optional<Location<World>> optionalLocation =
                    Sponge.getGame().getTeleportHelper().getSafeLocation(location.getExtent().getLocation(x, location.getBlockY(), z));

            if (optionalLocation.isPresent()) {
                location = optionalLocation.get();
                event.setToTransform(new Transform<World>(location));
            }
        }
    }

    @Listener
    @Exclude(value = {ChangeBlockEvent.Place.class})
    public void onInteractBlockEventSecondary(InteractBlockEvent.Secondary event, @Root Player player) {
        timings.onInteractBlockEventSecondary().startTiming();

        try {
            if (!builders.containsKey(player.getUniqueId())) {
                return;
            }
            Portal portal = builders.get(player.getUniqueId());

            if (player.getItemInHand(HandTypes.MAIN_HAND).isPresent()) {
                player.sendMessage(Text.of(TextColors.YELLOW, "Hand must be empty"));
                return;
            }

            Optional<Location<World>> optionalLocation = event.getTargetBlock().getLocation();

            if (!optionalLocation.isPresent()) {
                return;
            }
            Location<World> location = optionalLocation.get();

            Direction direction = PlayerRotation.getClosest(player.getRotation().getFloorY()).getDirection();

            com.gmail.trentech.pjp.portal.PortalBuilder builder = new com.gmail.trentech.pjp.portal.PortalBuilder(portal, location, direction);

            if (!builder.spawnPortal()) {
                player.sendMessage(Text.of(TextColors.DARK_RED, "Not a valid portal shape"));
                return;
            }

            builders.remove(player.getUniqueId());

            player.sendMessage(Text.of(TextColors.DARK_GREEN, "Portal ", portal.getName(), " created successfully"));
        } finally {
            timings.onInteractBlockEventSecondary().stopTiming();
        }
    }

    @Listener
    public void onConstructPortalEvent(ConstructPortalEvent event, @Root Player player) {
        timings.onConstructPortalEvent().startTiming();

        try {
            List<Location<World>> locations = event.getLocations();

            for (Location<World> location : event.getLocations()) {
                if (Portal.get(location, PortalType.PORTAL).isPresent()) {
                    player.sendMessage(Text.of(TextColors.DARK_RED, "Portals cannot over lap other portals"));
                    event.setCancelled(true);
                    return;
                }
            }

            ConfigurationNode config = ConfigManager.get().getConfig();

            int size = config.getNode("options", "portal", "size").getInt();
            if (locations.size() > size) {
                player.sendMessage(Text.of(TextColors.DARK_RED, "Portals cannot be larger than ", size, " blocks"));
                event.setCancelled(true);
                return;
            }

            if (locations.size() < 9) {
                player.sendMessage(Text.of(TextColors.DARK_RED, "Portal too small"));
                event.setCancelled(true);
                return;
            }
        } finally {
            timings.onConstructPortalEvent().stopTiming();
        }
    }

    @Listener
    public void onMoveEntityEventItem(MoveEntityEvent event, @Getter("getTargetEntity") Item item) {
        timings.onMoveEntityEvent().startTiming();

        try {
            Location<World> location = item.getLocation();

            Optional<Portal> optionalPortal = Portal.get(location, PortalType.PORTAL);

            if (!optionalPortal.isPresent()) {
                return;
            }
            Portal portal = optionalPortal.get();

            if (portal instanceof Portal.Server) {
                return;
            }
            Portal.Local local = (Portal.Local) portal;

            if (!ConfigManager.get().getConfig().getNode("options", "portal", "teleport_item").getBoolean()) {
                return;
            }

            Optional<Location<World>> optionalSpawnLocation = local.getLocation();

            if (!optionalSpawnLocation.isPresent()) {
                return;
            }
            Location<World> spawnLocation = optionalSpawnLocation.get();

            Vector3d rotation = portal.getRotation().toVector3d();

            event.setToTransform(new Transform<World>(spawnLocation.getExtent(), spawnLocation.getPosition(), rotation));
        } finally {
            timings.onMoveEntityEvent().stopTiming();
        }
    }

    @Listener
    public void onMoveEntityEventLiving(MoveEntityEvent event, @Getter("getTargetEntity") Living living) {
        if (living instanceof Player) {
            return;
        }

        timings.onMoveEntityEvent().startTiming();

        try {
            Location<World> location = living.getLocation();

            Optional<Portal> optionalPortal = Portal.get(location, PortalType.PORTAL);

            if (!optionalPortal.isPresent()) {
                return;
            }
            Portal portal = optionalPortal.get();

            if (portal instanceof Portal.Server) {
                return;
            }
            Portal.Local local = (Portal.Local) portal;

            if (!ConfigManager.get().getConfig().getNode("options", "portal", "teleport_mob").getBoolean()) {
                return;
            }

            Optional<Location<World>> optionalSpawnLocation = local.getLocation();

            if (!optionalSpawnLocation.isPresent()) {
                return;
            }
            Location<World> spawnLocation = optionalSpawnLocation.get();

            Vector3d rotation = portal.getRotation().toVector3d();

            event.setToTransform(new Transform<World>(spawnLocation.getExtent(), spawnLocation.getPosition(), rotation));
        } finally {
            timings.onMoveEntityEvent().stopTiming();
        }
    }

    private static List<UUID> cache = new ArrayList<>();

    @Listener(order = Order.FIRST)
    public void onMoveEntityEventPlayer(MoveEntityEvent event, @Getter("getTargetEntity") Player player) {
        timings.onMoveEntityEvent().startTiming();

        try {
            Location<World> location = event.getFromTransform().getLocation();

            Optional<Portal> optionalPortal = Portal.get(location, PortalType.PORTAL);

            if (!optionalPortal.isPresent()) {
                return;
            }
            Portal portal = optionalPortal.get();

            if (ConfigManager.get().getConfig().getNode("options", "advanced_permissions").getBoolean()) {
                if (!player.hasPermission("pjp.portal." + portal.getName())) {
                    player.sendMessage(Text.of(TextColors.DARK_RED, "You do not have permission to use this portal"));
                    return;
                }
            } else {
                if (!player.hasPermission("pjp.portal.interact")) {
                    player.sendMessage(Text.of(TextColors.DARK_RED, "You do not have permission to use portals"));
                    return;
                }
            }

            UUID uuid = player.getUniqueId();

            if (cache.contains(uuid)) {
                return;
            }
            cache.add(uuid);

            Teleport.teleport(player, portal);

            Sponge.getScheduler().createTaskBuilder().delayTicks(20).execute(c -> {
                cache.remove(uuid);
            }).submit(Main.getPlugin());
        } finally {
            timings.onMoveEntityEvent().stopTiming();
        }
    }

    @Listener
    public void onChangeBlockEventPlace(ChangeBlockEvent.Place event, @Root Player player) {
        timings.onChangeBlockEventPlace().startTiming();

        try {
            for (Transaction<BlockSnapshot> transaction : event.getTransactions()) {
                Location<World> location = transaction.getFinal().getLocation().get();

                if (!Portal.get(location, PortalType.PORTAL).isPresent()) {
                    continue;
                }

                event.setCancelled(true);
                break;
            }
        } finally {
            timings.onChangeBlockEventPlace().stopTiming();
        }
    }

    @Listener
    public void onChangeBlockEventBreak(ChangeBlockEvent.Break event, @Root Player player) {
        timings.onChangeBlockEventBreak().startTiming();

        try {
            for (Transaction<BlockSnapshot> transaction : event.getTransactions()) {
                Location<World> location = transaction.getFinal().getLocation().get();

                if (!Portal.get(location, PortalType.PORTAL).isPresent()) {
                    continue;
                }

                event.setCancelled(true);
                break;
            }
        } finally {
            timings.onChangeBlockEventBreak().stopTiming();
        }
    }
}
