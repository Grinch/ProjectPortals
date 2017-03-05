package com.gmail.trentech.pjp.rotation;

import com.flowpowered.math.vector.Vector3d;

import java.util.Optional;

public enum Rotation {

    SOUTH_EAST("southeast", 315),
    EAST("east", 270),
    NORTH_EAST("northeast", 225),
    NORTH("north", 180),
    NORTH_WEST("northwest", 135),
    WEST("west", 90),
    SOUTH_WEST("southwest", 45),
    SOUTH("south", 0);

    private final String name;
    private final int value;

    private Rotation(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }

    public Vector3d toVector3d() {
        return new Vector3d(0, getValue(), 0);
    }

    public static Optional<Rotation> get(String name) {
        Optional<Rotation> optional = Optional.empty();

        Rotation[] rotations = Rotation.values();

        for (Rotation rotation : rotations) {
            if (rotation.getName().equals(name.toLowerCase())) {
                optional = Optional.of(rotation);
                break;
            }
        }

        return optional;
    }

    public static Rotation getClosest(int value) {
        Rotation[] rotations = Rotation.values();

        int distance = Math.abs(rotations[0].getValue() - value);
        int index = 0;
        for (int i = 1; i < rotations.length; i++) {
            int cdistance = Math.abs(rotations[i].getValue() - value);
            if (cdistance < distance) {
                index = i;
                distance = cdistance;
            }
        }

        return rotations[index];
    }
}
