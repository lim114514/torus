package com.github.alantr7.torus.structure.builder;

import com.github.alantr7.torus.structure.component.Socket;

public record StructureSocketDef(Socket.Matter matter, Socket.FlowDirection direction, int allowedConnections) {
}
