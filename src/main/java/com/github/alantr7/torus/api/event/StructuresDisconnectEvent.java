package com.github.alantr7.torus.api.event;

import com.github.alantr7.torus.structure.socket.Socket;
import com.github.alantr7.torus.world.Direction;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class StructuresDisconnectEvent extends Event {

    private static final HandlerList handlerList = new HandlerList();

    private final Socket socket1;

    private final Socket socket2;

    private final Direction direction1;

    private final Direction direction2;

    public StructuresDisconnectEvent(Socket socket1, Socket socket2, Direction direction1, Direction direction2) {
        this.socket1 = socket1;
        this.socket2 = socket2;
        this.direction1 = direction1;
        this.direction2 = direction2;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }

}
