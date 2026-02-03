package com.github.alantr7.torus.structure.builder;

import com.github.alantr7.torus.structure.socket.Socket;

public record StructureSocketDef(Socket.Medium medium, Socket.FlowDirection direction, int allowedConnections) {
}
