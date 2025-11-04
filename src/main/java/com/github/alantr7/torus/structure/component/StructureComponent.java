package com.github.alantr7.torus.structure.component;

import com.github.alantr7.torus.world.BlockLocation;
import com.github.alantr7.torus.world.Direction;
import com.github.alantr7.torus.model.PartModel;
import com.github.alantr7.torus.structure.StructureInstance;
import lombok.Getter;
import lombok.Setter;

public class StructureComponent {

    public final String name;

    public final BlockLocation absoluteLocation;

    public final BlockLocation relativeLocation;

    public final Direction direction;

    public StructureComponent(String name, BlockLocation absoluteLocation, BlockLocation relativeLocation, Direction direction) {
        this.name = name;
        this.absoluteLocation = absoluteLocation;
        this.relativeLocation = relativeLocation;
        this.direction = direction;
    }

    public StructureComponent(StructureInstance structure, BlockLocation relativeLocation, String name) {
        this.absoluteLocation = structure.location.getRelative(relativeLocation);
        this.relativeLocation = relativeLocation;
        this.name = name;
        this.direction = structure.direction;
    }

}
