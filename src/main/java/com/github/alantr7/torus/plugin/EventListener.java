package com.github.alantr7.torus.plugin;

import com.github.alantr7.bukkitplugin.annotations.core.Inject;
import com.github.alantr7.bukkitplugin.annotations.core.Invoke;
import com.github.alantr7.bukkitplugin.annotations.core.Singleton;
import com.github.alantr7.torus.TorusPlugin;
import com.github.alantr7.torus.item.TorusItem;
import com.github.alantr7.torus.world.BlockLocation;
import com.github.alantr7.torus.world.Direction;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.world.TorusWorld;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Singleton
public class EventListener implements Listener {

    Map<UUID, Long> cooldowns = new HashMap<>();

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
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
            if (structure != null) {
                structure.setOwnerId(event.getPlayer().getUniqueId());
                if (structure.structure.isHeavy) {
                    structure.location.world.getBukkit().playSound(location.toBukkit().add(.5, 0, .5), Sound.BLOCK_ANVIL_PLACE, 0.2f, 1f);
                }
                if (event.getPlayer().getGameMode() == GameMode.SURVIVAL || event.getPlayer().getGameMode() == GameMode.ADVENTURE) {
                    item.setAmount(item.getAmount() - 1);
                }
            }
        } else {
            event.getPlayer().sendMessage(ChatColor.RED + "Not enough space to place the structure here.");
        }
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    void onMachineInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        if (cooldowns.getOrDefault(event.getPlayer().getUniqueId(), 0L) > System.currentTimeMillis())
            return;

        StructureInstance structure = new BlockLocation(event.getClickedBlock().getLocation()).getStructure();
        if (structure != null) {
            cooldowns.put(event.getPlayer().getUniqueId(), System.currentTimeMillis() + 200);
            if (structure.testOwnership(event.getPlayer())) {
                structure.handlePlayerInteraction(event, new BlockLocation(event.getClickedBlock().getLocation()));
            }
            event.setCancelled(true);
        }
    }

    @EventHandler
    void onMachineBreak(PlayerInteractEvent event) {
        if (event.getAction() != Action.LEFT_CLICK_BLOCK)
            return;

        TorusWorld world = TorusPlugin.getInstance().getWorldManager().getWorld(event.getPlayer().getWorld());
        BlockLocation loc = new BlockLocation(event.getClickedBlock().getLocation());

        StructureInstance instance = world.getStructure(loc);
        if (instance == null)
            return;

        if (instance.structure.isHeavy) {
            TorusItem item = TorusItem.getByItemStack(event.getPlayer().getInventory().getItemInMainHand());
            if (item == null || !item.namespacedId.equals("torus:hammer")) {
                event.getPlayer().sendMessage(ChatColor.RED + "This structure can be broken only with hammer.");
                event.setCancelled(true);
                return;
            }
        }

        if (instance.testOwnership(event.getPlayer())) {
            if (event.getPlayer().getGameMode() == GameMode.SURVIVAL || event.getPlayer().getGameMode() == GameMode.ADVENTURE) {
                TorusItem drop = TorusPlugin.getInstance().getItemManager().getItemByStructure(instance.structure);
                if (drop != null) {
                    world.getBukkit().dropItem(loc.toBukkit().add(.5, 0, .5), drop.toItemStack().clone());
                }
            }
            world.removeStructure(instance);
            if (instance.structure.isHeavy) {
                world.getBukkit().playSound(loc.toBukkit().add(.5, 0, .5), Sound.BLOCK_ANVIL_USE, 0.2f, 1.2f);
            }
        } else {
            event.getPlayer().sendMessage(ChatColor.RED + "You can not break a structure that you do not own.");
        }
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
