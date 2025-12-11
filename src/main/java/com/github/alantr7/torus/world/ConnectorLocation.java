package com.github.alantr7.torus.world;

import com.github.alantr7.torus.structure.component.Socket;

public record ConnectorLocation(BlockLocation location, Socket.Matter matter) {
}
