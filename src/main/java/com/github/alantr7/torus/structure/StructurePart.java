package com.github.alantr7.torus.structure;

import com.github.alantr7.torus.world.BlockLocation;
import com.github.alantr7.torus.world.Direction;

public class StructurePart {

    public final String name;

    public final BlockLocation absoluteLocation;

    public final BlockLocation relativeLocation;

    public final Direction direction;

    public StructurePart(String name, BlockLocation absoluteLocation, BlockLocation relativeLocation, Direction direction) {
        this.name = name;
        this.absoluteLocation = absoluteLocation;
        this.relativeLocation = relativeLocation;
        this.direction = direction;
    }

    public StructurePart(StructureInstance structure, BlockLocation relativeLocation, String name) {
        this.absoluteLocation = structure.location.getRelative(relativeLocation);
        this.relativeLocation = relativeLocation;
        this.name = name;
        this.direction = structure.direction;
    }

}
