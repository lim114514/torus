package com.github.alantr7.torus.structure.inventory;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class BukkitStructureInventory implements StructureInventory {

    private final Inventory inventory;

    public BukkitStructureInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    @Override
    public ItemStack[] getItems() {
        return inventory.getContents();
    }

    @Override
    public boolean canAdd(ItemStack item) {
        return true;
    }

    @Override
    public void addItem(ItemStack item) {
        inventory.addItem(item);
    }

    @Override
    public int getSize() {
        return inventory.getSize();
    }
}
