package com.github.alantr7.torus.structure;

import com.github.alantr7.torus.math.BlockLocation;
import com.github.alantr7.torus.math.Direction;
import com.github.alantr7.torus.math.IntArrayBuilder;
import com.github.alantr7.torus.structure.component.StructureComponent;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public abstract class Structure {

    protected Map<String, StructureComponent> components = new HashMap<>();

    @Getter
    protected int[] bounds = { 0, 0, 0 };

    public Structure() {
        IntArrayBuilder builder = new IntArrayBuilder();
        createBounds(builder);

        bounds = builder.build();
        if (bounds.length == 0) {
            bounds = new int[] { 0, 0, 0 };
        } else if (bounds.length % 3 != 0) {
            throw new RuntimeException("Invalid structure bounds!");
        }
    }

    protected void createBounds(IntArrayBuilder builder) {
    }

    public abstract StructureInstance instantiate(@NotNull BlockLocation location, Direction direction);

}
