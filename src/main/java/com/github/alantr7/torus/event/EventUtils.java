package com.github.alantr7.torus.event;

import com.github.alantr7.torus.api.event.PlayerStructureBreakEvent;
import com.github.alantr7.torus.api.event.PlayerStructurePlaceEvent;
import com.github.alantr7.torus.api.event.PlayerStructurePrePlaceEvent;
import com.github.alantr7.torus.player.TorusPlayer;
import com.github.alantr7.torus.structure.Structure;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.world.BlockLocation;
import com.github.alantr7.torus.world.Direction;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class EventUtils {

    public static boolean callStructurePrePlaceEvent(Player player, Structure structure, BlockLocation location, Direction direction) {
        PlayerStructurePrePlaceEvent event = new PlayerStructurePrePlaceEvent(TorusPlayer.get(player), structure, location, direction);
        Bukkit.getPluginManager().callEvent(event);

        return !event.isCancelled();
    }

    public static void callStructurePlaceEvent(Player player, StructureInstance structure) {
        PlayerStructurePlaceEvent event = new PlayerStructurePlaceEvent(TorusPlayer.get(player), structure);
        Bukkit.getPluginManager().callEvent(event);
    }

    public static boolean callStructureBreakEvent(Player player, StructureInstance structure) {
        PlayerStructureBreakEvent event = new PlayerStructureBreakEvent(TorusPlayer.get(player), structure);
        Bukkit.getPluginManager().callEvent(event);

        return !event.isCancelled();
    }

}
