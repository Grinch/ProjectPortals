package com.gmail.trentech.pjp.listeners;

import com.gmail.trentech.pjp.portal.LegacyBuilder;
import com.gmail.trentech.pjp.portal.Portal;
import com.gmail.trentech.pjp.portal.Portal.PortalType;
import com.gmail.trentech.pjp.utils.Timings;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class LegacyListener {

    public static ConcurrentHashMap<UUID, LegacyBuilder> builders = new ConcurrentHashMap<>();

    private Timings timings;

    public LegacyListener(Timings timings) {
        this.timings = timings;
    }

    @Listener
    public void onChangeBlockEventPlace(ChangeBlockEvent.Place event, @Root Player player) {
        timings.onChangeBlockEventPlace().startTiming();

        try {
            if (!builders.containsKey(player.getUniqueId())) {
                for (Transaction<BlockSnapshot> transaction : event.getTransactions()) {
                    Location<World> location = transaction.getFinal().getLocation().get();

                    if (!Portal.get(location, PortalType.PORTAL).isPresent()) {
                        continue;
                    }

                    event.setCancelled(true);
                    break;
                }
                return;
            }
            LegacyBuilder builder = builders.get(player.getUniqueId());

            for (Transaction<BlockSnapshot> transaction : event.getTransactions()) {
                if (transaction.getFinal().getState().getType().equals(BlockTypes.FIRE)) {
                    event.setCancelled(true);
                    break;
                }

                Location<World> location = transaction.getFinal().getLocation().get();

                if (builder.isFill()) {
                    builder.addFill(location);
                } else {
                    builder.addFrame(location);
                }
            }
        } finally {
            timings.onChangeBlockEventPlace().stopTiming();
        }
    }

    @Listener
    public void onChangeBlockEventBreak(ChangeBlockEvent.Break event, @Root Player player) {
        timings.onChangeBlockEventBreak().startTiming();

        try {
            if (!builders.containsKey(player.getUniqueId())) {
                for (Transaction<BlockSnapshot> transaction : event.getTransactions()) {
                    Location<World> location = transaction.getFinal().getLocation().get();

                    if (!Portal.get(location, PortalType.PORTAL).isPresent()) {
                        continue;
                    }

                    event.setCancelled(true);
                    break;
                }
                return;
            }
            LegacyBuilder builder = builders.get(player.getUniqueId());

            for (Transaction<BlockSnapshot> transaction : event.getTransactions()) {
                Location<World> location = transaction.getFinal().getLocation().get();
                if (builder.isFill()) {
                    builder.removeFill(location);
                } else {
                    builder.removeFrame(location);
                }
            }
        } finally {
            timings.onChangeBlockEventBreak().stopTiming();
        }
    }
}
