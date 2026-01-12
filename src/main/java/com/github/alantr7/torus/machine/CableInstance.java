package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.exception.SetupException;
import com.github.alantr7.torus.structure.Conductor;
import com.github.alantr7.torus.world.BlockLocation;
import com.github.alantr7.torus.world.Direction;
import com.github.alantr7.torus.structure.LoadContext;
import com.github.alantr7.torus.structure.builder.StructureBodyDef;
import com.github.alantr7.torus.structure.data.Data;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.structure.Structures;
import com.github.alantr7.torus.structure.component.Socket;
import com.github.alantr7.torus.world.SocketLocation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.github.alantr7.torus.machine.EnergyCable.*;

public class CableInstance extends StructureInstance implements Conductor {

    protected Data<Integer> type = dataContainer.persist("type", Data.Type.INT, 0);

    protected Socket socket;

    public CableInstance(BlockLocation location, StructureBodyDef bodyDef, Socket.Medium type) {
        super(type == Socket.Medium.ENERGY ? Structures.ENERGY_CABLE : type == Socket.Medium.ITEM ? Structures.ITEM_CABLE : Structures.FLUID_CABLE, location, bodyDef, Direction.NORTH);
        this.type.update(type.ordinal());
    }

    CableInstance(LoadContext context) {
        super(context);
    }

    @Override
    protected void setup() throws SetupException {
        if (dataContainer.getEntries().containsKey("connections")) {
            socket = new Socket(getComponent("base"), 0b111111, getMedium(), Socket.FlowDirection.ALL);
            socket.structure = this;
            socket.setConnections(dataContainer.getOrDefault("connections", Data.Type.INT, 0));

            // Register sockets
            socketsByName.put("base", socket);
            for (Direction possibleDirection : Direction.values()) {
                if (socket.isConnectableFrom(possibleDirection)) {
                    sockets.put(new SocketLocation(getComponent("base").absoluteLocation.getRelative(possibleDirection), socket.medium), socket);
                }
            }

            dataContainer.getEntries().remove("connections");
            save();
        } else {
            socket = requireSocket("base");
        }

        for (Direction direction : Direction.values()) {
            if (socket.isConnected(direction)) {
                state.set(getStateFromDirection(direction), true, false);
            }
        }
    }

    @Override
    public void tick(boolean isVirtual) {
    }

    @Override
    public void onSocketConnect(Socket socket, Socket neighbor, Direction direction) {
        state.set(getStateFromDirection(direction), true);
    }

    @Override
    public void onSocketDisconnect(Socket socket, Socket neighbor, Direction direction) {
        state.set(getStateFromDirection(direction), false);
    }

    @Override
    public Socket.Medium getMedium() {
        return Socket.Medium.values()[type.get()];
    }

    @Override
    public Collection<BlockLocation> getConnectedNodes() {
        List<BlockLocation> nodes = new ArrayList<>();
        for (Direction direction : Direction.values()) {
            if (socket.isConnected(direction))
                nodes.add(location.getRelative(direction));
        }

        return nodes;
    }

}
