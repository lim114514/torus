package com.github.alantr7.torus.addon;

import com.github.alantr7.torus.TorusPlugin;
import com.github.alantr7.torus.api.addon.LifecycleAdapter;
import com.github.alantr7.torus.api.addon.TorusAddon;
import com.github.alantr7.torus.item.Category;
import com.github.alantr7.torus.item.HeadData;
import com.github.alantr7.torus.item.ItemRegistry;
import com.github.alantr7.torus.item.TorusItem;
import com.github.alantr7.torus.recipe.TorusRecipeManager;
import com.github.alantr7.torus.structure.StructureRegistry;
import com.github.alantr7.torus.structure.Structures;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;

public class DefaultAddonLifecycleAdapter extends LifecycleAdapter {

    public DefaultAddonLifecycleAdapter(TorusAddon addon) {
        super(addon);
    }

    @Override
    public void registerStructures(StructureRegistry registry) {
        registry.registerAndInitialize(Structures.BLAST_FURNACE);
        registry.registerAndInitialize(Structures.BLOCK_BREAKER);
        registry.registerAndInitialize(Structures.PUMP);
        registry.registerAndInitialize(Structures.ORE_CRUSHER);
        registry.registerAndInitialize(Structures.ORE_WASHER);
        registry.registerAndInitialize(Structures.QUARRY);

        registry.registerAndInitialize(Structures.ENERGY_CABLE);
        registry.registerAndInitialize(Structures.ITEM_CABLE);
        registry.registerAndInitialize(Structures.FLUID_CABLE);

        registry.registerAndInitialize(Structures.POWER_POLE);
        registry.registerAndInitialize(Structures.CONNECTOR);
        registry.registerAndInitialize(Structures.WIRE_CONNECTOR);
        registry.registerAndInitialize(Structures.WIRE_RELAY);
        registry.registerAndInitialize(Structures.ELECTRICITY_METER);

        registry.registerAndInitialize(Structures.POWER_BANK);
        registry.registerAndInitialize(Structures.FLUID_TANK);

        registry.registerAndInitialize(Structures.TURRET);

        registry.registerAndInitialize(Structures.COAL_GENERATOR);
        registry.registerAndInitialize(Structures.SOLAR_GENERATOR);
        registry.registerAndInitialize(Structures.WINDMILL);
    }

