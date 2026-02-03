package com.github.alantr7.torus.network;

import com.github.alantr7.torus.machine.CableInstance;
import com.github.alantr7.torus.machine.WireConnectorInstance;
import com.github.alantr7.torus.structure.Conductor;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.structure.component.Socket;
import com.github.alantr7.torus.world.BlockLocation;
import com.github.alantr7.torus.world.Direction;
import com.github.alantr7.torus.world.TorusWorld;
import org.bukkit.Bukkit;

import java.util.*;

public class NetworkManager {

    public final TorusWorld world;

    Queue<Socket> queue = new LinkedList<>();

    public NetworkManager(TorusWorld world) {
        this.world = world;
    }

    public void queueLoaded(Socket socket) {
        if (socket == null) {
            throw new NullPointerException("Attempted to queue null socket!");
        }
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

    private static boolean attemptDirectMerge(Socket socket1, Socket socket2) {
        if (socket1.network == NetworkGraph.INIT && socket2.network != NetworkGraph.INIT) {
            socket1.network = socket2.network;
            if (socket1.structure instanceof Conductor) {
                socket1.network.edges.add(socket1.structure);
            } else {
                socket1.network.nodes.add(new Node(socket1.structure, socket1));
            }
            return true;
        }
        return false;
    }

    public boolean attemptMerge(Socket socket1, Socket socket2) {
        if (attemptDirectMerge(socket1, socket2) || attemptDirectMerge(socket2, socket1))
            return true;

        return false;
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

        // Skip network update if structure was removed meanwhile
        if (socket.structure == null || socket.structure.isRemoved)
            return;

        NetworkGraph network = new NetworkGraph(world.getTicks(), false);
        network.nodes = new HashSet<>();
        socket.network = network;

        // Skip network update if it was already done
        int directionsCount = 0;
        int closedDirectionsCount = 0;

        if (!(socket.structure instanceof Conductor conductor)) {
            network.nodes.add(new Node(socket.structure, socket));
        } else {
            if (!conductor.isConductive()) {
                return;
            }
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

            if (neighbor instanceof Conductor conductor) {
                if (conductor.isConductive()) {
                    Socket neighborSocket = neighbor.getSocket("base");
                    if (neighborSocket != null) {
                        neighborSocket.network = network;
                    }
                    network.edges.add(neighbor);
                }
                continue;
            }

            Socket neighborSocket = neighbor.getSocket(socket.getComponent().absoluteLocation, socket.medium);
            if (neighborSocket != null) {
                network.nodes.add(new Node(neighbor, neighborSocket));
                neighborSocket.network = network;

                closedDirectionsCount++;
            }
        }

        if (!(socket.structure instanceof WireConnectorInstance)) {
            if (directionsCount == closedDirectionsCount) {
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
            if (socket.isConnected(direction) && socket.getComponent().absoluteLocation.getRelative(direction).getStructure() instanceof Conductor conductor && conductor.isConductive()) {
                open.add(((StructureInstance) conductor).location);
                network.edges.add((StructureInstance) conductor);
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

                // Check if it's a conductor
                if (neighbor instanceof Conductor conductor) {
                    if (conductor.isConductive() && !open.contains(neighborLoc)) {
                        open.add(neighborLoc);
                        network.edges.add(neighbor);

                        Socket neighborSocket = neighbor.getSocket("base");
                        if (neighborSocket != null) {
                            neighborSocket.network = network;
                        }
                    }
                    continue;
                }

                Socket neighborSocket = neighbor.getSocket(start, socket.medium);
                if (neighborSocket != null) {
                    network.nodes.add(new Node(neighbor, neighborSocket));
                    closed.add(neighborLoc);

                    neighborSocket.network = network;
                }
            }

            closed.add(start);
        }
    }

}
