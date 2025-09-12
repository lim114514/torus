package com.github.alantr7.torus.world;

import com.github.alantr7.bukkitplugin.annotations.core.InvokePeriodically;
import com.github.alantr7.bukkitplugin.annotations.core.Singleton;
import com.github.alantr7.torus.machine.CableInstance;
import com.github.alantr7.torus.machine.SolarGeneratorInstance;
import com.github.alantr7.torus.math.BlockLocation;
import com.github.alantr7.torus.math.Direction;
import com.github.alantr7.torus.structure.EnergyCapacitor;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.structure.Structures;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

@Singleton
public class TorusWorld {

    public static final Map<BlockLocation, StructureInstance> loaded = new HashMap<>();

    public static final Map<BlockLocation, BlockLocation> occupations = new HashMap<>();

    @InvokePeriodically(interval = 20)
    void tickLoadedStructures() {
        loaded.forEach((location, instance) -> instance.tick());

        for (Player player : Bukkit.getOnlinePlayers()) {
            Block block = player.getTargetBlockExact(5);
            if (block == null) {
                player.resetTitle();
                continue;
            }

            BlockLocation occupation = occupations.get(new BlockLocation(block.getLocation()));
            if (occupation == null)
                continue;

            StructureInstance structure = loaded.get(occupation);
            if (structure instanceof EnergyCapacitor generator) {
                player.sendTitle("", structure.structure.getClass().getSimpleName() + " (" + generator.getStoredEnergy() + " / " + generator.getEnergyCapacity() + " RF)", 0, 25, 0);
            }
        }
    }

    public static void placeStructure(StructureInstance instance) {
        loaded.put(instance.location, instance);

        // Place bounds
        int[] bounds = instance.structure.getBounds();
        for (int i = 0; i < bounds.length; i += 3) {
            BlockLocation relative = instance.location.getRelative(bounds[i], bounds[i+1], bounds[i+2]);
            relative.getBlock().setType(Material.BARRIER);

            occupations.put(relative, instance.location);
        }

        // Update all cables around it
        instance.getConnectors().forEach((loc, connector) -> {
            for (Direction direction : Direction.values()) {
                if (connector.isConnectableFrom(direction)) {
                    if (loc.getRelative(direction).getStructure() instanceof CableInstance cable) {
                        Bukkit.broadcastMessage("Machine placed next to a cable. Connector direction: " + connector.getComponent().direction);
                        cable.updateConnections();
                    }
                }
            }
        });
    }

    public static void removeStructure(StructureInstance instance) {
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
                    }
                }
            }
        }

        // Update all cables around connectors
        else instance.getConnectors().forEach((loc, connector) -> {
            for (Direction direction : Direction.values()) {
                if (connector.isConnectableFrom(direction)) {
                    if (loc.getRelative(direction).getStructure() instanceof CableInstance cable) {
                        cable.updateConnections();
                    }
                }
            }
        });

        // Remove models
        instance.getComponents().forEach((name, component) -> component.getModel().remove());
    }

}
