package com.github.alantr7.torus.plugin;

import com.github.alantr7.bukkitplugin.annotations.core.Inject;
import com.github.alantr7.bukkitplugin.annotations.core.Invoke;
import com.github.alantr7.bukkitplugin.annotations.core.Singleton;
import com.github.alantr7.torus.TorusPlugin;
import com.github.alantr7.torus.config.MainConfig;
import com.github.alantr7.torus.item.TorusItem;
import com.github.alantr7.torus.machine.WireConnectorInstance;
import com.github.alantr7.torus.world.BlockLocation;
import com.github.alantr7.torus.world.Direction;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.world.TorusWorld;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityUnleashEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Singleton
public class EventListener implements Listener {

    Map<UUID, Long> placementCooldown = new HashMap<>();
    Map<UUID, Long> interactionCooldown = new HashMap<>();

    public static Map<UUID, WireConnectorInstance> establishingWireConnections = new HashMap<>();

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    void onMachinePlace(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
        if (placementCooldown.getOrDefault(event.getPlayer().getUniqueId(), 0L) > System.currentTimeMillis())
            return;

        TorusItem torusItem = TorusItem.getByItemStack(item);
        if (torusItem == null)
            return;

        if (!torusItem.isPlaceable()) {
            event.setCancelled(true);
            return;
        }

        long cooldownExpiry = System.currentTimeMillis() + 200;
        placementCooldown.put(event.getPlayer().getUniqueId(), cooldownExpiry);
        interactionCooldown.put(event.getPlayer().getUniqueId(), cooldownExpiry);

        if (MainConfig.WORLD_BLACKLIST.contains(event.getClickedBlock().getWorld().getName())) {
            event.getPlayer().sendMessage(ChatColor.RED + "Structures were disabled in this world by server owners.");
            event.setCancelled(true);
            return;
        }

        if (!event.getPlayer().isSneaking() && TorusWorld.isItemContainer(new BlockLocation(event.getClickedBlock().getLocation())))
            return;

        Block block = event.getClickedBlock().getRelative(event.getBlockFace());
        BlockLocation location = new BlockLocation(block.getLocation());

        Direction direction = torusItem.getStructure().isOmnidirectional ? Direction.fromBlockFace(event.getBlockFace()) : event.getBlockFace().getModY() != 0
          ? Direction.fromBlockFace(event.getPlayer().getFacing()).getOpposite()
          : Direction.fromBlockFace(event.getBlockFace());

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

    @EventHandler(priority = EventPriority.HIGH)
    void onMachineInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        if (!TorusPlugin.getInstance().getWorldManager().isWorldSupported(event.getPlayer().getWorld()))
            return;

        if (interactionCooldown.getOrDefault(event.getPlayer().getUniqueId(), 0L) > System.currentTimeMillis())
            return;

        StructureInstance structure = new BlockLocation(event.getClickedBlock().getLocation()).getStructure();
        if (structure != null) {
            interactionCooldown.put(event.getPlayer().getUniqueId(), System.currentTimeMillis() + 200);

            if (event.getPlayer().isSneaking() && event.getItem() != null) {
                TorusItem torusItem = TorusItem.getByItemStack(event.getItem());
                if (torusItem != null) {
                    if (torusItem.isPlaceable())
                        return;
                } else if (event.getItem().getType().isBlock())
                    return;
            }

            if (structure.structure.isInteractable && structure.testOwnership(event.getPlayer())) {
                structure.handlePlayerInteraction(event, new BlockLocation(event.getClickedBlock().getLocation()));
                event.setCancelled(true);

                placementCooldown.put(event.getPlayer().getUniqueId(), System.currentTimeMillis() + 200);
            }
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
            event.setCancelled(true);
        }
    }

    @EventHandler
    void onTorusEntityDamage(EntityDamageEvent event) {
        if (event.getEntity().getScoreboardTags().contains("torus_entity")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    void onLeashDrop(EntityUnleashEvent event) {
        if (event.getEntity().getScoreboardTags().contains("torus_entity")) {
            event.setDropLeash(false);

            if (((LivingEntity) event.getEntity()).getLeashHolder() instanceof Player player) {
                establishingWireConnections.remove(player.getUniqueId());
            } else {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    void onSwitchHotbarItem(PlayerItemHeldEvent event) {
        if (!TorusItem.is(event.getPlayer().getInventory().getItem(event.getNewSlot()), "torus:copper_wire")) {
            WireConnectorInstance connector = establishingWireConnections.remove(event.getPlayer().getUniqueId());
            if (connector != null) {
                connector.connectionCandidate.setLeashHolder(null);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    void onSwitchHotbarItem(InventoryClickEvent event) {
        Bukkit.getScheduler().runTaskLater(TorusPlugin.getInstance(), () -> {
            if (!TorusItem.is(event.getWhoClicked().getInventory().getItemInMainHand(), "torus:copper_wire")) {
                WireConnectorInstance connector = establishingWireConnections.remove(event.getWhoClicked().getUniqueId());
                if (connector != null) {
                    connector.connectionCandidate.setLeashHolder(null);
                }
            }
        }, 1L);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    void onSwitchHotbarItem(InventoryDragEvent event) {
        Bukkit.getScheduler().runTaskLater(TorusPlugin.getInstance(), () -> {
            if (!TorusItem.is(event.getWhoClicked().getInventory().getItemInMainHand(), "torus:copper_wire")) {
                WireConnectorInstance connector = establishingWireConnections.remove(event.getWhoClicked().getUniqueId());
                if (connector != null) {
                    connector.connectionCandidate.setLeashHolder(null);
                }
            }
        }, 1L);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    void onSwitchHotbarItem(PlayerSwapHandItemsEvent event) {
        Bukkit.getScheduler().runTaskLater(TorusPlugin.getInstance(), () -> {
            if (!TorusItem.is(event.getMainHandItem(), "torus:copper_wire")) {
                WireConnectorInstance connector = establishingWireConnections.remove(event.getPlayer().getUniqueId());
                if (connector != null) {
                    connector.connectionCandidate.setLeashHolder(null);
                }
            }
        }, 1L);
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
