package com.github.alantr7.torus.structure.builder;

import com.github.alantr7.torus.structure.component.Connector;

public record StructureConnectorDef(Connector.Matter matter, Connector.FlowDirection direction, int allowedConnections) {
}
