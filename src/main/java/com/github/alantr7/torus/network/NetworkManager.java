package com.github.alantr7.torus.network;

import com.github.alantr7.torus.machine.CableInstance;
import com.github.alantr7.torus.machine.WireConnectorInstance;
import com.github.alantr7.torus.structure.Conductor;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.structure.component.Socket;
import com.github.alantr7.torus.world.BlockLocation;
import com.github.alantr7.torus.world.Direction;
import com.github.alantr7.torus.world.TorusWorld;

import java.util.*;

public class NetworkManager {

    public final TorusWorld world;

    Queue<Socket> queue = new LinkedList<>();

    public NetworkManager(TorusWorld world) {
        this.world = world;
    }

    public void queueLoaded(Socket socket) {
        queue.add(socket);
    }

    public void queueUnloaded(Socket socket) {
        socket.network.invalidate();
        for (Node conn : socket.network.nodes) {
            if (conn.socket == socket || conn.structure.isUnloaded())
                continue;

            queueLoaded(conn.socket);
        }
    }

    public void tick() {
        if (queue.isEmpty())
            return;

        int spawnTick = world.getTicks();
        while (!queue.isEmpty()) {
            Socket socket = queue.remove();
            if (socket.network.spawnTick == spawnTick)
                continue;

            buildNetwork(socket);
        }
    }

    private void buildNetwork(Socket socket) {
        socket.network.invalidate();

        NetworkGraph network = new NetworkGraph(world.getTicks(), false);
        socket.network = network;

        // Skip network update if it was already done
        int directionsCount = 0;
        int closedDirectionsCount = 0;
        Set<Node> networkConnections = new HashSet<>();

        if (socket.structure != null && !(socket.structure instanceof Conductor)) {
            networkConnections.add(new Node(socket.structure, socket));
        }
        if (socket.structure instanceof Conductor) {
            network.edges.add(socket.structure);
        }

        // Check if socket is directly connected to another
        for (Direction direction : Direction.values()) {
            if (!socket.isConnected(direction))
                continue;

            directionsCount++;
            StructureInstance neighbor = socket.getComponent().absoluteLocation.getRelative(direction).getStructure();
            if (neighbor == null) {
                closedDirectionsCount++;
                continue;
            }

            if (neighbor instanceof CableInstance || neighbor instanceof WireConnectorInstance) {
                continue;
            }

            Socket neighborSocket = neighbor.getSocket(socket.getComponent().absoluteLocation, socket.medium);
            if (neighborSocket != null) {
                networkConnections.add(new Node(neighbor, neighborSocket));
                closedDirectionsCount++;
            }
        }

        if (!(socket.structure instanceof WireConnectorInstance)) {
            if (directionsCount == closedDirectionsCount) {
                network.nodes = networkConnections;
                return;
            }
        }

        List<BlockLocation> open = new LinkedList<>();
        List<BlockLocation> closed = new LinkedList<>();

        if (socket.structure instanceof WireConnectorInstance) {
            open.add(socket.structure.location);
        }
        closed.add(socket.getComponent().absoluteLocation);
        for (Direction direction : Direction.values()) {
            if (socket.isConnected(direction)) {
                if (socket.getComponent().absoluteLocation.getRelative(direction).getStructure() instanceof Conductor conductor) {
                    open.add(((StructureInstance) conductor).location);
                    network.edges.add((StructureInstance) conductor);
                }
            }
        }

        while (!open.isEmpty()) {
            BlockLocation start = open.removeFirst();
            Conductor startCable = (Conductor) start.getStructure();
            for (BlockLocation neighborLoc : startCable.getConnectedNodes()) {
                if (closed.contains(neighborLoc)) // Skip if already checked
                    continue;

                StructureInstance neighbor = neighborLoc.getStructure();
                if (neighbor == null) {
                    closed.add(neighborLoc);
                    continue;
                }

                Socket neighborSocket = neighbor.getSocket(start, socket.medium);

                // Check if it's a conductor
                if (neighbor instanceof Conductor) {
                    if (!open.contains(neighborLoc)) {
                        open.add(neighborLoc);
                        network.edges.add(neighbor);

                        if (neighborSocket != null) {
                            neighborSocket.network = network;
                        }
                    }
                    continue;
                }

                if (neighborSocket != null) {
                    networkConnections.add(new Node(neighbor, neighborSocket));
                    closed.add(neighborLoc);

                    neighborSocket.network = network;
                }
            }

            closed.add(start);
        }

        network.nodes = networkConnections;
    }

}
