package com.github.alantr7.torus.plugin;

import com.github.alantr7.bukkitplugin.annotations.core.Inject;
import com.github.alantr7.bukkitplugin.annotations.core.Invoke;
import com.github.alantr7.bukkitplugin.annotations.core.Singleton;
import com.github.alantr7.torus.TorusPlugin;
import com.github.alantr7.torus.item.TorusItem;
import com.github.alantr7.torus.world.BlockLocation;
import com.github.alantr7.torus.math.Direction;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.world.TorusWorld;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;

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
        if (cooldowns.getOrDefault(event.getPlayer().getUniqueId(), 0L) > System.currentTimeMillis())
            return;

        TorusItem torusItem = TorusItem.getByItemStack(item);
        if (torusItem == null)
            return;

        if (!torusItem.isPlaceable()) {
            event.setCancelled(true);
            return;
        }

        Block block = event.getClickedBlock().getRelative(event.getBlockFace());
        BlockLocation location = new BlockLocation(block.getLocation());
        Direction direction = event.getBlockFace().getModY() != 0
          ? Direction.fromBlockFace(event.getPlayer().getFacing()).getOpposite()
          : Direction.fromBlockFace(event.getBlockFace());

        cooldowns.put(event.getPlayer().getUniqueId(), System.currentTimeMillis() + 200);

        if (torusItem.getStructure().isPlaceableAt(location, direction)) {
            StructureInstance structure = torusItem.getStructure().place(location, direction);
            if (structure != null)
                structure.setOwnerId(event.getPlayer().getUniqueId());
        } else {
            event.getPlayer().sendMessage(ChatColor.RED + "Not enough space to place the structure here.");
        }
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    void onMachineInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        if (cooldowns.getOrDefault(event.getPlayer().getUniqueId(), 0L) > System.currentTimeMillis())
            return;

        StructureInstance structure = new BlockLocation(event.getClickedBlock().getLocation()).getStructure();
        if (structure != null) {
            cooldowns.put(event.getPlayer().getUniqueId(), System.currentTimeMillis() + 200);
            structure.handlePlayerInteraction(event, new BlockLocation(event.getClickedBlock().getLocation()));
        }
    }

    @EventHandler
    void onMachineBreak(PlayerInteractEvent event) {
        if (event.getAction() != Action.LEFT_CLICK_BLOCK)
            return;

        TorusWorld world = TorusPlugin.getInstance().getWorldManager().getWorld(event.getPlayer().getWorld());
        BlockLocation loc = new BlockLocation(event.getClickedBlock().getLocation());

        StructureInstance structure = world.getStructure(loc);
        if (structure == null)
            return;

        if (structure.testOwnership(event.getPlayer())) {
            world.removeStructure(structure);
        } else {
            event.getPlayer().sendMessage(ChatColor.RED + "You can not break a machine that you do not own.");
        }
    }

    @EventHandler
    void onMachineRotate(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
        if (item.getType() != Material.DEBUG_STICK)
            return;

        if (cooldowns.getOrDefault(event.getPlayer().getUniqueId(), 0L) > System.currentTimeMillis())
            return;

        StructureInstance machine = new BlockLocation(event.getClickedBlock().getLocation()).getStructure();
        if (machine == null)
            return;

        event.setCancelled(true);

        Direction right = switch (machine.direction) {
            case NORTH -> Direction.EAST;
            case EAST -> Direction.SOUTH;
            case SOUTH -> Direction.WEST;
            case WEST -> Direction.NORTH;
            default -> null;
        };

        if (right == null) {
            event.getPlayer().sendMessage("Can not rotate this machine.");
            return;
        }

        machine.remove();
        if (!machine.structure.isPlaceableAt(machine.location, right)) {
            machine.structure.place(machine.location, machine.direction);
            event.getPlayer().sendMessage(ChatColor.RED + "Not enough space to rotate the machine.");
            return;
        }

        machine.structure.place(machine.location, right);

        cooldowns.put(event.getPlayer().getUniqueId(), System.currentTimeMillis() + 200);
        event.getPlayer().sendMessage(ChatColor.YELLOW + "You rotated the machine.");
    }

    @EventHandler
    void onUseTorusItemInVanillaCraftingRecipe(PrepareItemCraftEvent event) {
        if (!(event.getRecipe() instanceof CraftingRecipe recipe))
            return;

        if (!recipe.getKey().getNamespace().equals("minecraft"))
            return;

        for (ItemStack stack : event.getInventory().getMatrix()) {
            if (stack == null)
                continue;

            if (TorusItem.getByItemStack(stack) != null) {
                event.getInventory().setResult(null);
                return;
            }
        }
    }

    @EventHandler
    void onUseTorusItemInVanillaCraftingRecipe(CraftItemEvent event) {
        if (!(event.getRecipe() instanceof CraftingRecipe recipe))
            return;

        if (!recipe.getKey().getNamespace().equals("minecraft"))
            return;

        for (ItemStack stack : event.getInventory().getMatrix()) {
            if (stack == null)
                continue;

            if (TorusItem.getByItemStack(stack) != null) {
                event.setCancelled(true);
                event.getInventory().setResult(null);
                return;
            }
        }
    }

    @Invoke(Invoke.Schedule.AFTER_PLUGIN_ENABLE)
    void registerEvents(@Inject TorusPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

}
