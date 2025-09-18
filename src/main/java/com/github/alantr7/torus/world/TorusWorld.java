package com.github.alantr7.torus.world;

import com.github.alantr7.bukkitplugin.annotations.core.InvokePeriodically;
import com.github.alantr7.bukkitplugin.annotations.core.Singleton;
import com.github.alantr7.torus.machine.CableInstance;
import com.github.alantr7.torus.machine.InventoryInterfaceInstance;
import com.github.alantr7.torus.math.BlockLocation;
import com.github.alantr7.torus.math.Direction;
import com.github.alantr7.torus.structure.EnergyContainer;
import com.github.alantr7.torus.structure.StructureInstance;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Singleton
public class TorusWorld {

    @Getter
    private final World bukkit;

    @Getter
    private final Map<BlockLocation, StructureInstance> loaded = new HashMap<>();

    private static final Map<BlockLocation, BlockLocation> occupations = new HashMap<>();

    public TorusWorld(World bukkit) {
        this.bukkit = bukkit;
    }

    static final Set<Material> MINECRAFT_BLOCK_CONTAINER_TYPES = Set.of(
      Material.CHEST, Material.TRAPPED_CHEST, Material.DROPPER, Material.DISPENSER, Material.HOPPER, Material.SHULKER_BOX
    );
    public static boolean isItemContainer(BlockLocation location) {
        Material material = location.getBlock().getType();
        if (MINECRAFT_BLOCK_CONTAINER_TYPES.contains(material))
            return true;

        if (material.name().endsWith("_SHULKER_BOX"))
            return true;

        return false;
    }

    public StructureInstance getStructure(BlockLocation location) {
        BlockLocation machineLocation = occupations.get(location);
        return machineLocation == null ? null : loaded.get(machineLocation);
    }

    public void placeStructure(StructureInstance instance) {
        loaded.put(instance.location, instance);

        // Place bounds
        int[] bounds = instance.structure.getBounds();
        for (int i = 0; i < bounds.length; i += 3) {
            BlockLocation relative = instance.location.getRelative(bounds[i], bounds[i+1], bounds[i+2]);
            relative.getBlock().setType(Material.BARRIER);

            occupations.put(relative, instance.location);
        }

        // Update all cables if I'm cable
        if (instance instanceof CableInstance cable) {
            for (Direction direction : Direction.values()) {
                if (cable.isConnectableFrom(direction)) {
                    // TODO: Replace with an interface like "Updatable" and call its updateConnections() instead
                    if (instance.location.getRelative(direction).getStructure() instanceof CableInstance cable1) {
                        cable1.updateConnections();
                    }
                    else if (instance.location.getRelative(direction).getStructure() instanceof InventoryInterfaceInstance iii) {
                        iii.updateConnections();
                    }
                }
            }
        }

        // Update all cables around it
        instance.getConnectors().forEach((connector) -> {
            for (Direction direction : Direction.values()) {
                if (connector.isConnectableFrom(direction)) {
                    if (connector.getComponent().absoluteLocation.getRelative(direction).getStructure() instanceof CableInstance cable) {
                        Bukkit.broadcastMessage("Machine placed next to a cable. Connector direction: " + connector.getComponent().direction);
                        cable.updateConnections();
                    }
                }
            }
        });

        if (instance instanceof InventoryInterfaceInstance iii) {
            iii.updateModel();
        }
    }

    public void removeStructure(StructureInstance instance) {
        loaded.remove(instance.location);

        // Remove bounds
        int[] bounds = instance.structure.getBounds();
        for (int i = 0; i < bounds.length; i += 3) {
            BlockLocation relative = instance.location.getRelative(bounds[i], bounds[i+1], bounds[i+2]);
            relative.getBlock().setType(Material.AIR);

            occupations.remove(relative);
        }

        // Update all cables if I'm cable
        if (instance instanceof CableInstance cable) {
            for (Direction direction : Direction.values()) {
                if (cable.isConnectableFrom(direction)) {
                    if (instance.location.getRelative(direction).getStructure() instanceof CableInstance cable1) {
                        cable1.updateConnections();
                    } else if (instance.location.getRelative(direction).getStructure() instanceof InventoryInterfaceInstance iii) {
                        iii.updateConnections();
                    }
                }
            }
        }

        // Update all cables around connectors
        else instance.getConnectors().forEach((connector) -> {
            for (Direction direction : Direction.values()) {
                if (connector.isConnectableFrom(direction)) {
                    if (connector.getComponent().absoluteLocation.getRelative(direction).getStructure() instanceof CableInstance cable) {
                        cable.updateConnections();
                    }
                }
            }
        });

        // Remove models
        instance.getComponents().forEach((component) -> {
            if (component.getModel() != null)
                component.getModel().remove();
        });
    }

}
