package com.github.alantr7.torus.world;

import com.github.alantr7.bukkitplugin.annotations.core.InvokePeriodically;
import com.github.alantr7.bukkitplugin.annotations.core.Singleton;
import com.github.alantr7.torus.math.BlockLocation;
import com.github.alantr7.torus.structure.EnergyContainer;
import com.github.alantr7.torus.structure.StructureInstance;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Singleton
public class TorusWorldManager {

    private final Map<UUID, TorusWorld> worlds = new HashMap<>();

    public TorusWorldManager() {
        for (World world : Bukkit.getWorlds()) {
            TorusWorld torusWorld = new TorusWorld(world);
            torusWorld.load();

            worlds.put(world.getUID(), torusWorld);
        }
    }

    public TorusWorld getWorld(World world) {
        return worlds.get(world.getUID());
    }

    public Collection<TorusWorld> getWorlds() {
        return worlds.values();
    }

    @InvokePeriodically(interval = 20)
    void tickLoadedStructures() {
        worlds.forEach((id, world) -> world.getLoaded().forEach((location, instance) -> instance.tick()));

        for (Player player : Bukkit.getOnlinePlayers()) {
            Block block = player.getTargetBlockExact(5);
            if (block == null) {
                player.resetTitle();
                continue;
            }

            StructureInstance structure = worlds.get(player.getWorld().getUID()).getStructure(new BlockLocation(block.getLocation()));
            if (structure instanceof EnergyContainer generator) {
                player.sendTitle("", structure.structure.getClass().getSimpleName() + " (" + generator.getStoredEnergy() + " / " + generator.getEnergyCapacity() + " RF)", 0, 25, 0);
            }
        }
    }

}
