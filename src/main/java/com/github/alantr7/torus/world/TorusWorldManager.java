package com.github.alantr7.torus.world;

import com.github.alantr7.bukkitplugin.annotations.core.InvokePeriodically;
import com.github.alantr7.bukkitplugin.annotations.core.Singleton;
import com.github.alantr7.torus.math.BlockLocation;
import com.github.alantr7.torus.structure.EnergyContainer;
import com.github.alantr7.torus.structure.StructureInstance;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
        worlds.forEach((id, world) -> {
            boolean isDirty = false;
            for (Map.Entry<BlockLocation, StructureInstance> entry : world.getLoaded().entrySet()) {
                StructureInstance instance = entry.getValue();
                if (instance.getDataContainer().isDirty()) {
                    isDirty = true;
                }
                instance.tick();
            }

            if (isDirty) {
                world.save();
            }
        });

        for (Player player : Bukkit.getOnlinePlayers()) {
            Block block = player.getTargetBlockExact(5);
            if (block == null) {
                player.resetTitle();
                continue;
            }

            StructureInstance structure = worlds.get(player.getWorld().getUID()).getStructure(new BlockLocation(block.getLocation()));
            if (structure instanceof EnergyContainer generator) {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GOLD + structure.structure.getClass().getSimpleName() + ChatColor.GRAY + " (" + generator.getStoredEnergy() + " / " + generator.getEnergyCapacity() + " RF)"));
            }
        }
    }

}
