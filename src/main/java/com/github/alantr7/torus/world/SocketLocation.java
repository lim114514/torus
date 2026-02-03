package com.github.alantr7.torus.world;

import com.github.alantr7.torus.structure.socket.Socket;

public record SocketLocation(BlockLocation location, Socket.Medium medium) {
}
