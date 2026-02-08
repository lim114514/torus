package com.github.alantr7.torus.world;

import com.github.alantr7.bukkitplugin.annotations.core.Invoke;
import com.github.alantr7.bukkitplugin.annotations.core.InvokePeriodically;
import com.github.alantr7.bukkitplugin.annotations.core.Singleton;
import com.github.alantr7.torus.TorusPlugin;
import com.github.alantr7.torus.config.MainConfig;
import com.github.alantr7.torus.player.TorusPlayer;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.utils.Timing;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Singleton
public class TorusWorldManager implements Listener {

    private final Map<UUID, TorusWorld> worlds = new HashMap<>();

    @InvokePeriodically(interval = 0, limit = 1, delay = 2L)
    void initialize() {
        for (World world : Bukkit.getWorlds()) {
            if (MainConfig.WORLD_BLACKLIST.contains(world.getName()))
                continue;

            TorusWorld torusWorld = new TorusWorld(world);
            torusWorld.load();

            for (Chunk chunk : world.getLoadedChunks()) {
                torusWorld.getChunkOrLoad(new BlockLocation(torusWorld, chunk.getX() << 4, 0, chunk.getZ() << 4));
            }

            worlds.put(world.getUID(), torusWorld);
        }
    }

    public boolean isWorldSupported(World world) {
        return worlds.containsKey(world.getUID());
    }

    public TorusWorld getWorld(World world) {
        return worlds.get(world.getUID());
    }

    public Collection<TorusWorld> getWorlds() {
        return worlds.values();
    }

    @EventHandler
    void handleChunkLoadEvent(ChunkLoadEvent event) {
        TorusWorld world = worlds.get(event.getWorld().getUID());
        if (world == null)
            return;

        world.handleChunkLoad(event.getChunk());
    }

    @EventHandler
    void handleChunkUnloadEvent(ChunkUnloadEvent event) {
        TorusWorld world = worlds.get(event.getWorld().getUID());
        if (world == null)
            return;

        world.handleChunkUnload(event.getChunk());
    }

    @Getter
    private final Timing tickDurationTimings = new Timing(5);

    @InvokePeriodically(interval = 20L, delay = 10L)
    private void tickLoadedStructures() {
        long start = System.currentTimeMillis();
        worlds.values().forEach(TorusWorld::tick);
        tickDurationTimings.add((int) (System.currentTimeMillis() - start));
    }

    @InvokePeriodically(interval = 8)
    private void showInspectionHolograms() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!worlds.containsKey(player.getWorld().getUID()) || MainConfig.WORLD_BLACKLIST.contains(player.getWorld().getName()))
                continue;

            TorusPlayer torusPlayer = TorusPlayer.get(player);
            Block block = player.getTargetBlockExact(5);
            if (block == null) {
                torusPlayer.hideInspectionHologram();
                continue;
            }

            BlockLocation blockLocation = new BlockLocation(block.getLocation());
            StructureInstance structure = blockLocation.getStructure();
            if (structure != null && structure.inspectionHologram != null && structure.inspectableDataContainer.inspectableBlocks.contains(blockLocation)) {
                structure.updateInspectionHologram();
                torusPlayer.showInspectionHologram(structure);
            } else {
                torusPlayer.hideInspectionHologram();
            }
        }
    }

    @InvokePeriodically(interval = 20 * 60, delay = 20 * 60)
    @Invoke(Invoke.Schedule.AFTER_PLUGIN_DISABLE)
    private void autoSaveStructures() {
        worlds.values().forEach(TorusWorld::save);
    }

    @Invoke(Invoke.Schedule.AFTER_PLUGIN_DISABLE)
    private void removeModelsOnDisable() {
        worlds.values().forEach(world ->world.regions.values().forEach(region -> region.chunks.values().forEach(chunk -> {
            chunk.structures.values().forEach(StructureInstance::handleUnload);
        })));
    }

}
