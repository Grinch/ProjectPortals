package com.gmail.trentech.pjp.rotation;

import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.util.Direction;

public enum PlayerRotation {

    EAST(Direction.EAST, -90),
    NORTH(Direction.NORTH, -180),
    WEST(Direction.WEST, 90),
    SOUTH(Direction.SOUTH, 0);

    private final Direction direction;
    private final int value;

    private PlayerRotation(Direction direction, int value) {
        this.direction = direction;
        this.value = value;
    }

    public Direction getDirection() {
        return direction;
    }

    public int getValue() {
        return value;
    }

    public Vector3d toVector3d() {
        return new Vector3d(0, getValue(), 0);
    }

    public static PlayerRotation getClosest(int value) {
        PlayerRotation[] directions = PlayerRotation.values();

        int distance = Math.abs(directions[0].getValue() - value);
        int index = 0;
        for (int i = 1; i < directions.length; i++) {
            int cdistance = Math.abs(directions[i].getValue() - value);
            if (cdistance < distance) {
                index = i;
                distance = cdistance;
            }
        }

        return directions[index];
    }
}
