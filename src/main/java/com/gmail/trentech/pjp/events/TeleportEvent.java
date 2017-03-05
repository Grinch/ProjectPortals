package com.gmail.trentech.pjp.events;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.impl.AbstractEvent;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class TeleportEvent extends AbstractEvent implements Cancellable {

    private final Player player;
    private final Cause cause;
    private double price;
    private boolean cancelled = false;

    public TeleportEvent(Player player, Cause cause, double price) {
        this.player = player;
        this.cause = cause;
        this.price = price;
    }

    public Player getPlayer() {
        return player;
    }

    public double getPrice() {
        return price;
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

    public static class Local extends TeleportEvent {

        private final Location<World> source;
        private Location<World> destination;

        public Local(Player player, Location<World> source, Location<World> destination, double price, Cause cause) {
            super(player, cause, price);

            this.source = source;
            this.setDestination(destination);
        }

        public Location<World> getDestination() {
            return destination;
        }

        public void setDestination(Location<World> destination) {
            this.destination = destination;
        }

        public Location<World> getSource() {
            return source;
        }
    }

    public static class Server extends TeleportEvent {

        private final String source;
        private String destination;

        public Server(Player player, String source, String destination, double price, Cause cause) {
            super(player, cause, price);

            this.source = source;
            this.destination = destination;
        }

        public String getDestination() {
            return destination;
        }

        public void setDestination(String destination) {
            this.destination = destination;
        }

        public String getSource() {
            return source;
        }

    }

}
