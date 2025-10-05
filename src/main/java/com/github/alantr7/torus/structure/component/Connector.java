package com.github.alantr7.torus.structure.component;

import com.github.alantr7.torus.Fluid;
import com.github.alantr7.torus.item.ItemCriteria;
import com.github.alantr7.torus.machine.CableInstance;
import com.github.alantr7.torus.structure.data.Data;
import com.github.alantr7.torus.world.BlockLocation;
import com.github.alantr7.torus.math.Direction;
import com.github.alantr7.torus.structure.EnergyContainer;
import com.github.alantr7.torus.structure.FluidContainer;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.structure.inventory.StructureInventory;
import com.github.alantr7.torus.world.TorusWorld;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.BlockInventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class Connector implements Connectable {

    @Getter @Setter
    protected int connections;

    @Getter @Setter
    protected int allowedConnections;

    public int maximumInput = 100;

    public int maximumOutput = 100;

    @Getter
    public List<Connection> connectedStructures = Collections.emptyList();

    public StructureInventory linkedInventory;

    @Getter @Setter
    protected FlowDirection flowDirection;

    public final Matter matter;

    public enum FlowDirection {
        ALL, NONE, IN, OUT,
        ;
    }

    public enum Matter {
        ITEM, ENERGY, FLUID,
        ;
    }

    @Getter
    protected StructureComponent component;

    public Connector(StructureComponent component, int allowedConnections, Matter matter, FlowDirection direction) {
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

                    Connector connector = neighbor.getConnector(neighborLoc, matter);
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

    public int consumeEnergy(int amount) {
        int original = amount;
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

    public int maintainEnergy(EnergyContainer container) {
        if (container.getStoredEnergy().get() == container.getEnergyCapacity())
            return 0;

        updateConnections();

        if (connectedStructures.isEmpty())
            return 0;

        return container.supplyEnergy(
          consumeEnergy(Math.min(maximumInput, container.getEnergyCapacity() - container.getStoredEnergy().get()))
        );
    }

    public int consumeFluid(Fluid fluid, int amount) {
        int original = amount;
        for (Connection conn : connectedStructures) {
            if (conn.connector.matter != Matter.FLUID)
                continue;

            if (conn.connector.flowDirection != FlowDirection.OUT && conn.connector.flowDirection != FlowDirection.ALL)
                continue;

            FluidContainer container = (FluidContainer) conn.structure;
            if (container.getFluid() != fluid)
                continue;

            amount -= container.consumeFluid(Math.min(amount, conn.connector.maximumOutput));

            if (amount == 0)
                break;
        }

        return original - amount;
    }

    public List<ItemStack> consumeItems(@Nullable ItemCriteria criteria, int amount, boolean onlyFirst) {
        List<ItemStack> result = new ArrayList<>();

        if (connectedStructures.isEmpty()) {
            for (Direction direction : Direction.values()) {
                if (isConnectableFrom(direction) && TorusWorld.isItemContainer(component.absoluteLocation.getRelative(direction))) {
                    BlockInventoryHolder holder = (BlockInventoryHolder) component.absoluteLocation.getRelative(direction).getBlock().getState();
                    consumeItems(criteria, amount, onlyFirst, holder.getInventory().getContents(), result);

                    return result;
                }
            }
        }

        for (Connection conn : connectedStructures) {
            if (conn.connector.matter != Matter.ITEM)
                continue;

            if (conn.connector.flowDirection != FlowDirection.OUT && conn.connector.flowDirection != FlowDirection.ALL)
                continue;

            StructureInventory inventory = conn.connector.linkedInventory;
            ItemStack[] items = inventory.getItems();
            if (!consumeItems(criteria, amount, onlyFirst, items, result))
                break;
        }

        return result;
    }

    private boolean consumeItems(@Nullable ItemCriteria criteria, int amount, boolean onlyFirst, ItemStack[] items, List<ItemStack> results) {
        for (int i = 0; i < items.length; i++) {
            ItemStack item = items[i];
            if (item != null && (criteria == null || criteria.matches(item))) {
                int newAmount = Math.max(0, item.getAmount() - amount);
                amount -= item.getAmount() - newAmount;

                ItemStack resultItem = item.clone();
                resultItem.setAmount(item.getAmount() - newAmount);

                item.setAmount(newAmount);
                if (newAmount == 0)
                    items[i] = null;

                results.add(resultItem);

                if (onlyFirst)
                    return false;
            }
        }
        return true;
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
