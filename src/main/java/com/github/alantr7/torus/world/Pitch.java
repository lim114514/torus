package com.github.alantr7.torus.world;

import com.github.alantr7.torus.structure.builder.StructureSocketDef;
import com.github.alantr7.torus.utils.MathUtils;

public enum Pitch {

    FORWARD(0), UP(90), DOWN(-90);

    public final int rotV;

    Pitch(int rotV) {
        this.rotV = rotV;
    }

    public Direction transform(Direction ref, Direction forward) {
        if (this == FORWARD)
            return forward;

        Direction direction;

        if (ref == forward) {
            direction = Direction.UP;
        }
        else if (ref.getOpposite() == forward) {
            direction = Direction.DOWN;
        }
        else if (Direction.UP == forward) {
            direction = ref.getOpposite();
        } else {
            direction = ref;
        }

        return this == UP ? direction : direction.getOpposite();
    }

    public int transform(StructureSocketDef socketDef, Direction direction) {
        int allowedConnectionsOriginal = socketDef.allowedConnections();
        int allowedConnections;
        if (this != Pitch.FORWARD) {
            allowedConnections = 0;
            for (Direction possibleDirection : Direction.values()) {
                if (!MathUtils.hasFlag(allowedConnectionsOriginal, possibleDirection.mask()))
                    continue;

                if (possibleDirection != direction && possibleDirection != direction.getOpposite() && possibleDirection != Direction.UP && possibleDirection != Direction.DOWN) {
                    allowedConnections = MathUtils.setFlag(allowedConnections, possibleDirection.mask(), true);
                    continue;
                }

                allowedConnections = MathUtils.setFlag(allowedConnections, transform(direction, possibleDirection).mask(), true);
            }
            return allowedConnections;
        } else {
            return allowedConnectionsOriginal;
        }
    }

}
