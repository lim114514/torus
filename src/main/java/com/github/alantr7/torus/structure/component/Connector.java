package com.github.alantr7.torus.structure.component;

import com.github.alantr7.torus.machine.CableInstance;
import com.github.alantr7.torus.math.BlockLocation;
import com.github.alantr7.torus.math.ConnectorLocation;
import com.github.alantr7.torus.math.Direction;
import com.github.alantr7.torus.structure.EnergyContainer;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.structure.inventory.StructureInventory;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

public class Connector implements Connectable {

    @Getter @Setter
    protected int connections;

    @Getter @Setter
    protected int allowedConnections;

    @Getter @Setter
    protected int maximumInput = 100;

    @Getter @Setter
    protected int maximumOutput = 100;

    @Getter
    public List<Connection> connectedStructures = Collections.emptyList();

    public StructureInventory linkedInventory;

    @Getter
    protected FlowDirection flowDirection;

    public final Matter matter;

    public enum FlowDirection {
        ALL, NONE, IN, OUT,
        ;
    }

    public enum Matter {
        ITEM, ENERGY, LIQUID,
        ;
    }

    @Getter
    protected StructureComponent component;

    public Connector(StructureComponent component, int allowedConnections, FlowDirection direction, Matter matter) {
        this.component = component;
        this.allowedConnections = allowedConnections;
        this.flowDirection = direction;
        this.matter = matter;
    }

    public void updateConnections() {
        List<BlockLocation> open = new LinkedList<>();
        List<BlockLocation> closed = new LinkedList<>();
        List<Connection> structures = new ArrayList<>();

        closed.add(component.absoluteLocation);
        for (Direction direction : Direction.values()) {
            if (isConnected(direction)) {
                if (component.absoluteLocation.getRelative(direction).getStructure() instanceof CableInstance cable)
                    open.add(cable.location);
            }
        }

        while (!open.isEmpty()) {
            BlockLocation start = open.getFirst();
            CableInstance startCable = (CableInstance) start.getStructure();
            for (Direction direction : Direction.values()) {
                if (startCable.isConnected(direction)) {
                    BlockLocation neighborLoc = start.getRelative(direction);

                    // Skip if already checked
                    if (closed.contains(neighborLoc))
                        continue;

                    // Check if it's a cable
                    StructureInstance neighbor = neighborLoc.getStructure();
                    if (neighbor == null) {
                        closed.add(neighborLoc);
                        continue;
                    }

                    if (neighbor instanceof CableInstance) {
                        if (!open.contains(neighborLoc)) {
                            open.add(neighborLoc);
                            continue;
                        }
                    }

                    Connector connector = neighbor.getConnectors().get(new ConnectorLocation(neighborLoc, matter));
                    if (connector != null) {
                        structures.add(new Connection(neighbor, connector));
                        closed.add(neighborLoc);
                    }
                }
            }

            closed.add(open.removeFirst());
        }

        this.connectedStructures = structures;
    }

    public double consumeEnergy(double amount) {
        double original = amount;
        for (Connection conn : connectedStructures) {
            if (conn.connector.matter != Matter.ENERGY)
                continue;

            if (conn.connector.flowDirection != FlowDirection.OUT && conn.connector.flowDirection != FlowDirection.ALL)
                continue;

            EnergyContainer capacitor = (EnergyContainer) conn.structure;
            amount -= capacitor.consumeEnergy(Math.min(amount, conn.connector.maximumOutput));

            if (amount == 0)
                break;
        }

        return original - amount;
    }

    public static class Connection {

        public final StructureInstance structure;

        public final Connector connector;

        public Connection(StructureInstance structure, Connector connector) {
            this.structure = structure;
            this.connector = connector;
        }

    }

}
