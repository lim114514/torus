package com.github.alantr7.torus.api.event;

import com.github.alantr7.torus.player.TorusPlayer;
import com.github.alantr7.torus.structure.Structure;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.world.BlockLocation;
import com.github.alantr7.torus.world.Direction;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class PlayerStructurePlaceEvent extends Event {

    @Getter
    private static final HandlerList handlerList = new HandlerList();

    private final TorusPlayer player;

    private final StructureInstance structure;

    public PlayerStructurePlaceEvent(TorusPlayer player, StructureInstance structure) {
        this.player = player;
        this.structure = structure;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }

}
