package com.github.alantr7.torus.structure.component;

import com.github.alantr7.torus.world.BlockLocation;
import com.github.alantr7.torus.world.Direction;
import com.github.alantr7.torus.model.Model;
import com.github.alantr7.torus.structure.StructureInstance;
import lombok.Getter;
import lombok.Setter;

public class StructureComponent {

    public final String name;

    public final BlockLocation absoluteLocation;

    public final BlockLocation relativeLocation;

    public final Direction direction;

    @Getter @Setter
    protected Model model;

    public StructureComponent(String name, BlockLocation absoluteLocation, BlockLocation relativeLocation, Direction direction, Model model) {
        this.name = name;
        this.absoluteLocation = absoluteLocation;
        this.relativeLocation = relativeLocation;
        this.direction = direction;
        this.model = model;
    }

    public StructureComponent(StructureInstance structure, BlockLocation relativeLocation, String name, Model model) {
        this.absoluteLocation = structure.location.getRelative(relativeLocation);
        this.relativeLocation = relativeLocation;
        this.name = name;
        this.direction = structure.direction;
        this.model = model;
    }

}
