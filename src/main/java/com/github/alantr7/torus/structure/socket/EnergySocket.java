package com.github.alantr7.torus.structure.socket;

import com.github.alantr7.torus.network.Node;
import com.github.alantr7.torus.structure.EnergyContainer;
import com.github.alantr7.torus.structure.component.StructureComponent;

public class EnergySocket extends Socket {

    public EnergySocket(StructureComponent component, int allowedConnections, FlowDirection direction) {
        super(component, allowedConnections, Medium.ENERGY, direction);
    }

    public int consumeEnergy(int amount) {
        if (network.isInvalidated())
            return 0;

        int original = amount;
        for (Node conn : network.nodes) {
            if (conn.socket == this)
                continue;

            if (conn.socket.medium != Medium.ENERGY)
                continue;

            if (conn.socket.flowDirection != FlowDirection.OUT && conn.socket.flowDirection != FlowDirection.ALL)
                continue;

            EnergyContainer capacitor = (EnergyContainer) conn.structure;
            amount -= capacitor.consumeEnergy(Math.min(amount, conn.socket.maximumOutput));;

            if (amount == 0)
                break;
        }

        return original - amount;
    }

    public int maintainEnergy(EnergyContainer container) {
        if (network.isInvalidated())
            return 0;

        if (container.getStoredEnergy().get() == container.getEnergyCapacity())
            return 0;

        if (network.nodes.isEmpty() || (network.nodes.size() == 1 && network.nodes.iterator().next().socket == this))
            return 0;

        return container.supplyEnergy(
          consumeEnergy(Math.min(maximumInput, container.getEnergyCapacity() - container.getStoredEnergy().get()))
        );
    }

}
