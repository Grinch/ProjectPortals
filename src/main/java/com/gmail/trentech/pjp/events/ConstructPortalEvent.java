package com.gmail.trentech.pjp.events;

import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.impl.AbstractEvent;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.List;

public class ConstructPortalEvent extends AbstractEvent implements Cancellable {

    private boolean cancelled = false;

    private final Cause cause;
    private final List<Location<World>> locations;

    public ConstructPortalEvent(List<Location<World>> frame, List<Location<World>> fill, Cause cause) {
        this.cause = cause;
        frame.addAll(fill);
        this.locations = frame;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public Cause getCause() {
        return cause;
    }

    public List<Location<World>> getLocations() {
        return locations;
    }
}
