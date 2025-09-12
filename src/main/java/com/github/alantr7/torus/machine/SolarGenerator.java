package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.math.BlockLocation;
import com.github.alantr7.torus.math.Direction;
import com.github.alantr7.torus.math.IntArrayBuilder;
import com.github.alantr7.torus.structure.Structure;
import com.github.alantr7.torus.structure.StructureInstance;
import org.jetbrains.annotations.NotNull;

public class SolarGenerator extends Structure {

    @Override
    public void createBounds(IntArrayBuilder builder) {
        builder.add(0, 0, 0);
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                builder.add(x, 1, z);
            }
        }
    }

    @Override
    public StructureInstance instantiate(@NotNull BlockLocation location, Direction direction) {
        return new SolarGeneratorInstance(location, direction);
    }

}
