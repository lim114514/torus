package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.world.BlockLocation;
import com.github.alantr7.torus.world.Direction;
import com.github.alantr7.torus.structure.LoadContext;
import com.github.alantr7.torus.structure.builder.StructureBodyDef;
import com.github.alantr7.torus.structure.data.Data;
import com.github.alantr7.torus.model.ModelTemplate;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.structure.Structures;
import com.github.alantr7.torus.structure.component.Connectable;
import com.github.alantr7.torus.structure.component.Connector;

import static com.github.alantr7.torus.machine.EnergyCable.*;

public class CableInstance extends StructureInstance implements Connectable {

    protected Data<Integer> connections = dataContainer.persist("connections", Data.Type.INT, 0);

    protected Data<Integer> type = dataContainer.persist("type", Data.Type.INT, 0);

    boolean shouldUpdateModel;

    public CableInstance(BlockLocation location, StructureBodyDef bodyDef, Connector.Matter type) {
        super(type == Connector.Matter.ENERGY ? Structures.ENERGY_CABLE : type == Connector.Matter.ITEM ? Structures.ITEM_CABLE : Structures.FLUID_CABLE, location, bodyDef, Direction.NORTH);
        this.type.update(type.ordinal());
    }

    CableInstance(LoadContext context) {
        super(context);
    }

    @Override
    protected void setup() {
    }

    public void updateConnections() {
        updateConnection(Direction.NORTH);
        updateConnection(Direction.SOUTH);
        updateConnection(Direction.EAST);
        updateConnection(Direction.WEST);
        updateConnection(Direction.UP);
        updateConnection(Direction.DOWN);

        if (shouldUpdateModel) {
            updateModel();
            shouldUpdateModel = false;
        }
    }

    public void updateConnection(Direction direction) {
        StructureInstance possibleConnection = location.getRelative(direction).getStructure();
        boolean hasConnected = false;

        // Check if this cable connects to a connector
        if (possibleConnection != null) {
            Connector connector = possibleConnection.getConnector(location.getRelative(direction), getType());
            if (connector != null && connector.isConnectableFrom(direction.getOpposite())) {
                hasConnected = true;
                shouldUpdateModel = true;

                connector.setConnected(direction.getOpposite(), true);
                possibleConnection.save();
            }
        }

        // Check if this cable connects to another cable
        if (!hasConnected && possibleConnection instanceof CableInstance cable && cable.getType() == getType()) {
            hasConnected = true;
            shouldUpdateModel = true;

            cable.setConnected(direction.getOpposite(), true);
            cable.updateModel();
        }

        if (hasConnected != isConnected(direction))
            shouldUpdateModel = true;

        setConnected(direction, hasConnected);
    }

    public void updateModel() {
        ModelTemplate model = new ModelTemplate();
        if (connections.get() == 0) {
            model.add(CABLE_MODELS[type.get()][6]);
        }

        for (Direction direction : Direction.values()) {
            if (isConnected(direction)) {
                model.add(CABLE_MODELS[type.get()][direction.ordinal()]);
            }
        }

        components.get("base").setModel(model.recycle(components.get("base").getModel(), location.getBlock().getLocation().add(.5, 0, .5), direction.rotH, direction.rotV));
        save();
    }

    @Override
    public void tick() {
    }

    @Override
    public int getAllowedConnections() {
        return Direction.NORTH.mask() | Direction.SOUTH.mask() | Direction.EAST.mask() | Direction.WEST.mask() | Direction.UP.mask() | Direction.DOWN.mask();
    }

    @Override
    public int getConnections() {
        return connections.get();
    }

    @Override
    public void setConnections(int connections) {
        this.connections.update(connections);
    }

    public Connector.Matter getType() {
        return Connector.Matter.values()[type.get()];
    }

}
