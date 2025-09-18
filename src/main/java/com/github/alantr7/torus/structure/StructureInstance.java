package com.github.alantr7.torus.structure;

import com.github.alantr7.torus.math.BlockLocation;
import com.github.alantr7.torus.math.ConnectorLocation;
import com.github.alantr7.torus.math.Direction;
import com.github.alantr7.torus.structure.builder.StructureBodyDef;
import com.github.alantr7.torus.structure.builder.StructureComponentDef;
import com.github.alantr7.torus.structure.builder.StructureConnectorDef;
import com.github.alantr7.torus.structure.component.Connector;
import com.github.alantr7.torus.structure.component.StructureComponent;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class StructureInstance {

    public final Structure structure;

    public final BlockLocation location;

    public final Direction direction;

    protected Map<String, StructureComponent> components = new HashMap<>();

    protected Map<ConnectorLocation, Connector> connectors = new HashMap<>();

    protected Map<String, Connector> connectorsByName = new HashMap<>();

    public StructureInstance(Structure structure, BlockLocation location, StructureBodyDef bodyDef, Direction direction) {
        this.structure = structure;
        this.location = location;
        this.direction = direction;

        for (StructureComponentDef componentDef : bodyDef.components()) {
            StructureComponent component = new StructureComponent(
              this,
              new BlockLocation(location.world, (int) componentDef.offset().x, (int) componentDef.offset().y, (int) componentDef.offset().z),
              componentDef.model()
            );
            components.put(componentDef.name(), component);
        }

        for (StructureConnectorDef connectorDef : bodyDef.connectors()) {
            Connector connector = new Connector(components.get(connectorDef.component()), connectorDef.allowedConnections(), connectorDef.matter(), connectorDef.direction());
            connectors.put(new ConnectorLocation(components.get(connectorDef.component()).absoluteLocation, connectorDef.matter()), connector);
            connectorsByName.put(connectorDef.component(), connector);
        }
    }

    public StructureComponent getComponent(String name) {
        return components.get(name);
    }

    public Collection<StructureComponent> getComponents() {
        return components.values();
    }

    public Connector getConnector(BlockLocation location, Connector.Matter matter) {
        return connectors.get(new ConnectorLocation(location, matter));
    }

    public Connector getConnector(String name) {
        return connectorsByName.get(name);
    }

    public Collection<Connector> getConnectors() {
        return connectors.values();
    }

    public abstract void tick();

    protected abstract void setup();

    public void remove() {
        location.world.removeStructure(this);
    }

}
