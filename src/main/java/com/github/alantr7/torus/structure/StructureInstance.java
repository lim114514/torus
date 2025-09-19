package com.github.alantr7.torus.structure;

import com.github.alantr7.bytils.buffer.ByteArrayReader;
import com.github.alantr7.bytils.buffer.ByteArrayWriter;
import com.github.alantr7.torus.math.BlockLocation;
import com.github.alantr7.torus.math.ConnectorLocation;
import com.github.alantr7.torus.math.Direction;
import com.github.alantr7.torus.structure.builder.StructureBodyDef;
import com.github.alantr7.torus.structure.builder.StructureComponentDef;
import com.github.alantr7.torus.structure.builder.StructureConnectorDef;
import com.github.alantr7.torus.structure.component.Connector;
import com.github.alantr7.torus.structure.component.StructureComponent;
import com.github.alantr7.torus.structure.data.DataContainer;
import com.github.alantr7.torus.structure.display.Model;
import com.github.alantr7.torus.world.TorusWorld;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.ItemDisplay;

import java.lang.reflect.Constructor;
import java.util.*;

public abstract class StructureInstance {

    public final Structure structure;

    public final BlockLocation location;

    public final Direction direction;

    @Getter
    protected final DataContainer dataContainer;

    protected Map<String, StructureComponent> components = new HashMap<>();

    protected Map<ConnectorLocation, Connector> connectors = new HashMap<>();

    protected Map<String, Connector> connectorsByName = new HashMap<>();

    public StructureInstance(LoadContext context) {
        this.structure = context.structure();
        this.location = context.location();
        this.direction = context.direction();
        this.dataContainer = context.data();
    }

    public StructureInstance(Structure structure, BlockLocation location, StructureBodyDef bodyDef, Direction direction) {
        this.structure = structure;
        this.location = location;
        this.direction = direction;
        this.dataContainer = new DataContainer();

        for (StructureComponentDef componentDef : bodyDef.components()) {
            StructureComponent component = new StructureComponent(
              this,
              new BlockLocation(location.world, (int) componentDef.offset().x, (int) componentDef.offset().y, (int) componentDef.offset().z),
              componentDef.name(),
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

    // TODO: Use Utf8Pool for strings in the entire chunk
    public void save(ByteArrayWriter buffer) {
        // Structure ID
        buffer.writeString(structure.getId());

        // Location
//        buffer.writeBytes(ByteArrayWriter.toBytes(location.x & 0xf, 2));
        buffer.writeBytes(ByteArrayWriter.toBytes(location.x, 4));
        buffer.writeBytes(ByteArrayWriter.toBytes(location.y, 3));
//        buffer.writeBytes(ByteArrayWriter.toBytes(location.z % 0xf, 2));
        buffer.writeBytes(ByteArrayWriter.toBytes(location.z, 4));

        // Direction
        buffer.writeU1(direction.ordinal());

        // Components Length + Connectors Length
        buffer.writeU1((components.size() << 4) | connectors.size());
        // Components
        components.forEach((name, component) -> {
            // Name
            buffer.writeString(name);

            // Component offset
            buffer.writeU1(component.relativeLocation.x);
            buffer.writeU1(component.relativeLocation.y);
            buffer.writeU1(component.relativeLocation.z);

            // Model
            if (component.getModel() != null) {
                buffer.writeU1(component.getModel().entities.size());
                for (ItemDisplay entity : component.getModel().entities) {
                    buffer.writeString(entity.getUniqueId().toString());
                }
            } else {
                buffer.writeU1(0);
            }
        });

        // Connectors
        connectors.forEach((l, connector) -> {
            // Linked component
            buffer.writeString(connector.getComponent().name);

            int data = connector.matter.ordinal();
            data = (data << 4) | connector.getFlowDirection().ordinal();

            buffer.writeU1(connector.getAllowedConnections());
            buffer.writeU1(connector.getConnections());
            buffer.writeU1(data);
        });

        // Data Container
        buffer.writeBytes(dataContainer.toBytes());

        dataContainer.setDirty(false);
    }

    public static StructureInstance fromBytes(TorusWorld world, ByteArrayReader reader) {
        String structureId = reader.readString();

        // Location
        int x = ByteArrayReader.toInt(reader.readBytes(4));
        int y = ByteArrayReader.toInt(reader.readBytes(3));
        int z = ByteArrayReader.toInt(reader.readBytes(4));
        BlockLocation location = new BlockLocation(world, x, y, z);

        // Direction
        int direction = reader.readU1();

        // Components Length + Connectors Length
        int counts = reader.readU1();
        int componentsLength = (counts >> 4) & 0x0f;
        int connectorsLength = counts & 0x0f;

        Map<String, StructureComponent> components = new HashMap<>(componentsLength);
        Map<ConnectorLocation, Connector> connectors = new HashMap<>(connectorsLength);

        for (int i = 0; i < componentsLength; i++) {
            // Name
            String name = reader.readString();

            // Component offset
            int cx = reader.readU1();
            int cy = reader.readU1();
            int cz = reader.readU1();

            // Model
            int entitiesLength = reader.readU1();
            List<ItemDisplay> entities = new ArrayList<>();

            for (int j = 0; j < entitiesLength; j++) {
                String uid = reader.readString();
                entities.add((ItemDisplay) Bukkit.getEntity(UUID.fromString(uid)));

                Bukkit.broadcastMessage("Entity: " + uid);
            }

            Model model = new Model(entities);
            StructureComponent component = new StructureComponent(name, location.getRelative(cx, cy, cz), new BlockLocation(world, cx, cy, cz), Direction.values()[direction], model);
            components.put(name, component);
        }

        Bukkit.broadcastMessage("Connectors: " + connectorsLength);
        for (int i = 0; i < connectorsLength; i++) {
            // Linked component
            String linkedComponent = reader.readString();
            StructureComponent component = components.get(linkedComponent);

            int allowedConnections = reader.readU1();
            int connections = reader.readU1();
            int data = reader.readU1();
            int flowDirection = data & 0x0f;
            int matterOrdinal = (data >> 4) & 0x0f;

            // TODO: Error handling
            Connector connector = new Connector(component, allowedConnections, Connector.Matter.values()[matterOrdinal], Connector.FlowDirection.values()[flowDirection]);
            connector.setConnections(connections);
            connectors.put(new ConnectorLocation(component.absoluteLocation, connector.matter), connector);
        }

        // Data Container
        DataContainer dataContainer = DataContainer.fromBytes(reader);

        System.out.println(" - Location: " + x + ", " + y + ", " + z);
        System.out.println(" - Direction: " + direction + "\n");

        Structure structure = Structures.getStructureById(structureId);
        if (structure == null) {
            System.err.println("Invalid structure!");
            return null;
        }

        Class<? extends StructureInstance> instanceClass = structure.instanceClass;

        try {
            Constructor<? extends StructureInstance> constructor = instanceClass.getDeclaredConstructor(LoadContext.class);
            constructor.setAccessible(true);

            StructureInstance instance = constructor.newInstance(new LoadContext(structure, new BlockLocation(world, x, y, z), Direction.values()[direction], dataContainer));
            instance.components.putAll(components);
            instance.connectors.putAll(connectors);
            connectors.forEach((l, c) -> instance.connectorsByName.put(c.getComponent().name, c));

            instance.setup();
            return instance;
        } catch (Exception e) {
            System.err.println("Could not instantiate " + instanceClass.getName());
            e.printStackTrace();
        }

        return null;
    }

}
