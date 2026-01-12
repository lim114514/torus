package com.github.alantr7.torus.structure.component;

import com.github.alantr7.torus.network.NetworkGraph;
import com.github.alantr7.torus.network.Node;
import com.github.alantr7.torus.structure.Conductor;
import com.github.alantr7.torus.world.Fluid;
import com.github.alantr7.torus.item.ItemCriteria;
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
        if (network.isInvalidated())
            return Collections.emptyList();

        List<ItemStack> result = new ArrayList<>();
        AtomicInteger amount1 = new AtomicInteger(amount);

        if (network.nodes.size() == 1) {
            for (Direction direction : Direction.values()) {
                if (isConnectableFrom(direction) && TorusWorld.isItemContainer(component.absoluteLocation.getRelative(direction))) {
                    BlockInventoryHolder holder = (BlockInventoryHolder) component.absoluteLocation.getRelative(direction).getBlock().getState();
                    consumeItems(criteria, amount1, onlyFirst, holder.getInventory().getContents(), null, result);

                    return result;
                }
            }
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

}
