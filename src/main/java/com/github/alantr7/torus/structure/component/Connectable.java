package com.github.alantr7.torus.structure.component;

import com.github.alantr7.torus.world.Direction;

public interface Connectable {

    int getAllowedConnections();

    int getConnections();

    void setConnections(int connections);

    default boolean isConnected(Direction direction) {
        return (getConnections() & direction.mask()) != 0;
    }

    default boolean isConnectableFrom(Direction direction) {
        return (getAllowedConnections() & direction.mask()) != 0;
    }

    default Direction[] getValidConnectionsDirections() {
        Direction[] values = new Direction[Direction.values().length];
        int count = 0;

        for (Direction dir : Direction.values()) {
            if (isConnectableFrom(dir)) {
                values[count++] = dir;
            }
        }

        Direction[] stripped = new Direction[count];
        System.arraycopy(values, 0, stripped, 0, count);

        return stripped;
    }

    default void setConnected(Direction direction, boolean connected) {
        if (connected) {
            setConnections(getConnections() | direction.mask());
        } else {
            setConnections(getConnections() & ~direction.mask());
        }
    }

}
