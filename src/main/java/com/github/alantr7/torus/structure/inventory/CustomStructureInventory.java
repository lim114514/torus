package com.github.alantr7.torus.structure.inventory;

import lombok.Getter;
import org.bukkit.inventory.ItemStack;

public class CustomStructureInventory implements StructureInventory {

    @Getter
    private final ItemStack[] items;

    public CustomStructureInventory(int size) {
        this.items = new ItemStack[size];
    }

    @Override
    public boolean canAdd(ItemStack item) {
        return false;
    }

    @Override
    public void addItem(ItemStack item) {
        for (int i = 0; i < items.length; i++) {
            if (items[i] == null) {
                items[i] = item.clone();
                return;
            }

            if (items[i].isSimilar(item)) {
                int original = items[i].getAmount();
                items[i].setAmount(Math.min(items[i].getMaxStackSize(), original + item.getAmount()));

                int remaining = item.getAmount() - (items[i].getAmount() - original);
                item.setAmount(remaining);

                if (remaining == 0)
                    return;
            }
        }
    }

    @Override
    public int getSize() {
        return 1;
    }

}
