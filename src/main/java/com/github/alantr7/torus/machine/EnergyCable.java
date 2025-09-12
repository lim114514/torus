package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.math.BlockLocation;
import com.github.alantr7.torus.math.Direction;
import com.github.alantr7.torus.structure.Structure;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.structure.component.Connector;
import org.jetbrains.annotations.NotNull;

public class EnergyCable extends Structure {

    @Override
    public StructureInstance instantiate(@NotNull BlockLocation location, Direction direction) {
        return new CableInstance(location, Connector.Matter.ENERGY);
    }

}
