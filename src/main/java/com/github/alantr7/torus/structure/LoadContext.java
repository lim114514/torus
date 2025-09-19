package com.github.alantr7.torus.structure;

import com.github.alantr7.torus.math.BlockLocation;
import com.github.alantr7.torus.math.Direction;
import com.github.alantr7.torus.structure.data.DataContainer;

public record LoadContext(Structure structure, BlockLocation location, Direction direction, DataContainer data) {
}
