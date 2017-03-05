package com.gmail.trentech.pjp.listeners;

import com.gmail.trentech.pjp.Main;
import com.gmail.trentech.pjp.portal.Portal;
import com.gmail.trentech.pjp.portal.Portal.PortalType;
import com.gmail.trentech.pjp.utils.ConfigManager;
import com.gmail.trentech.pjp.utils.Teleport;
import com.gmail.trentech.pjp.utils.Timings;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class DoorListener {

    public static ConcurrentHashMap<UUID, Portal> builders = new ConcurrentHashMap<>();

    private Timings timings;

    public DoorListener(Timings timings) {
        this.timings = timings;
    }

    @Listener
    public void onChangeBlockEventBreak(ChangeBlockEvent.Break event, @Root Player player) {
        timings.onChangeBlockEventBreak().startTiming();

        try {
            for (Transaction<BlockSnapshot> transaction : event.getTransactions()) {
                Location<World> location = transaction.getFinal().getLocation().get();

                Optional<Portal> optionalPortal = Portal.get(location, PortalType.DOOR);

                if (!optionalPortal.isPresent()) {
                    continue;
                }
                Portal portal = optionalPortal.get();

                if (!player.hasPermission("pjp.door.break")) {
                    player.sendMessage(Text.of(TextColors.DARK_RED, "you do not have permission to break door portals"));
                    event.setCancelled(true);
                } else {
                    portal.remove();
                    player.sendMessage(Text.of(TextColors.DARK_GREEN, "Broke door portal"));
                }
            }
        } finally {
            timings.onChangeBlockEventBreak().stopTiming();
        }
    }

    @Listener(order = Order.POST)
    public void onChangeBlockEventPlace(ChangeBlockEvent.Place event, @Root Player player) {
        timings.onChangeBlockEventPlace().startTiming();

        try {
            if (!builders.containsKey(player.getUniqueId())) {
                return;
            }

            for (Transaction<BlockSnapshot> transaction : event.getTransactions()) {
                Location<World> location = transaction.getFinal().getLocation().get();

                Optional<Boolean> optionalOpen = location.get(Keys.OPEN);

                if (!optionalOpen.isPresent()) {
                    continue;
                }

                if (!player.hasPermission("pjp.door.place")) {
                    player.sendMessage(Text.of(TextColors.DARK_RED, "you do not have permission to place door portals"));
                    builders.remove(player.getUniqueId());
                    event.setCancelled(true);
                    return;
                }

                Portal portal = builders.get(player.getUniqueId());
                portal.create(location);

                player.sendMessage(Text.of(TextColors.DARK_GREEN, "New door portal created"));

                builders.remove(player.getUniqueId());
                break;
            }
        } finally {
            timings.onChangeBlockEventPlace().stopTiming();
        }
    }

    private static List<UUID> cache = new ArrayList<>();

    @Listener
    public void onMoveEntityEventPlayer(MoveEntityEvent event, @Getter("getTargetEntity") Player player) {
        timings.onMoveEntityEvent().startTimingIfSync();

        try {
            Location<World> location = player.getLocation();

            Optional<Boolean> optionalOpen = location.get(Keys.OPEN);

            if (!optionalOpen.isPresent()) {
                return;
            }

            if (!optionalOpen.get()) {
                return;
            }

            Optional<Portal> optionalPortal = Portal.get(location, PortalType.DOOR);

            if (!optionalPortal.isPresent()) {
                return;
            }
            Portal portal = optionalPortal.get();

            if (ConfigManager.get().getConfig().getNode("options", "advanced_permissions").getBoolean()) {
                if (!player.hasPermission(
                        "pjp.door." + location.getExtent().getName() + "_" + location.getBlockX() + "_" + location.getBlockY() + "_" + location
                                .getBlockZ())) {
                    player.sendMessage(Text.of(TextColors.DARK_RED, "You do not have permission to use this door portal"));
                    return;
                }
            } else {
                if (!player.hasPermission("pjp.door.interact")) {
                    player.sendMessage(Text.of(TextColors.DARK_RED, "you do not have permission to interact with door portals"));
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
}
