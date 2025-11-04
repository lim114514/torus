package com.github.alantr7.torus.structure;

import com.github.alantr7.bytils.buffer.ByteArrayReader;
import com.github.alantr7.bytils.buffer.ByteArrayWriter;
import com.github.alantr7.torus.TorusPlugin;
import com.github.alantr7.torus.exception.MissingDataException;
import com.github.alantr7.torus.exception.SetupException;
import com.github.alantr7.torus.log.Category;
import com.github.alantr7.torus.log.TorusLogger;
import com.github.alantr7.torus.math.MathUtils;
import com.github.alantr7.torus.math.StringPool;
import com.github.alantr7.torus.plugin.Permissions;
import com.github.alantr7.torus.structure.data.Data;
import com.github.alantr7.torus.structure.display.EntityReference;
import com.github.alantr7.torus.world.BlockLocation;
import com.github.alantr7.torus.world.ConnectorLocation;
import com.github.alantr7.torus.world.Direction;
import com.github.alantr7.torus.structure.builder.StructureBodyDef;
import com.github.alantr7.torus.structure.builder.StructureComponentDef;
import com.github.alantr7.torus.structure.component.Connector;
import com.github.alantr7.torus.structure.component.StructureComponent;
import com.github.alantr7.torus.structure.data.DataContainer;
import com.github.alantr7.torus.structure.display.Model;
import com.github.alantr7.torus.world.TorusChunk;
import com.github.alantr7.torus.world.TorusRegion;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;

import java.lang.reflect.Constructor;
import java.util.*;

public abstract class StructureInstance {

    public final Structure structure;

    public final BlockLocation location;

    public final Direction direction;

    public final DataContainer dataContainer;

    public boolean isCorrupted;

    public boolean isDirty = false;

    protected Map<String, StructureComponent> components = new HashMap<>();

    protected Map<ConnectorLocation, Connector> connectors = new HashMap<>();

    protected Map<String, Connector> connectorsByName = new HashMap<>();

    private byte[] bounds;

    private int[][] occupiedChunks;

    public StructureInstance(LoadContext context) {
        this.structure = context.structure();
        this.location = context.location();
        this.direction = context.direction();
        this.dataContainer = context.data();
        setOccupiedChunks();
    }

    public StructureInstance(Structure structure, BlockLocation location, StructureBodyDef bodyDef, Direction direction) {
        this.structure = structure;
        this.location = location;
        this.direction = direction;
        this.dataContainer = new DataContainer();
        this.dataContainer.structure = this;

        for (StructureComponentDef componentDef : bodyDef.components()) {
            MathUtils.applyRotation(componentDef.offset, direction.rotH);
            StructureComponent component = new StructureComponent(
              this,
              new BlockLocation(location.world, (int) componentDef.offset.x, (int) componentDef.offset.y, (int) componentDef.offset.z),
              componentDef.name,
              componentDef.template == null ? componentDef.model : componentDef.template.build(location.toBukkit().add(.5, 0, .5), direction)
            );
            components.put(componentDef.name, component);

            if (componentDef.connectorDef != null) {
                Connector connector = new Connector(components.get(componentDef.name), componentDef.connectorDef.allowedConnections(), componentDef.connectorDef.matter(), componentDef.connectorDef.direction());
                connectors.put(new ConnectorLocation(components.get(componentDef.name).absoluteLocation, componentDef.connectorDef.matter()), connector);
                connectorsByName.put(componentDef.name, connector);
            }
        }
        setOccupiedChunks();
    }

    public byte[] getBounds() {
        if (this.bounds != null)
            return this.bounds;

        return this.bounds = MathUtils.rotateVectors(structure.bounds, direction);
    }

