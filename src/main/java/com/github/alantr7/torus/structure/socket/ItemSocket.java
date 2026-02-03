package com.github.alantr7.torus.structure.socket;

import com.github.alantr7.torus.item.ItemCriteria;
import com.github.alantr7.torus.network.Node;
import com.github.alantr7.torus.structure.component.StructureComponent;
import com.github.alantr7.torus.structure.inventory.StructureInventory;
import com.github.alantr7.torus.world.BlockLocation;
import com.github.alantr7.torus.world.Direction;
import com.github.alantr7.torus.world.TorusWorld;
import org.bukkit.inventory.BlockInventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ItemSocket extends Socket {

    public StructureInventory linkedInventory;

    public int[] linkedInventoryAllowedSlots;

    private static final int[] LINKED_INVENTORY_ALLOWED_SLOTS_FALLBACK_ARRAY = new int[54];
    static {
        for (int i = 0; i < 54; i++) {
            LINKED_INVENTORY_ALLOWED_SLOTS_FALLBACK_ARRAY[i] = i;
        }
    }

    public ItemSocket(StructureComponent component, int allowedConnections, FlowDirection direction) {
        super(component, allowedConnections, Medium.ITEM, direction);
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

            StructureInventory inventory = ((ItemSocket) conn.socket).linkedInventory;
            if (inventory == null)
                continue;

            ItemStack[] items = inventory.getItems();
            if (!consumeItems(criteria, amount1, onlyFirst, items, ((ItemSocket) conn.socket).linkedInventoryAllowedSlots, result))
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

}
