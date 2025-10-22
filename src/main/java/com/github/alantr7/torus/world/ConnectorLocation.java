package com.github.alantr7.torus.world;

import com.github.alantr7.torus.structure.component.Connector;

public record ConnectorLocation(BlockLocation location, Connector.Matter matter) {
}
