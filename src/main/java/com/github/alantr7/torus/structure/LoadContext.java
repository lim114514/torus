package com.github.alantr7.torus.structure;

import com.github.alantr7.torus.world.BlockLocation;
import com.github.alantr7.torus.world.Direction;
import com.github.alantr7.torus.structure.data.DataContainer;
import com.github.alantr7.torus.world.Pitch;

public record LoadContext(Structure structure, BlockLocation location, Direction direction, Pitch pitch, DataContainer data) {
}
