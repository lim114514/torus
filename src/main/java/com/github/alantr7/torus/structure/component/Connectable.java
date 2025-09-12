package com.github.alantr7.torus.structure.component;

import com.github.alantr7.torus.math.Direction;

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

    default void setConnected(Direction direction, boolean connected) {
        if (connected) {
            setConnections(getConnections() | direction.mask());
        } else {
            setConnections(getConnections() & ~direction.mask());
        }
    }

}
