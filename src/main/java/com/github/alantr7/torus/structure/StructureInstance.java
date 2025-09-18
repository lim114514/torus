package com.github.alantr7.torus.structure;

import com.github.alantr7.torus.math.BlockLocation;
import com.github.alantr7.torus.math.ConnectorLocation;
import com.github.alantr7.torus.math.Direction;
import com.github.alantr7.torus.structure.component.Connector;
import com.github.alantr7.torus.structure.component.StructureComponent;
import com.github.alantr7.torus.world.TorusWorld;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public abstract class StructureInstance {

    public final Structure structure;

    public final BlockLocation location;

    public final Direction direction;

    @Getter
    protected Map<String, StructureComponent> components = new HashMap<>();

    @Getter
    protected Map<ConnectorLocation, Connector> connectors = new HashMap<>();

    public StructureInstance(Structure structure, BlockLocation location, Direction direction) {
        this.structure = structure;
        this.location = location;
        this.direction = direction;
    }

    public abstract void create();

    public abstract void tick();

    public void remove() {
        TorusWorld.removeStructure(this);
    }

}
