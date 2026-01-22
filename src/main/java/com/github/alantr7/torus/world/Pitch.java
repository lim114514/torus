package com.github.alantr7.torus.world;

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

}
