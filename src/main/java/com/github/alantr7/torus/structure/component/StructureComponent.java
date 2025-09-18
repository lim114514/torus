package com.github.alantr7.torus.structure.component;

import com.github.alantr7.torus.math.BlockLocation;
import com.github.alantr7.torus.math.Direction;
import com.github.alantr7.torus.structure.display.Model;
import com.github.alantr7.torus.structure.StructureInstance;
import lombok.Getter;
import lombok.Setter;

public class StructureComponent {

    public final StructureInstance structure;

    public final BlockLocation absoluteLocation;

    public final BlockLocation relativeLocation;

    public final Direction direction;

    @Getter @Setter
    protected Model model;

    public StructureComponent(StructureInstance structure, BlockLocation relativeLocation, Model model) {
        this.structure = structure;
        this.absoluteLocation = structure.location.getRelative(relativeLocation);
        this.relativeLocation = relativeLocation;
        this.direction = structure.direction;
        this.model = model;
    }

}
