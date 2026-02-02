package com.github.alantr7.torus.structure.component;

import com.github.alantr7.torus.network.NetworkGraph;
import com.github.alantr7.torus.network.Node;
import com.github.alantr7.torus.structure.*;
import com.github.alantr7.torus.utils.EventUtils;
import com.github.alantr7.torus.world.Fluid;
import com.github.alantr7.torus.item.ItemCriteria;
import com.github.alantr7.torus.world.BlockLocation;
import com.github.alantr7.torus.world.Direction;
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

    public NetworkGraph network = NetworkGraph.INIT;

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

    @Getter
    public final Medium medium;

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

    public enum Medium {
        ITEM, ENERGY, FLUID,
        ;
    }

    @Getter
    protected StructureComponent component;

    public Socket(StructureComponent component, int allowedConnections, Medium medium, FlowDirection direction) {
        this.component = component;
        this.allowedConnections = allowedConnections;
        this.flowDirection = direction;
        this.medium = medium;
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

    public int consumeFluid(Fluid fluid, int amount) {
        if (network.isInvalidated())
            return 0;

        int original = amount;
        for (Node conn : network.nodes) {
            if (conn.socket == this)
                continue;

            if (conn.socket.medium != Medium.FLUID)
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

        if (network.isInvalidated() || network.nodes.size() == 1) {
            for (Direction direction : Direction.values()) {
                BlockLocation blockLocation = component.absoluteLocation.getRelative(direction);
                if (isConnectableFrom(direction) && blockLocation.isLoaded() && TorusWorld.isItemContainer(blockLocation)) {
                    BlockInventoryHolder holder = (BlockInventoryHolder) blockLocation.getBlock().getState();
                    consumeItems(criteria, amount1, onlyFirst, holder.getInventory().getContents(), null, result);

                    return result;
                }
            }
        }

        if (network.isInvalidated()) {
            return Collections.emptyList();
        }

        for (Node conn : network.nodes) {
            if (conn.socket == this)
                continue;

            if (conn.socket.medium != Medium.ITEM)
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
                BlockLocation blockLocation = component.absoluteLocation.getRelative(direction);
                if (!blockLocation.isLoaded() || !TorusWorld.isItemContainer(blockLocation))
                    continue;

                BlockInventoryHolder holder = (BlockInventoryHolder) blockLocation.getBlock().getState();
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

        structure.location.world.networkManager.queueLoaded(neighborSocket);
        return true;
    }

}
