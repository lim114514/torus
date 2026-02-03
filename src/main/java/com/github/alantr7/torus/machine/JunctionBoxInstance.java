package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.exception.SetupException;
import com.github.alantr7.torus.structure.*;
import com.github.alantr7.torus.structure.builder.StructureBodyDef;
import com.github.alantr7.torus.structure.socket.Socket;
import com.github.alantr7.torus.world.BlockLocation;
import com.github.alantr7.torus.world.Direction;
import com.github.alantr7.torus.world.Pitch;

import java.util.Collection;

import static com.github.alantr7.torus.machine.Connector.getStateFromDirection;

public class JunctionBoxInstance extends StructureInstance implements Conductor {

    private Boolean wasPowered;

    JunctionBoxInstance(LoadContext context) {
        super(context);
    }

    public JunctionBoxInstance(BlockLocation location, StructureBodyDef bodyDef, Direction direction, Pitch pitch) {
        super(Structures.JUNCTION_BOX, location, bodyDef, direction, pitch);
    }

    @Override
    protected void setup() throws SetupException {
        Socket socket = getSocket("base");
        for (Direction direction : Direction.values()) {
            if (socket.isConnected(direction)) {
                state.set(getStateFromDirection(direction.relativeTo(this.direction)), true, false);
            }
        }
    }

    @Override
    public void tick(boolean isVirtual) {
        if (isVirtual)
            return;

        boolean isPowered = location.getBlock().isBlockPowered();
        if (wasPowered != null && isPowered == wasPowered)
            return;

        if (wasPowered = isPowered) {
            location.world.networkManager.queueLoaded(getSocket("base"));
        } else {
            location.world.networkManager.queueUnloaded(getSocket("base"));
        }
    }

    @Override
    public boolean isConductive() {
        return wasPowered != null && wasPowered;
    }

    @Override
    public void onSocketConnect(Socket socket, Socket neighbor, Direction direction) {
        state.set(Connector.getStateFromDirection(direction.relativeTo(this.direction)), true);
    }

    @Override
    public void onSocketDisconnect(Socket socket, Socket neighbor, Direction direction) {
        state.set(Connector.getStateFromDirection(direction.relativeTo(this.direction)), false);
    }

    @Override
    public Collection<BlockLocation> getConnectedNodes() {
        return getSocket("base").getNodes();
    }

    @Override
    public Socket.Medium getMedium() {
        return Socket.Medium.ENERGY;
    }

}
