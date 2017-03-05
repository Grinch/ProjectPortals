package com.gmail.trentech.pjp.portal;

import static com.gmail.trentech.pjp.data.DataQueries.VECTOR3D;
import static com.gmail.trentech.pjp.data.DataQueries.WORLD;

import com.flowpowered.math.vector.Vector3d;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataSerializable;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.MemoryDataContainer;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.DataTranslators;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Optional;

public class LocationSerializable implements DataSerializable {

    private World world;
    private Vector3d vector3d;

    protected LocationSerializable(World world, Vector3d vector3d) {
        this.world = world;
        this.vector3d = vector3d;
    }

    protected LocationSerializable(Location<World> location) {
        this.world = location.getExtent();
        this.vector3d = location.getPosition();
    }

    public Location<World> getLocation() {
        return new Location<>(world, vector3d);
    }

    @Override
    public int getContentVersion() {
        return 0;
    }

    @Override
    public DataContainer toContainer() {
        return new MemoryDataContainer().set(VECTOR3D, DataTranslators.VECTOR_3_D.translate(vector3d)).set(WORLD, world.getName());
    }

    public static class Builder extends AbstractDataBuilder<LocationSerializable> {

        public Builder() {
            super(LocationSerializable.class, 0);
        }

        @Override
        protected Optional<LocationSerializable> buildContent(DataView container) throws InvalidDataException {
            if (container.contains(WORLD, VECTOR3D)) {
                Optional<World> optionalWorld = Sponge.getServer().getWorld(container.getString(WORLD).get());

                if (!optionalWorld.isPresent()) {
                    return Optional.empty();
                }
                World world = optionalWorld.get();

                Vector3d vector3d = DataTranslators.VECTOR_3_D.translate(container.getView(VECTOR3D).get());

                return Optional.of(new LocationSerializable(world, vector3d));
            }

            return Optional.empty();
        }
    }

    public static String serialize(Location<World> location) {
        ConfigurationNode node = DataTranslators.CONFIGURATION_NODE.translate(new LocationSerializable(location).toContainer());

        StringWriter stringWriter = new StringWriter();
        try {
            HoconConfigurationLoader.builder().setSink(() -> new BufferedWriter(stringWriter)).build().save(node);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return stringWriter.toString();
    }

    public static Location<World> deserialize(String item) {
        ConfigurationNode node = null;
        try {
            node = HoconConfigurationLoader.builder().setSource(() -> new BufferedReader(new StringReader(item))).build().load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        DataView dataView = DataTranslators.CONFIGURATION_NODE.translate(node);

        return Sponge.getDataManager().deserialize(LocationSerializable.class, dataView).get().getLocation();
    }
}
