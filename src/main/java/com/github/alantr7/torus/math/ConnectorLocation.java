package com.github.alantr7.torus.math;

import com.github.alantr7.torus.structure.component.Connector;
import com.github.alantr7.torus.world.BlockLocation;

public record ConnectorLocation(BlockLocation location, Connector.Matter matter) {
}
