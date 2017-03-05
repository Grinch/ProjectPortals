package com.gmail.trentech.pjp.portal;

import static com.gmail.trentech.pjp.data.DataQueries.BED_RESPAWN;
import static com.gmail.trentech.pjp.data.DataQueries.PORTAL_TYPE;
import static com.gmail.trentech.pjp.data.DataQueries.PRICE;
import static com.gmail.trentech.pjp.data.DataQueries.PROPERTIES;
import static com.gmail.trentech.pjp.data.DataQueries.ROTATION;
import static com.gmail.trentech.pjp.data.DataQueries.SERVER;
import static com.gmail.trentech.pjp.data.DataQueries.VECTOR3D;
import static com.gmail.trentech.pjp.data.DataQueries.WORLD;

import com.flowpowered.math.vector.Vector3d;
import com.gmail.trentech.pjp.effects.Particle;
import com.gmail.trentech.pjp.effects.Particles;
import com.gmail.trentech.pjp.rotation.Rotation;
import com.gmail.trentech.pjp.utils.SQLUtils;
import com.gmail.trentech.pjp.utils.Teleport;
import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataSerializable;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.MemoryDataContainer;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.DataContentUpdater;
import org.spongepowered.api.data.persistence.DataTranslators;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public abstract class Portal extends SQLUtils implements DataSerializable {

    private final PortalType type;
    private String name;
    private Rotation rotation = Rotation.EAST;
    private double price = 0;
    private Optional<Properties> properties = Optional.empty();

    private static ConcurrentHashMap<String, Portal> cache = new ConcurrentHashMap<>();

    protected Portal(PortalType type, Rotation rotation, double price) {
        this.type = type;
        this.rotation = rotation;
        this.price = price;
    }

    public static Optional<Portal> get(String name, PortalType type) {
        if (cache.containsKey(name)) {
            Portal portal = cache.get(name);

            if (portal.getType().equals(type)) {
                return Optional.of(cache.get(name));
            }
        }

        return Optional.empty();
    }

    public static Optional<Portal> get(Location<World> location, PortalType type) {
        if (type.equals(PortalType.PORTAL)) {
            for (Entry<String, Portal> entry : cache.entrySet()) {
                Portal portal = entry.getValue();

                if (!portal.getType().equals(type)) {
                    continue;
                }

                Properties properties = portal.getProperties().get();

                List<Location<World>> frame = properties.getFrame();

                if (!frame.get(0).getExtent().equals(location.getExtent())) {
                    continue;
                }

                for (Location<World> loc : frame) {
                    if (loc.getBlockPosition().equals(location.getBlockPosition())) {
                        return Optional.of(portal);
                    }
                }

                for (Location<World> loc : properties.getFill()) {
                    if (loc.getBlockPosition().equals(location.getBlockPosition())) {
                        return Optional.of(portal);
                    }
                }
            }
        }

        return get(location.getExtent().getName() + ":" + location.getBlockX() + "." + location.getBlockY() + "." + location.getBlockZ(), type);
    }

    public static List<Portal> all(PortalType type) {
        List<Portal> list = new ArrayList<>();

        for (Entry<String, Portal> entry : cache.entrySet()) {
            Portal portal = entry.getValue();

            if (portal.getType().equals(type)) {
                list.add(portal);
            }
        }

        return list;
    }

    public static void init() {
        try {
            Connection connection = getDataSource().getConnection();

            PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + getPrefix("PORTALS"));

            ResultSet result = statement.executeQuery();

            while (result.next()) {
                String name = result.getString("Name");

                Portal portal = deserialize(result.getString("Data"));
                portal.setName(name);

                cache.put(name, portal);

                if (portal.getProperties().isPresent()) {
                    Properties properties = portal.getProperties().get();
                    properties.getParticle().createTask(name, properties.getFill(), properties.getParticleColor());
                }
            }

            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void create(String name) {
        this.name = name;

        try {
            Connection connection = getDataSource().getConnection();

            PreparedStatement statement = connection.prepareStatement("INSERT into " + getPrefix("PORTALS") + " (Name, Data) VALUES (?, ?)");

            statement.setString(1, name);
            statement.setString(2, serialize(this));

            statement.executeUpdate();

            connection.close();

            cache.put(name, this);

            if (properties.isPresent()) {
                Properties properties = this.properties.get();
                properties.getParticle().createTask(name, properties.getFill(), properties.getParticleColor());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void create(Location<World> location) {
        name = location.getExtent().getName() + ":" + location.getBlockX() + "." + location.getBlockY() + "." + location.getBlockZ();

        create(name);

        Particle particle = Particles.getDefaultEffect("creation");
        particle.spawnParticle(location, false, Particles.getDefaultColor("creation", particle.isColorable()));
    }

    public void update() {
        try {
            Connection connection = getDataSource().getConnection();

            PreparedStatement statement = connection.prepareStatement("UPDATE " + getPrefix("PORTALS") + " SET Data = ? WHERE Name = ?");

            statement.setString(1, serialize(this));
            statement.setString(2, name);

            statement.executeUpdate();

            connection.close();

            cache.put(name, this);

            if (properties.isPresent()) {
                Properties properties = this.properties.get();

                for (Task task : Sponge.getScheduler().getScheduledTasks()) {
                    if (task.getName().equals(name)) {
                        task.cancel();
                        break;
                    }
                }
                properties.update(true);
                properties.getParticle().createTask(name, properties.getFill(), properties.getParticleColor());
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void remove() {
        try {
            Connection connection = getDataSource().getConnection();

            PreparedStatement statement = connection.prepareStatement("DELETE from " + getPrefix("PORTALS") + " WHERE Name = ?");

            statement.setString(1, name);
            statement.executeUpdate();

            connection.close();

            cache.remove(name);

            if (properties.isPresent()) {
                Properties properties = this.properties.get();

                for (Task task : Sponge.getScheduler().getScheduledTasks()) {
                    if (task.getName().equals(name)) {
                        task.cancel();
                        break;
                    }
                }
                properties.update(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public PortalType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Rotation getRotation() {
        return rotation;
    }

    public void setRotation(Rotation rotation) {
        this.rotation = rotation;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Optional<Properties> getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = Optional.of(properties);
    }

    public static class Server extends Portal {

        private String server;

        public Server(PortalType type, String server, Rotation rotation, double price) {
            super(type, rotation, price);
            this.server = server;
        }

        public String getServer() {
            return server;
        }

        public void setServer(String server) {
            this.server = server;
        }

        @Override
        public int getContentVersion() {
            return 0;
        }

        @Override
        public DataContainer toContainer() {
            DataContainer container =
                    new MemoryDataContainer().set(PORTAL_TYPE, getType().name()).set(SERVER, server).set(ROTATION, getRotation().getName())
                            .set(PRICE, getPrice());

            if (getProperties().isPresent()) {
                container.set(PROPERTIES, getProperties().get());
            }

            return container;
        }

        public static class Builder extends AbstractDataBuilder<Server> {

            public Builder() {
                super(Server.class, 0);
            }

            @Override
            protected Optional<Server> buildContent(DataView container) throws InvalidDataException {
                if (container.contains(PORTAL_TYPE, SERVER, ROTATION, PRICE)) {
                    PortalType type = PortalType.valueOf(container.getString(PORTAL_TYPE).get());
                    String server = container.getString(SERVER).get();
                    Rotation rotation = Rotation.get(container.getString(ROTATION).get()).get();
                    Double price = container.getDouble(PRICE).get();

                    Portal.Server portal = new Portal.Server(type, server, rotation, price);

                    if (container.contains(PROPERTIES)) {
                        portal.setProperties(container.getSerializable(PROPERTIES, Properties.class).get());
                    }

                    return Optional.of(portal);
                }

                return Optional.empty();
            }
        }
    }

    public static class Local extends Portal {

        private World world;
        private Optional<Vector3d> vector3d;
        private boolean bedSpawn;

        public Local(PortalType type, World world, Optional<Vector3d> vector3d, Rotation rotation, double price, boolean bedSpawn) {
            super(type, rotation, price);
            this.world = world;
            this.vector3d = vector3d;
            this.bedSpawn = bedSpawn;
        }

        public World getWorld() {
            return world;
        }

        public void setWorld(World world) {
            this.world = world;
        }

        public Optional<Vector3d> getVector3d() {
            return vector3d;
        }

        public void setVector3d(Vector3d vector3d) {
            this.vector3d = Optional.of(vector3d);
        }

        public boolean isBedSpawn() {
            return bedSpawn;
        }

        public void setBedSpawn(boolean bedSpawn) {
            this.bedSpawn = bedSpawn;
        }

        public Optional<Location<World>> getLocation() {
            if (vector3d.isPresent()) {
                Vector3d vector3d = this.vector3d.get();

                if (vector3d.getX() == 0 && vector3d.getY() == 0 && vector3d.getZ() == 0) {
                    return Teleport.getRandomLocation(world);
                } else {
                    return Optional.of(new Location<World>(world, vector3d));
                }
            } else {
                return Optional.of(world.getSpawnLocation());
            }
        }


        @Override
        public int getContentVersion() {
            return 1;
        }

        @Override
        public DataContainer toContainer() {
            DataContainer container =
                    new MemoryDataContainer().set(PORTAL_TYPE, getType().name()).set(WORLD, world.getName()).set(ROTATION, getRotation().getName())
                            .set(PRICE, getPrice()).set(BED_RESPAWN, isBedSpawn());

            if (getProperties().isPresent()) {
                container.set(PROPERTIES, getProperties().get());
            }

            if (vector3d.isPresent()) {
                Vector3d vector3d = this.vector3d.get();
                container.set(VECTOR3D, DataTranslators.VECTOR_3_D.translate(vector3d));
            }

            return container;
        }

        public static class Update1 implements DataContentUpdater {

            @Override
            public int getInputVersion() {
                return 0;
            }

            @Override
            public int getOutputVersion() {
                return 1;
            }

            @Override
            public DataView update(DataView container) {
                if (!container.contains(BED_RESPAWN)) {
                    container.set(BED_RESPAWN, false);
                }

                return container;
            }

        }

        public static class Builder extends AbstractDataBuilder<Local> {

            public Builder() {
                super(Local.class, 0);
            }

            @Override
            protected Optional<Local> buildContent(DataView container) throws InvalidDataException {
                if (container.contains(PORTAL_TYPE, WORLD, ROTATION, PRICE)) {
                    Optional<World> optionalWorld = Sponge.getServer().getWorld(container.getString(WORLD).get());

                    if (!optionalWorld.isPresent()) {
                        return Optional.empty();
                    }

                    PortalType type = PortalType.valueOf(container.getString(PORTAL_TYPE).get());
                    World world = optionalWorld.get();
                    Optional<Vector3d> vector3d = Optional.empty();
                    Rotation rotation = Rotation.get(container.getString(ROTATION).get()).get();
                    Double price = container.getDouble(PRICE).get();
                    boolean bedRespawn = false;

                    if (container.contains(BED_RESPAWN)) {
                        bedRespawn = container.getBoolean(BED_RESPAWN).get();
                    }

                    if (container.contains(VECTOR3D)) {
                        vector3d = Optional.of(DataTranslators.VECTOR_3_D.translate(container.getView(VECTOR3D).get()));
                    }

                    Portal.Local portal = new Portal.Local(type, world, vector3d, rotation, price, bedRespawn);

                    if (container.contains(PROPERTIES)) {
                        portal.setProperties(container.getSerializable(PROPERTIES, Properties.class).get());
                    }

                    return Optional.of(portal);
                }

                return Optional.empty();
            }
        }
    }

    public enum PortalType {
        BUTTON,
        DOOR,
        HOME,
        LEVER,
        PLATE,
        PORTAL,
        SIGN,
        WARP;
    }

    public static String serialize(Portal portal) {
        try {
            StringWriter sink = new StringWriter();
            HoconConfigurationLoader loader = HoconConfigurationLoader.builder().setSink(() -> new BufferedWriter(sink)).build();
            ConfigurationNode node = loader.createEmptyNode();
            if (portal instanceof Server) {
                node.setValue(TypeToken.of(Server.class), ((Server) portal));
            } else {
                node.setValue(TypeToken.of(Local.class), ((Local) portal));
            }

            loader.save(node);
            return sink.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Portal deserialize(String item) {
        try {
            StringReader source = new StringReader(item);
            HoconConfigurationLoader loader = HoconConfigurationLoader.builder().setSource(() -> new BufferedReader(source)).build();
            ConfigurationNode node = loader.load();
            try {
                return node.getValue(TypeToken.of(Local.class));
            } catch (Exception e) {
                return node.getValue(TypeToken.of(Server.class));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
