package com.github.alantr7.torus.math;

import org.bukkit.block.BlockFace;

public enum Direction {

    NORTH(0, 0, -1, 0, 0),
    EAST(1, 0, 0, 90, 0),
    SOUTH(0, 0, 1, 180, 0),
    WEST(-1, 0, 0, 270, 0),
    UP(0, 1, 0, 0, 90),
    DOWN(0, -1, 0, 0, 180),
    ;

    public final int modX;
    public final int modY;
    public final int modZ;
    public final int rotH;
    public final int rotV;

    Direction(int modX, int modY, int modZ, int rotH, int rotV) {
        this.modX = modX;
        this.modY = modY;
        this.modZ = modZ;
        this.rotH = rotH;
        this.rotV = rotV;
    }

    public int mask() {
        return (int) Math.pow(2, ordinal());
    }

    public Direction getOpposite() {
        return switch (this) {
            case UP -> DOWN;
            case DOWN -> UP;

            case EAST -> WEST;
            case WEST -> EAST;

            case NORTH -> SOUTH;
            case SOUTH -> NORTH;
        };
    }

    public Direction getRight() {
        return switch (this) {
            case NORTH -> EAST;
            case EAST -> SOUTH;
            case SOUTH -> WEST;
            default -> NORTH;
        };
    }

    public Direction getLeft() {
        return getRight().getOpposite();
    }

    public static Direction fromBlockFace(BlockFace face) {
        return switch (face) {
            case NORTH -> NORTH;
            case EAST -> EAST;
            case SOUTH -> SOUTH;
            case WEST -> WEST;
            case UP -> UP;
            case DOWN -> DOWN;
            default -> null;
        };
    }

}
