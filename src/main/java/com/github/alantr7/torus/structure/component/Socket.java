package com.github.alantr7.torus.structure.component;

import com.github.alantr7.torus.machine.WireConnectorInstance;
import com.github.alantr7.torus.structure.Conductor;
import com.github.alantr7.torus.world.Fluid;
import com.github.alantr7.torus.item.ItemCriteria;
import com.github.alantr7.torus.machine.CableInstance;
import com.github.alantr7.torus.world.BlockLocation;
import com.github.alantr7.torus.world.Direction;
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
import java.util.concurrent.atomic.AtomicInteger;

public class Socket implements Connectable, Conductor {

    public StructureInstance structure;

    @Getter @Setter
    protected int connections;

    @Getter @Setter
    protected int allowedConnections;

    public int maximumInput = 100;

    public int maximumOutput = 100;

    public List<Connection> networkConnections = Collections.emptyList();

    protected int networkUpdateTick = -1;

    public StructureInventory linkedInventory;

    public int[] linkedInventoryAllowedSlots;

    private static final int[] LINKED_INVENTORY_ALLOWED_SLOTS_FALLBACK_ARRAY = new int[54];
    static {
        for (int i = 0; i < 54; i++) {
            LINKED_INVENTORY_ALLOWED_SLOTS_FALLBACK_ARRAY[i] = i;
        }
    }

    @Getter @Setter
    protected FlowDirection flowDirection;

    public final Matter matter;

    @Override
    public Collection<BlockLocation> getConnectedNodes() {
        List<BlockLocation> nodes = new ArrayList<>();
        for (Direction direction : Direction.values()) {
            if (isConnected(direction))
                nodes.add(component.absoluteLocation.getRelative(direction));
        }

        return nodes;
    }

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

    public Socket(StructureComponent component, int allowedConnections, Matter matter, FlowDirection direction) {
        this.component = component;
        this.allowedConnections = allowedConnections;
        this.flowDirection = direction;
        this.matter = matter;
    }

    public void updateNetwork() {
        // Skip network update if it was already done
        if (networkUpdateTick == component.absoluteLocation.world.getTicks())
            return;

        int directionsCount = 0;
        int closedDirectionsCount = 0;
        List<Connection> networkConnections = new ArrayList<>();

        if (structure != null) {
            networkConnections.add(new Connection(structure, this));
        }

        // Check if connector is directly connected to another
        for (Direction direction : Direction.values()) {
            if (!isConnected(direction))
                continue;

            directionsCount++;
            StructureInstance neighbor = component.absoluteLocation.getRelative(direction).getStructure();
            if (neighbor == null) {
                closedDirectionsCount++;
                continue;
            }

            if (neighbor instanceof CableInstance || neighbor instanceof WireConnectorInstance) {
                continue;
            }

            Socket neighborSocket = neighbor.getSocket(component.absoluteLocation, matter);
            if (neighborSocket != null) {
                networkConnections.add(new Connection(neighbor, neighborSocket));
                closedDirectionsCount++;
            }
        }

        if (directionsCount == closedDirectionsCount) {
            this.networkConnections = networkConnections;
            return;
        }

        List<BlockLocation> open = new LinkedList<>();
        List<BlockLocation> closed = new LinkedList<>();

        closed.add(component.absoluteLocation);
        for (Direction direction : Direction.values()) {
            if (isConnected(direction)) {
                if (component.absoluteLocation.getRelative(direction).getStructure() instanceof Conductor conductor)
                    open.add(((StructureInstance) conductor).location);
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
                if (neighbor instanceof Conductor) {
                    if (!open.contains(neighborLoc)) {
                        open.add(neighborLoc);
                    }
                    continue;
                }

                Socket socket = neighbor.getSocket(start, matter);
                if (socket != null) {
                    networkConnections.add(new Connection(neighbor, socket));
                    closed.add(neighborLoc);

                    socket.networkConnections = networkConnections;
                    socket.networkUpdateTick = component.absoluteLocation.world.getTicks();
                }
            }

            closed.add(start);
        }

        this.networkConnections = networkConnections;
        this.networkUpdateTick = component.absoluteLocation.world.getTicks();
    }

