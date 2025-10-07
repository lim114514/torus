package com.github.alantr7.torus.item;

import com.github.alantr7.torus.TorusPlugin;
import com.github.alantr7.torus.structure.Structures;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class TorusItemManager {

    private final Map<String, TorusItem> registry = new HashMap<>();

    {
        // Structure Items
        registerItem(new TorusItem("torus:solar_generator", Structures.SOLAR_GENERATOR, Material.PAPER, "Solar Generator", Collections.emptyList()));
        registerItem(new TorusItem("torus:coal_generator", Structures.COAL_GENERATOR, Material.PAPER, "Coal Generator", Collections.emptyList()));
        registerItem(new TorusItem("torus:power_bank", Structures.POWER_BANK, Material.PAPER, "Power Bank", Collections.emptyList()));

        registerItem(new TorusItem("torus:block_breaker", Structures.BLOCK_BREAKER, Material.PAPER, "Block Breaker", Collections.emptyList()));
        registerItem(new TorusItem("torus:pump", Structures.PUMP, Material.PAPER, "Pump", Collections.emptyList()));
        registerItem(new TorusItem("torus:ore_crusher", Structures.ORE_CRUSHER, Material.PAPER, "Ore Crusher", Collections.emptyList()));
        registerItem(new TorusItem("torus:ore_washer", Structures.ORE_WASHER, Material.PAPER, "Ore Washer", Collections.emptyList()));

        registerItem(new TorusItem("torus:energy_cable", Structures.ENERGY_CABLE, Material.PAPER, "Energy Cable", Collections.emptyList()));
        registerItem(new TorusItem("torus:fluid_pipe", Structures.FLUID_CABLE, Material.PAPER, "Fluid Pipe", Collections.emptyList()));
        registerItem(new TorusItem("torus:item_conduit", Structures.ITEM_CABLE, Material.PAPER, "Item Conduit", Collections.emptyList()));
        registerItem(new TorusItem("torus:inventory_interface", Structures.INVENTORY_INTERFACE, Material.PAPER, "Inventory Interface", Collections.emptyList()));

        registerItem(new TorusItem("torus:fluid_tank", Structures.FLUID_TANK, Material.PAPER, "Fluid Tank", Collections.emptyList()));

        // Regular Items
        registerItem(new TorusItem("torus:iron_dust", null, Material.DEAD_TUBE_CORAL_FAN, "Iron Dust", Collections.emptyList()));
        registerItem(new TorusItem("torus:copper_dust", null, Material.GLOWSTONE_DUST, "Copper Dust", Collections.emptyList()));
        registerItem(new TorusItem("torus:gold_dust", null, Material.HORN_CORAL_FAN, "Gold Dust", Collections.emptyList()));
        registerItem(new TorusItem("torus:copper_wire", null, Material.PITCHER_POD, "Copper Wire", Collections.emptyList()));
        registerItem(new TorusItem("torus:transistor", null, Material.COMPARATOR, "Transistor", Collections.emptyList()));
        registerItem(new TorusItem("torus:circuit_board", null, Material.PAPER, "Circuit Board", Collections.emptyList()));
    }

    public void registerItem(TorusItem item) {
        registry.put(item.namespacedId, item);
    }

    public TorusItem getItemById(String id) {
        return registry.get(id);
    }

    public TorusItem getItemByItemStack(ItemStack item) {
        if (!item.hasItemMeta())
            return null;

        String itemId = item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(TorusPlugin.getInstance(), "torus_item"), PersistentDataType.STRING);
        if (itemId == null)
            return null;

        return registry.get(itemId);
    }

    // TODO: Implement item providers system to support other plugins
    public ItemStack getItemStackByReference(ItemReference reference) {
        if (reference.providerId.equals("minecraft")) {
            try {
                return new ItemStack(Material.valueOf(reference.itemId));
            } catch (Exception ex) {
                return null;
            }
        }
        TorusItem item = getItemById("torus:" + reference.itemId.toLowerCase());
        return item != null ? item.toItemStack().clone() : null;
    }

    @NotNull
    public ItemReference createItemReference(@NotNull ItemStack item) {
        // TODO: Implement item providers system to support other plugins
        TorusItem torusItem = getItemByItemStack(item);
        return torusItem != null
          ? new ItemReference("torus", torusItem.namespacedId.substring("torus:".length()))
          : new ItemReference("minecraft", item.getType().name());
    }

    public Collection<String> getItemIds() {
        return registry.keySet();
    }

    public Collection<TorusItem> getItems() {
        return registry.values();
    }

}
