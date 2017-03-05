package com.gmail.trentech.pjp.commands;

import com.flowpowered.math.vector.Vector3d;
import com.gmail.trentech.pjp.listeners.ButtonListener;
import com.gmail.trentech.pjp.listeners.DoorListener;
import com.gmail.trentech.pjp.listeners.LeverListener;
import com.gmail.trentech.pjp.listeners.PlateListener;
import com.gmail.trentech.pjp.listeners.SignListener;
import com.gmail.trentech.pjp.portal.Portal;
import com.gmail.trentech.pjp.portal.Portal.PortalType;
import com.gmail.trentech.pjp.rotation.Rotation;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.World;

import java.util.Optional;

public class CMDObj {

    public static class Button extends CMDObjBase {

        public Button() {
            super("button");
        }

        @Override
        protected void init(Player player, Optional<String> server, Optional<World> world, Optional<Vector3d> vector3d, Rotation rotation,
                double price, boolean bedRespawn) {
            if (server.isPresent()) {
                ButtonListener.builders.put(player.getUniqueId(), new Portal.Server(PortalType.BUTTON, server.get(), rotation, price));
            } else {
                ButtonListener.builders
                        .put(player.getUniqueId(), new Portal.Local(PortalType.BUTTON, world.get(), vector3d, rotation, price, bedRespawn));
            }
        }
    }

    public static class Door extends CMDObjBase {

        public Door() {
            super("door");
        }

        @Override
        protected void init(Player player, Optional<String> server, Optional<World> world, Optional<Vector3d> vector3d, Rotation rotation,
                double price, boolean bedRespawn) {
            if (server.isPresent()) {
                DoorListener.builders.put(player.getUniqueId(), new Portal.Server(PortalType.DOOR, server.get(), rotation, price));
            } else {
                DoorListener.builders
                        .put(player.getUniqueId(), new Portal.Local(PortalType.DOOR, world.get(), vector3d, rotation, price, bedRespawn));
            }
        }
    }

    public static class Lever extends CMDObjBase {

        public Lever() {
            super("lever");
        }

        @Override
        protected void init(Player player, Optional<String> server, Optional<World> world, Optional<Vector3d> vector3d, Rotation rotation,
                double price, boolean bedRespawn) {
            if (server.isPresent()) {
                LeverListener.builders.put(player.getUniqueId(), new Portal.Server(PortalType.LEVER, server.get(), rotation, price));
            } else {
                LeverListener.builders
                        .put(player.getUniqueId(), new Portal.Local(PortalType.LEVER, world.get(), vector3d, rotation, price, bedRespawn));
            }
        }
    }

    public static class Plate extends CMDObjBase {

        public Plate() {
            super("pressure plate");
        }

        @Override
        protected void init(Player player, Optional<String> server, Optional<World> world, Optional<Vector3d> vector3d, Rotation rotation,
                double price, boolean bedRespawn) {
            if (server.isPresent()) {
                PlateListener.builders.put(player.getUniqueId(), new Portal.Server(PortalType.PLATE, server.get(), rotation, price));
            } else {
                PlateListener.builders
                        .put(player.getUniqueId(), new Portal.Local(PortalType.PLATE, world.get(), vector3d, rotation, price, bedRespawn));
            }
        }
    }

    public static class Sign extends CMDObjBase {

        public Sign() {
            super("sign");
        }

        @Override
        protected void init(Player player, Optional<String> server, Optional<World> world, Optional<Vector3d> vector3d, Rotation rotation,
                double price, boolean bedRespawn) {
            if (server.isPresent()) {
                SignListener.builders.put(player.getUniqueId(), new Portal.Server(PortalType.SIGN, server.get(), rotation, price));
            } else {
                SignListener.builders
                        .put(player.getUniqueId(), new Portal.Local(PortalType.SIGN, world.get(), vector3d, rotation, price, bedRespawn));
            }
        }
    }
}