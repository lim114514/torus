package com.github.alantr7.torus.structure.inventory;

import org.bukkit.inventory.ItemStack;

public interface StructureInventory {

    ItemStack[] getItems();

    boolean canAdd(ItemStack item);

    void addItem(ItemStack item);

    int getSize();

}