    public int consumeEnergy(int amount) {
        int original = amount;
        for (Connection conn : networkConnections) {
            if (conn.socket == this)
                continue;

            if (conn.socket.matter != Matter.ENERGY)
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
        if (container.getStoredEnergy().get() == container.getEnergyCapacity())
            return 0;

        updateNetwork();

        if (networkConnections.isEmpty() || (networkConnections.size() == 1 && networkConnections.getFirst().socket == this))
            return 0;

        return container.supplyEnergy(
          consumeEnergy(Math.min(maximumInput, container.getEnergyCapacity() - container.getStoredEnergy().get()))
        );
    }

    public int consumeFluid(Fluid fluid, int amount) {
        int original = amount;
        for (Connection conn : networkConnections) {
            if (conn.socket == this)
                continue;

            if (conn.socket.matter != Matter.FLUID)
                continue;

            if (conn.socket.flowDirection != FlowDirection.OUT && conn.socket.flowDirection != FlowDirection.ALL)
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

    public List<ItemStack> consumeItems(@Nullable ItemCriteria criteria, int amount, boolean onlyFirst) {
        List<ItemStack> result = new ArrayList<>();
        AtomicInteger amount1 = new AtomicInteger(amount);

        if (networkConnections.size() == 1) {
            for (Direction direction : Direction.values()) {
                if (isConnectableFrom(direction) && TorusWorld.isItemContainer(component.absoluteLocation.getRelative(direction))) {
                    BlockInventoryHolder holder = (BlockInventoryHolder) component.absoluteLocation.getRelative(direction).getBlock().getState();
                    consumeItems(criteria, amount1, onlyFirst, holder.getInventory().getContents(), null, result);

                    return result;
                }
            }
        }

        for (Connection conn : networkConnections) {
            if (conn.socket == this)
                continue;

            if (conn.socket.matter != Matter.ITEM)
                continue;

            if (conn.socket.flowDirection != FlowDirection.OUT && conn.socket.flowDirection != FlowDirection.ALL)
                continue;

            StructureInventory inventory = conn.socket.linkedInventory;
            if (inventory == null)
                continue;

            ItemStack[] items = inventory.getItems();
            if (!consumeItems(criteria, amount1, onlyFirst, items, conn.socket.linkedInventoryAllowedSlots, result))
                break;
        }

        return result;
    }

    private boolean consumeItems(@Nullable ItemCriteria criteria, AtomicInteger amount, boolean onlyFirst, ItemStack[] items, int[] slots, List<ItemStack> results) {
        int len;
        if (slots == null) {
            slots = LINKED_INVENTORY_ALLOWED_SLOTS_FALLBACK_ARRAY;
            len = items.length;
        } else {
            len = slots.length;
        }

        for (int k = 0; k < len; k++) {
            int i = slots[k];
            ItemStack item = items[i];
            if (item != null && (criteria == null || criteria.matches(item))) {
                int newAmount = Math.max(0, item.getAmount() - amount.get());
                amount.addAndGet(-(item.getAmount() - newAmount));

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

    public void attemptDirectItemExport() {
        if (linkedInventory == null)
            return;

        for (Direction direction : Direction.values()) {
            if (isConnectableFrom(direction)) {
                if (!TorusWorld.isItemContainer(component.absoluteLocation.getRelative(direction)))
                    continue;

                BlockInventoryHolder holder = (BlockInventoryHolder) component.absoluteLocation.getRelative(direction).getBlock().getState();
                ItemStack[] items = linkedInventory.getItems();

                int[] slots = linkedInventoryAllowedSlots != null ? linkedInventoryAllowedSlots : LINKED_INVENTORY_ALLOWED_SLOTS_FALLBACK_ARRAY;
                int len = slots == LINKED_INVENTORY_ALLOWED_SLOTS_FALLBACK_ARRAY ? items.length : linkedInventoryAllowedSlots.length;

                for (int k = 0; k < len; k++) {
                    int i = slots[k];
                    ItemStack item = items[i];
                    if (item != null) {
                        holder.getInventory().addItem(item.clone());
                        item.setAmount(0);
                        items[i] = null;
                    }
                }

                break;
            }
        }
    }

    public static class Connection {

        public final StructureInstance structure;

        public final Socket socket;

        public Connection(StructureInstance structure, Socket socket) {
            this.structure = structure;
            this.socket = socket;
        }

    }

}
