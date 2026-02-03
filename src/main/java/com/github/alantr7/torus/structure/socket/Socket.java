package com.github.alantr7.torus.structure.socket;

import com.github.alantr7.torus.network.NetworkGraph;
import com.github.alantr7.torus.network.Node;
import com.github.alantr7.torus.structure.*;
import com.github.alantr7.torus.structure.component.Connectable;
import com.github.alantr7.torus.structure.component.StructureComponent;
import com.github.alantr7.torus.utils.EventUtils;
import com.github.alantr7.torus.world.Fluid;
import com.github.alantr7.torus.world.BlockLocation;
import com.github.alantr7.torus.world.Direction;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

public abstract class Socket implements Connectable {

    public StructureInstance structure;

    @Getter @Setter
    protected int connections;

    @Getter @Setter
    protected int allowedConnections;

    public int maximumInput = 100;

    public int maximumOutput = 100;

    public NetworkGraph network = NetworkGraph.INIT;

    @Getter @Setter
    protected FlowDirection flowDirection;

    @Getter
    public final Medium medium;

    public enum FlowDirection {
        ALL, NONE, IN, OUT;
    }

    public enum Medium {
        ITEM, ENERGY, FLUID;
    }

    @Getter
    protected StructureComponent component;

    public Socket(StructureComponent component, int allowedConnections, Medium medium, FlowDirection direction) {
        this.component = component;
        this.allowedConnections = allowedConnections;
        this.flowDirection = direction;
        this.medium = medium;
    }

    public Collection<BlockLocation> getNodes() {
        List<BlockLocation> nodes = new ArrayList<>();
        for (Direction direction : Direction.values()) {
            if (isConnected(direction))
                nodes.add(component.absoluteLocation.getRelative(direction));
        }
        return nodes;
    }

    public boolean toggleConnection(Direction direction) {
        if (!isConnectableFrom(direction))
            return false;

        BlockLocation relativeLoc = getComponent().absoluteLocation.getRelative(direction);
        StructureInstance neighbor = relativeLoc.getStructure();
        if (neighbor == null)
            return false;

        Socket neighborSocket = neighbor.getSocket(getComponent().absoluteLocation, medium);
        if (neighborSocket == null || !neighborSocket.isConnectableFrom(direction.getOpposite()))
            return false;

        if (isConnected(direction)) {
            EventUtils.callStructuresDisconnectEvent(this, neighborSocket, direction, direction.getOpposite());

            setConnected(direction, false);
            structure.onSocketDisconnect(this, neighborSocket, direction);

            neighborSocket.setConnected(direction.getOpposite(), false);
            neighbor.onSocketDisconnect(neighborSocket, this, direction.getOpposite());
        } else {
            if (!EventUtils.callStructuresConnectEvent(this, neighborSocket, direction, direction.getOpposite()))
                return true;

            setConnected(direction, true);
            structure.onSocketConnect(this, neighborSocket, direction);

            neighborSocket.setConnected(direction.getOpposite(), true);
            neighbor.onSocketConnect(neighborSocket, this, direction.getOpposite());
        }

        structure.location.world.networkManager.queueLoaded(this);
        structure.location.world.networkManager.queueLoaded(neighborSocket);
        return true;
    }

}
