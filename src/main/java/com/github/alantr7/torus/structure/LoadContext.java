package com.github.alantr7.torus.structure;

import com.github.alantr7.torus.world.BlockLocation;
import com.github.alantr7.torus.world.Direction;
import com.github.alantr7.torus.structure.data.DataContainer;

public record LoadContext(Structure structure, BlockLocation location, Direction direction, DataContainer data) {
}