    @Override
    public void registerItems(ItemRegistry registry) {
        // Structure Items
        registry.registerItem(new TorusItem(TorusPlugin.DEFAULT_ADDON, "solar_generator", Category.GENERATORS, Structures.SOLAR_GENERATOR, Material.PAPER, "Solar Generator", Collections.emptyList()));
        registry.registerItem(new TorusItem(TorusPlugin.DEFAULT_ADDON, "coal_generator", Category.GENERATORS, Structures.COAL_GENERATOR, Material.PAPER, "Coal Generator", Collections.emptyList()));
        registry.registerItem(new TorusItem(TorusPlugin.DEFAULT_ADDON, "windmill", Category.GENERATORS, Structures.WINDMILL, Material.PAPER, "Windmill", Collections.emptyList()));

        registry.registerItem(new TorusItem(TorusPlugin.DEFAULT_ADDON, "turret", Category.MACHINES, Structures.TURRET, Material.PAPER, "Turret", Collections.emptyList()));
        registry.registerItem(new TorusItem(TorusPlugin.DEFAULT_ADDON, "blast_furnace", Category.MACHINES, Structures.BLAST_FURNACE, Material.PAPER, "Blast Furnace", Collections.emptyList()));
        registry.registerItem(new TorusItem(TorusPlugin.DEFAULT_ADDON, "block_breaker", Category.MACHINES, Structures.BLOCK_BREAKER, Material.PAPER, "Block Breaker", Collections.emptyList()));
        registry.registerItem(new TorusItem(TorusPlugin.DEFAULT_ADDON, "pump", Category.MACHINES, Structures.PUMP, Material.PAPER, "Pump", Collections.emptyList()));
        registry.registerItem(new TorusItem(TorusPlugin.DEFAULT_ADDON, "ore_crusher", Category.MACHINES, Structures.ORE_CRUSHER, Material.PAPER, "Ore Crusher", Collections.emptyList()));
        registry.registerItem(new TorusItem(TorusPlugin.DEFAULT_ADDON, "ore_washer", Category.MACHINES, Structures.ORE_WASHER, Material.PAPER, "Ore Washer", Collections.emptyList()));
        registry.registerItem(new TorusItem(TorusPlugin.DEFAULT_ADDON, "quarry", Category.MACHINES, Structures.QUARRY, Material.PAPER, "Quarry", Collections.emptyList()));

        registry.registerItem(new TorusItem(TorusPlugin.DEFAULT_ADDON, "energy_cable", Category.NETWORK, Structures.ENERGY_CABLE, Material.PAPER, "Energy Cable", Collections.emptyList()));
        registry.registerItem(new TorusItem(TorusPlugin.DEFAULT_ADDON, "fluid_pipe", Category.NETWORK, Structures.FLUID_CABLE, Material.PAPER, "Fluid Pipe", Collections.emptyList()));
        registry.registerItem(new TorusItem(TorusPlugin.DEFAULT_ADDON, "item_conduit", Category.NETWORK, Structures.ITEM_CABLE, Material.PAPER, "Item Conduit", Collections.emptyList()));

        registry.registerItem(new TorusItem(TorusPlugin.DEFAULT_ADDON, "power_pole", Category.NETWORK, Structures.POWER_POLE, Material.SPRUCE_FENCE, "Power Pole", Collections.emptyList()));
        registry.registerItem(new TorusItem(TorusPlugin.DEFAULT_ADDON, "connector", Category.NETWORK, Structures.CONNECTOR, Material.HEAVY_CORE, "Connector", Collections.emptyList()));
        registry.registerItem(new TorusItem(TorusPlugin.DEFAULT_ADDON, "wire_connector", Category.NETWORK, Structures.WIRE_CONNECTOR, Material.HEAVY_CORE, "Wire Connector", Collections.emptyList()));
        registry.registerItem(new TorusItem(TorusPlugin.DEFAULT_ADDON, "wire_relay", Category.NETWORK, Structures.WIRE_RELAY, Material.HEAVY_CORE, "Wire Relay", Collections.emptyList()));
        registry.registerItem(new TorusItem(TorusPlugin.DEFAULT_ADDON, "electricity_meter", Category.NETWORK, Structures.ELECTRICITY_METER, Material.OBSERVER, "Electricity Meter", Collections.emptyList()));

        registry.registerItem(new TorusItem(TorusPlugin.DEFAULT_ADDON, "screwdriver",  Category.TOOLS, null, Material.STICK, "Screwdriver", Collections.emptyList()));
        registry.registerItem(new TorusItem(TorusPlugin.DEFAULT_ADDON, "hammer",  Category.TOOLS, null, Material.STICK, "Hammer", Collections.emptyList()));

        registry.registerItem(new TorusItem(TorusPlugin.DEFAULT_ADDON, "power_bank", Category.STORAGE, Structures.POWER_BANK, Material.PAPER, "Power Bank", Collections.emptyList()));
        registry.registerItem(new TorusItem(TorusPlugin.DEFAULT_ADDON, "fluid_tank", Category.STORAGE, Structures.FLUID_TANK, Material.PAPER, "Fluid Tank", Collections.emptyList()));

        // Regular Items
        registry.registerItem(new TorusItem(TorusPlugin.DEFAULT_ADDON, "iron_dust", Category.RESOURCES, null, Material.DEAD_TUBE_CORAL_FAN, "Iron Dust", Collections.emptyList()));
        registry.registerItem(new TorusItem(TorusPlugin.DEFAULT_ADDON, "copper_dust", Category.RESOURCES, null, Material.GLOWSTONE_DUST, "Copper Dust", Collections.emptyList()));
        registry.registerItem(new TorusItem(TorusPlugin.DEFAULT_ADDON, "gold_dust", Category.RESOURCES, null, Material.HORN_CORAL_FAN, "Gold Dust", Collections.emptyList()));
        registry.registerItem(new TorusItem(TorusPlugin.DEFAULT_ADDON, "steel_ingot", Category.RESOURCES, null, Material.IRON_INGOT, "Steel Ingot", Collections.emptyList()));
        registry.registerItem(new TorusItem(TorusPlugin.DEFAULT_ADDON, "steel_nugget", Category.RESOURCES, null, Material.IRON_NUGGET, "Steel Nugget", Collections.emptyList()));
        registry.registerItem(new TorusItem(TorusPlugin.DEFAULT_ADDON, "machine_block", Category.COMPONENTS, null, new HeadData("http://textures.minecraft.net/texture/944bdc56def9a99e0f775d0c3879a69138d1510a18bbacb0c82902bbf816171c"), "Machine Block", Collections.emptyList()));
        registry.registerItem(new TorusItem(TorusPlugin.DEFAULT_ADDON, "copper_wire", Category.COMPONENTS, null, Material.PITCHER_POD, "Copper Wire", Collections.emptyList()));
        registry.registerItem(new TorusItem(TorusPlugin.DEFAULT_ADDON, "copper_coil", Category.COMPONENTS, null, new HeadData("http://textures.minecraft.net/texture/b3652284921a2dba440060f2f63aa3ba2b0df62c9c36bf7883acedc336df911b"), "Copper Coil", Collections.emptyList()));
        registry.registerItem(new TorusItem(TorusPlugin.DEFAULT_ADDON, "transistor", Category.COMPONENTS, null, Material.COMPARATOR, "Transistor", Collections.emptyList()));
        registry.registerItem(new TorusItem(TorusPlugin.DEFAULT_ADDON, "circuit_board", Category.COMPONENTS, null, Material.PAPER, "Circuit Board", Collections.emptyList()));
        registry.registerItem(new TorusItem(TorusPlugin.DEFAULT_ADDON, "motor", Category.COMPONENTS, null, new HeadData("http://textures.minecraft.net/texture/8cbca012f67e54de9aee72ff424e056c2ae58de5eacc949ab2bcd9683cec"), "Motor", Collections.emptyList()));
        registry.registerItem(new TorusItem(TorusPlugin.DEFAULT_ADDON, "generator", Category.COMPONENTS, null, new HeadData("http://textures.minecraft.net/texture/7b6a3366d21618e244c1a8e49f32feae2924aeaf985c9d16b0b430ccb2a88ffa"), "Generator", Collections.emptyList()));
        registry.registerItem(new TorusItem(TorusPlugin.DEFAULT_ADDON, "windmill_blade", Category.COMPONENTS, null, Material.WHITE_BANNER, "Windmill Blade", Collections.emptyList()));

        Category.RESOURCES.display = registry.getItemById("torus:copper_dust").toItemStack();
        Category.GENERATORS.display = new ItemStack(Material.FURNACE);
        Category.MACHINES.display = registry.getItemById("torus:motor").toItemStack();
        Category.COMPONENTS.display = registry.getItemById("torus:transistor").toItemStack();
        Category.TOOLS.display = registry.getItemById("torus:screwdriver").toItemStack();
    }

    @Override
    public void registerRecipes(TorusRecipeManager registry) {
        super.registerRecipes(registry);
    }

}