    public StructureComponent requireComponent(String name) throws MissingDataException {
        StructureComponent component = components.get(name);
        if (component == null)
            throw new MissingDataException("Component by name '" + name + "' could not be found.");

        return component;
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

    public Connector requireConnector(String name) throws MissingDataException {
        Connector connector = connectorsByName.get(name);
        if (connector == null)
            throw new MissingDataException("Connector by name '" + name + "' could not be found.");

        return connector;
    }

    public Collection<Connector> getConnectors() {
        return connectors.values();
    }

    private void setOccupiedChunks() {
        byte[] bounds = getBounds();
        Set<Vector2i> positions = new HashSet<>();

        for (int i = 0; i < bounds.length; i+=3) {
            positions.add(new Vector2i((location.x + bounds[i]) >> 4, (location.z + bounds[i + 2]) >> 4));
        }

        this.occupiedChunks = positions.stream().map(v -> new int[]{v.x, v.y}).toArray(int[][]::new);
    }

    public boolean isFullyLoaded() {
        for (int[] chunkPos : occupiedChunks) {
            if (location.world.getChunkAt(chunkPos[0], chunkPos[1]) == null)
                return false;
        }
        return true;
    }

    public abstract void tick();

    protected abstract void setup() throws SetupException;

    @Nullable
    public UUID getOwnerId() {
        return dataContainer.getOrDefault("owner_id", Data.Type.UUID, null);
    }

    public void setOwnerId(@Nullable UUID id) {
        dataContainer.persist("owner_id", Data.Type.UUID, id);
    }

    public boolean testOwnership(@NotNull Player player) {
        UUID ownerId = getOwnerId();
        return player.hasPermission(Permissions.STRUCTURE_BREAK_OTHERS) || (ownerId != null && player.getUniqueId().equals(ownerId));
    }

    public void remove() {
        location.world.removeStructure(this);
    }

    public void save() {
        isDirty = true;
        location.getChunk().isDirty = true;
    }

    public void handlePlayerInteraction(PlayerInteractEvent event, BlockLocation location) {
    }

    public void save(ByteArrayWriter buffer, StringPool keys) {
        // Structure ID
        buffer.writeU2(structure.numericId);

        int basePointer = buffer.getPointer();

        // Structure Length
        buffer.writeU2(0);

        // Location
        buffer.writeBytes(ByteArrayWriter.toBytes(location.x & 0xf, 1));;
        buffer.writeBytes(ByteArrayWriter.toBytes(location.y, 2));
        buffer.writeBytes(ByteArrayWriter.toBytes(location.z & 0xf, 1));

        // Direction
        buffer.writeU1(direction.ordinal());

        // Components Length + Connectors Length
        buffer.writeU1((components.size() << 4) | connectors.size());
        // Components
        components.forEach((name, component) -> {
            // Name
            buffer.writeU1(keys.pool(name));

            // Component offset
            buffer.writeU1(((component.relativeLocation.x + 7) << 4) | (component.relativeLocation.z + 7));
            buffer.writeU1(component.relativeLocation.y);

            // Model
            if (component.getModel() != null) {
                if (component.getModel().template != null && component.getModel().template.name != null) {
                    buffer.writeU1(1);
                    buffer.writeU1(keys.pool(component.getModel().template.name));
                } else {
                    buffer.writeU1(0);
                }

                buffer.writeU1(component.getModel().entityReferences.size());
                for (EntityReference ref : component.getModel().entityReferences) {
                    buffer.writeBytes(ByteArrayWriter.toBytes(ref.id.getMostSignificantBits(), 8));
                    buffer.writeBytes(ByteArrayWriter.toBytes(ref.id.getLeastSignificantBits(), 8));
                }
            } else {
                buffer.writeU1(0);
                buffer.writeU1(0);
            }
        });

        // Connectors
        connectors.forEach((l, connector) -> {
            // Linked component
            buffer.writeU1(keys.pool(connector.getComponent().name));

            int data = connector.matter.ordinal();
            data = (data << 4) | connector.getFlowDirection().ordinal();

            buffer.writeU1(connector.getAllowedConnections());
            buffer.writeU1(connector.getConnections());
            buffer.writeU1(data);
        });

        // Data Container
        buffer.writeBytes(dataContainer.toBytes(keys));
        dataContainer.setDirty(false);

        int returnPointer = buffer.getPointer();
        buffer.setPointer(basePointer);
        buffer.writeU2(returnPointer - basePointer - 2);
        buffer.setPointer(returnPointer);
    }

    public static StructureInstance fromBytes(TorusRegion region, TorusChunk chunk, ByteArrayReader reader, int structureId) {
        Structure structure = TorusPlugin.getInstance().getStructureRegistry().getStructure(structureId);

        // Location
        int x = ByteArrayReader.toInt(reader.readBytes(1));
        int y = ByteArrayReader.toInt(reader.readBytes(2));
        int z = ByteArrayReader.toInt(reader.readBytes(1));
        BlockLocation location = new BlockLocation(chunk.world, (chunk.position.x << 4) | x, y, (chunk.position.y << 4) | z);

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
            String name = region.strings.at(reader.readU1());

            // Component offset
            int packedXZ = reader.readU1();
            int cx = ((packedXZ >> 4) & 0xf) - 7;
            int cz = (packedXZ & 0xf) - 7;
            int cy = reader.readU1();

            // Model
            String modelName = reader.readU1() == 1 ? region.strings.at(reader.readU1()) : null;

            int entitiesLength = reader.readU1();
            List<EntityReference> entities = new ArrayList<>();

            for (int j = 0; j < entitiesLength; j++) {
                byte[] uidmost = reader.readBytes(8);
                byte[] uidleast = reader.readBytes(8);

                UUID uid = new UUID(ByteArrayReader.toLong(uidmost), ByteArrayReader.toLong(uidleast));
                entities.add(new EntityReference(uid));
            }

            Model model = new Model(structure != null ? structure.getNamedModelTemplate(modelName) : null, entities);
            StructureComponent component = new StructureComponent(name, location.getRelative(cx, cy, cz), new BlockLocation(chunk.world, cx, cy, cz), Direction.values()[direction], model);
            components.put(name, component);
        }

        for (int i = 0; i < connectorsLength; i++) {
            // Linked component
            String linkedComponent = region.strings.at(reader.readU1());
            StructureComponent component = components.get(linkedComponent);

            int allowedConnections = reader.readU1();
            int connections = reader.readU1();
            int data = reader.readU1();
            int flowDirection = data & 0x0f;
            int matterOrdinal = (data >> 4) & 0x0f;

            if (matterOrdinal < Connector.Matter.values().length && flowDirection < Connector.FlowDirection.values().length) {
                Connector connector = new Connector(component, allowedConnections, Connector.Matter.values()[matterOrdinal], Connector.FlowDirection.values()[flowDirection]);
                connector.setConnections(connections);
                connectors.put(new ConnectorLocation(component.absoluteLocation, connector.matter), connector);
            } else {
                System.err.println("Invalid matter or direction!");
            }
        }

        // Data Container
        DataContainer dataContainer = DataContainer.fromBytes(reader, region.strings);

        if (structure == null) {
            TorusLogger.error(Category.STRUCTURES, "Unrecognized structure ID: " + structureId);
            return null;
        }

        Class<? extends StructureInstance> instanceClass = structure.instanceClass;

        try {
            Constructor<? extends StructureInstance> constructor = instanceClass.getDeclaredConstructor(LoadContext.class);
            constructor.setAccessible(true);

            StructureInstance instance = constructor.newInstance(new LoadContext(structure, location, Direction.values()[direction], dataContainer));
            dataContainer.structure = instance;
            instance.components.putAll(components);
            instance.connectors.putAll(connectors);
            connectors.forEach((l, c) -> instance.connectorsByName.put(c.getComponent().name, c));

            try {
                instance.setup();
            } catch (SetupException exc) {
                instance.isCorrupted = true;
                exc.printStackTrace();
            }

            return instance;
        } catch (Exception e) {
            TorusLogger.error(Category.STRUCTURES, "Could not instantiate " + instanceClass.getName());
            e.printStackTrace();
        }

        return null;
    }

}
