package com.github.alantr7.torus.world;

import com.github.alantr7.bukkitplugin.annotations.core.Invoke;
import com.github.alantr7.bukkitplugin.annotations.core.InvokePeriodically;
import com.github.alantr7.bukkitplugin.annotations.core.Singleton;
import com.github.alantr7.torus.TorusPlugin;
import com.github.alantr7.torus.structure.Inspectable;
import com.github.alantr7.torus.structure.StructureInstance;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
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

    public TorusWorldManager() {
        for (World world : Bukkit.getWorlds()) {
            TorusWorld torusWorld = new TorusWorld(world);
            torusWorld.load();

            for (Chunk chunk : world.getLoadedChunks()) {
                torusWorld.getChunkOrLoad(new BlockLocation(torusWorld, chunk.getX() << 4, 0, chunk.getZ() << 4));
            }

            worlds.put(world.getUID(), torusWorld);
        }
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

    @InvokePeriodically(interval = 20)
    private void tickLoadedStructures() {
        worlds.values().forEach(TorusWorld::tick);

        for (Player player : Bukkit.getOnlinePlayers()) {
            Block block = player.getTargetBlockExact(5);
            if (block == null) {
                player.resetTitle();
                continue;
            }

            BlockLocation blockLocation = new BlockLocation(block.getLocation());
            StructureInstance structure = worlds.get(player.getWorld().getUID()).getStructure(blockLocation);
            if (structure instanceof Inspectable generator) {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(generator.getInspectionText(blockLocation, player)));
            }
        }
    }

    @Invoke(Invoke.Schedule.AFTER_PLUGIN_ENABLE)
    private void registerEvents() {
        Bukkit.getPluginManager().registerEvents(this, TorusPlugin.getInstance());
    }

}
