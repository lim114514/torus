package com.github.alantr7.torus.api.event;

import com.github.alantr7.torus.player.TorusPlayer;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.world.BlockLocation;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class PlayerStructureInteractEvent extends Event implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final TorusPlayer player;

    private final StructureInstance structure;

    private final BlockLocation clickedBlock;

    @Setter
    private boolean isCancelled = false;

    public PlayerStructureInteractEvent(TorusPlayer player, StructureInstance structure, BlockLocation clickedBlock) {
        this.player = player;
        this.structure = structure;
        this.clickedBlock = clickedBlock;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

}
