package com.github.alantr7.torus.item;

import com.github.alantr7.torus.TorusPlugin;
import com.github.alantr7.torus.structure.Structure;
import com.github.alantr7.torus.structure.Structures;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class TorusItemManager {

    private final Map<String, TorusItem> registry = new HashMap<>();

    private final Map<String, Category> categories = new LinkedHashMap<>();

    {
        // Structure Items
        registerItem(new TorusItem("torus:solar_generator", Category.GENERATORS, Structures.SOLAR_GENERATOR, Material.PAPER, "Solar Generator", Collections.emptyList()));
        registerItem(new TorusItem("torus:coal_generator", Category.GENERATORS, Structures.COAL_GENERATOR, Material.PAPER, "Coal Generator", Collections.emptyList()));

        registerItem(new TorusItem("torus:turret", Category.MACHINES, Structures.TURRET, Material.PAPER, "Turret", Collections.emptyList()));
        registerItem(new TorusItem("torus:blast_furnace", Category.MACHINES, Structures.BLAST_FURNACE, Material.PAPER, "Blast Furnace", Collections.emptyList()));
        registerItem(new TorusItem("torus:block_breaker", Category.MACHINES, Structures.BLOCK_BREAKER, Material.PAPER, "Block Breaker", Collections.emptyList()));
        registerItem(new TorusItem("torus:pump", Category.MACHINES, Structures.PUMP, Material.PAPER, "Pump", Collections.emptyList()));
        registerItem(new TorusItem("torus:ore_crusher", Category.MACHINES, Structures.ORE_CRUSHER, Material.PAPER, "Ore Crusher", Collections.emptyList()));
        registerItem(new TorusItem("torus:ore_washer", Category.MACHINES, Structures.ORE_WASHER, Material.PAPER, "Ore Washer", Collections.emptyList()));
        registerItem(new TorusItem("torus:quarry", Category.MACHINES, Structures.QUARRY, Material.PAPER, "Quarry", Collections.emptyList()));

        registerItem(new TorusItem("torus:energy_cable", Category.NETWORK, Structures.ENERGY_CABLE, Material.PAPER, "Energy Cable", Collections.emptyList()));
        registerItem(new TorusItem("torus:fluid_pipe", Category.NETWORK, Structures.FLUID_CABLE, Material.PAPER, "Fluid Pipe", Collections.emptyList()));
        registerItem(new TorusItem("torus:item_conduit", Category.NETWORK, Structures.ITEM_CABLE, Material.PAPER, "Item Conduit", Collections.emptyList()));
        registerItem(new TorusItem("torus:connector", Category.NETWORK, Structures.CONNECTOR, Material.HEAVY_CORE, "Connector", Collections.emptyList()));

        registerItem(new TorusItem("torus:screwdriver",  Category.TOOLS, null, Material.STICK, "Screwdriver", Collections.emptyList()));
        registerItem(new TorusItem("torus:hammer",  Category.TOOLS, null, Material.STICK, "Hammer", Collections.emptyList()));

        registerItem(new TorusItem("torus:power_bank", Category.STORAGE, Structures.POWER_BANK, Material.PAPER, "Power Bank", Collections.emptyList()));
        registerItem(new TorusItem("torus:fluid_tank", Category.STORAGE, Structures.FLUID_TANK, Material.PAPER, "Fluid Tank", Collections.emptyList()));

        // Regular Items
        registerItem(new TorusItem("torus:iron_dust", Category.RESOURCES, null, Material.DEAD_TUBE_CORAL_FAN, "Iron Dust", Collections.emptyList()));
        registerItem(new TorusItem("torus:copper_dust", Category.RESOURCES, null, Material.GLOWSTONE_DUST, "Copper Dust", Collections.emptyList()));
        registerItem(new TorusItem("torus:gold_dust", Category.RESOURCES, null, Material.HORN_CORAL_FAN, "Gold Dust", Collections.emptyList()));
        registerItem(new TorusItem("torus:steel_ingot", Category.RESOURCES, null, Material.IRON_INGOT, "Steel Ingot", Collections.emptyList()));
        registerItem(new TorusItem("torus:steel_nugget", Category.RESOURCES, null, Material.IRON_NUGGET, "Steel Nugget", Collections.emptyList()));
        registerItem(new TorusItem("torus:machine_block", Category.COMPONENTS, null, new HeadData("http://textures.minecraft.net/texture/944bdc56def9a99e0f775d0c3879a69138d1510a18bbacb0c82902bbf816171c"), "Machine Block", Collections.emptyList()));
        registerItem(new TorusItem("torus:copper_wire", Category.COMPONENTS, null, Material.PITCHER_POD, "Copper Wire", Collections.emptyList()));
        registerItem(new TorusItem("torus:copper_coil", Category.COMPONENTS, null, new HeadData("http://textures.minecraft.net/texture/b3652284921a2dba440060f2f63aa3ba2b0df62c9c36bf7883acedc336df911b"), "Copper Coil", Collections.emptyList()));
        registerItem(new TorusItem("torus:transistor", Category.COMPONENTS, null, Material.COMPARATOR, "Transistor", Collections.emptyList()));
        registerItem(new TorusItem("torus:circuit_board", Category.COMPONENTS, null, Material.PAPER, "Circuit Board", Collections.emptyList()));
        registerItem(new TorusItem("torus:motor", Category.COMPONENTS, null, new HeadData("http://textures.minecraft.net/texture/8cbca012f67e54de9aee72ff424e056c2ae58de5eacc949ab2bcd9683cec"), "Motor", Collections.emptyList()));
        registerItem(new TorusItem("torus:generator", Category.COMPONENTS, null, new HeadData("http://textures.minecraft.net/texture/7b6a3366d21618e244c1a8e49f32feae2924aeaf985c9d16b0b430ccb2a88ffa"), "Generator", Collections.emptyList()));
    }

    {
        categories.put("resources", Category.RESOURCES);
        categories.put("generators", Category.GENERATORS);
        categories.put("machines", Category.MACHINES);
        categories.put("network", Category.NETWORK);
        categories.put("components", Category.COMPONENTS);
        categories.put("storage", Category.STORAGE);
        categories.put("tools", Category.TOOLS);

        Category.RESOURCES.display = getItemById("torus:copper_dust").itemStack;
        Category.GENERATORS.display = new ItemStack(Material.FURNACE);
        Category.MACHINES.display = getItemById("torus:motor").itemStack;
        Category.COMPONENTS.display = getItemById("torus:transistor").itemStack;
        Category.TOOLS.display = getItemById("torus:screwdriver").itemStack;
    }

    public void registerItem(TorusItem item) {
        registry.put(item.namespacedId, item);
        if (item.category != null) {
            item.category.items.add(item);
        }
    }

    public TorusItem getItemById(String id) {
        if (id.startsWith("torus:"))
            return registry.get(id);
        return registry.get("torus:" + id);
    }

    public TorusItem getItemByItemStack(ItemStack item) {
        if (!item.hasItemMeta())
            return null;

        String itemId = item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(TorusPlugin.getInstance(), "torus_item"), PersistentDataType.STRING);
        if (itemId == null)
            return null;

        return registry.get(itemId);
    }

    public TorusItem getItemByStructure(Structure structure) {
        for (TorusItem item : getItems()) {
            if (item.structure == structure)
                return item;
        }
        return null;
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

    public Collection<Category> getCategories() {
        return categories.values();
    }

}
