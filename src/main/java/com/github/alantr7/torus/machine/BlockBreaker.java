package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.math.BlockLocation;
import com.github.alantr7.torus.math.Direction;
import com.github.alantr7.torus.structure.Structure;
import com.github.alantr7.torus.structure.StructureInstance;
import org.jetbrains.annotations.NotNull;

public class BlockBreaker extends Structure {

    @Override
    public StructureInstance instantiate(@NotNull BlockLocation location, Direction direction) {
        return new BlockBreakerInstance(location, direction);
    }

}
