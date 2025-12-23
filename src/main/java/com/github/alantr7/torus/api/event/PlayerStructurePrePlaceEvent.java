package com.github.alantr7.torus.api.event;

import com.github.alantr7.torus.player.TorusPlayer;
import com.github.alantr7.torus.structure.Structure;
import com.github.alantr7.torus.world.BlockLocation;
import com.github.alantr7.torus.world.Direction;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class PlayerStructurePrePlaceEvent extends Event implements Cancellable {

    @Getter
    private static final HandlerList handlerList = new HandlerList();

    private final TorusPlayer player;

    private final Structure structure;

    private final BlockLocation location;

    private final Direction direction;

    @Getter @Setter
    private boolean isCancelled;

    public PlayerStructurePrePlaceEvent(TorusPlayer player, Structure structure, BlockLocation location, Direction direction) {
        this.player = player;
        this.structure = structure;
        this.location = location;
        this.direction = direction;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }

}
