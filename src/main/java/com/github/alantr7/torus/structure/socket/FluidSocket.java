package com.github.alantr7.torus.structure.socket;

import com.github.alantr7.torus.network.Node;
import com.github.alantr7.torus.structure.FluidContainer;
import com.github.alantr7.torus.structure.component.StructureComponent;
import com.github.alantr7.torus.world.Fluid;

public class FluidSocket extends Socket {

    public FluidSocket(StructureComponent component, int allowedConnections, FlowDirection direction) {
        super(component, allowedConnections, Medium.FLUID, direction);
    }

    public int consumeFluid(Fluid fluid, int amount) {
        if (network.isInvalidated())
            return 0;

        int original = amount;
        for (Node conn : network.nodes) {
            if (conn.socket == this)
                continue;

            if (conn.socket.medium != Socket.Medium.FLUID)
                continue;

            if (conn.socket.flowDirection != Socket.FlowDirection.OUT && conn.socket.flowDirection != Socket.FlowDirection.ALL)
                continue;

            FluidContainer container = (FluidContainer) conn.structure;
            if (container.getFluid() != fluid)
                continue;

            amount -= container.consumeFluid(Math.min(amount, conn.socket.maximumOutput));

            if (amount == 0)
                break;
        }

        return original - amount;
    }

}
