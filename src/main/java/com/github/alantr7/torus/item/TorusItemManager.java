package com.github.alantr7.torus.item;

import com.github.alantr7.bukkitplugin.annotations.core.Singleton;
import com.github.alantr7.torus.TorusPlugin;
import com.github.alantr7.torus.structure.Structures;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class TorusItemManager {

    private final Map<String, TorusItem> registry = new HashMap<>();

    {
        registerItem(new TorusItem("torus:solar_generator", Structures.SOLAR_GENERATOR, Material.PAPER, "Solar Generator", Collections.emptyList(), 0));
        registerItem(new TorusItem("torus:block_breaker", Structures.BLOCK_BREAKER, Material.PAPER, "Block Breaker", Collections.emptyList(), 0));
        registerItem(new TorusItem("torus:pump", Structures.PUMP, Material.PAPER, "Pump", Collections.emptyList(), 0));

        registerItem(new TorusItem("torus:energy_cable", Structures.ENERGY_CABLE, Material.PAPER, "Energy Cable", Collections.emptyList(), 0));
        registerItem(new TorusItem("torus:fluid_pipe", Structures.FLUID_CABLE, Material.PAPER, "Fluid Pipe", Collections.emptyList(), 0));
        registerItem(new TorusItem("torus:item_conduit", Structures.ITEM_CABLE, Material.PAPER, "Item Conduit", Collections.emptyList(), 0));
        registerItem(new TorusItem("torus:inventory_interface", Structures.INVENTORY_INTERFACE, Material.PAPER, "Inventory Interface", Collections.emptyList(), 0));

        registerItem(new TorusItem("torus:fluid_tank", Structures.FLUID_TANK, Material.PAPER, "Fluid Tank", Collections.emptyList(), 0));
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

    public Collection<String> getItemIds() {
        return registry.keySet();
    }

    public Collection<TorusItem> getItems() {
        return registry.values();
    }

}
