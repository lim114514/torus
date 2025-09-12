package com.github.alantr7.torus.plugin;

import com.github.alantr7.bukkitplugin.annotations.core.Inject;
import com.github.alantr7.bukkitplugin.annotations.core.Invoke;
import com.github.alantr7.bukkitplugin.annotations.core.Singleton;
import com.github.alantr7.torus.TorusPlugin;
import com.github.alantr7.torus.math.BlockLocation;
import com.github.alantr7.torus.math.Direction;
import com.github.alantr7.torus.structure.Structure;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.structure.Structures;
import com.github.alantr7.torus.world.TorusWorld;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Singleton
public class EventListener implements Listener {

    Map<UUID, Long> cooldowns = new HashMap<>();

    @EventHandler
    void onMachinePlace(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
        if (item.getType() != Material.STICK)
            return;

        if (cooldowns.getOrDefault(event.getPlayer().getUniqueId(), 0L) > System.currentTimeMillis())
            return;;

        Block block = event.getClickedBlock().getRelative(event.getBlockFace());

        Structure structure;
        if (item.getItemMeta().hasEnchant(Enchantment.SILK_TOUCH)) {
            structure = Structures.SOLAR_GENERATOR;
        }
        else if (item.getItemMeta().hasEnchant(Enchantment.DEPTH_STRIDER)) {
            structure = Structures.BLOCK_BREAKER;
        }
        else if (item.getItemMeta().hasEnchant(Enchantment.AQUA_AFFINITY)) {
            structure = Structures.ENERGY_CABLE;
        }
        else if (item.getItemMeta().hasEnchant(Enchantment.CHANNELING)) {
            structure = Structures.ITEM_CABLE;
        }
        else return;

        BlockLocation location = new BlockLocation(block.getLocation());
        Direction direction = Direction.NORTH;

        StructureInstance instance = structure.instantiate(location, direction);
        instance.create();

        TorusWorld.placeStructure(instance);
        cooldowns.put(event.getPlayer().getUniqueId(), System.currentTimeMillis() + 200);
    }

    @EventHandler
    void onMachineBreak(PlayerInteractEvent event) {
        if (event.getAction() != Action.LEFT_CLICK_BLOCK)
            return;

        BlockLocation loc = TorusWorld.occupations.get(new BlockLocation(event.getClickedBlock().getLocation()));
        if (loc == null)
            return;

        StructureInstance structure = TorusWorld.loaded.get(loc);
        if (structure != null) {
            TorusWorld.removeStructure(structure);
        }
    }

    @Invoke(Invoke.Schedule.AFTER_PLUGIN_ENABLE)
    void registerEvents(@Inject TorusPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

}
