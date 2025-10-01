package com.github.alantr7.torus.structure;

import com.github.alantr7.torus.world.BlockLocation;
import com.github.alantr7.torus.math.Direction;
import com.github.alantr7.torus.math.IntArrayBuilder;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

public abstract class Structure {

    public final String id;

    public int numericId;

    protected final Class<? extends StructureInstance> instanceClass;

    @Getter
    protected int[] bounds = { 0, 0, 0 };

    public Structure(String id, Class<? extends StructureInstance> instanceClass) {
        this.id = id;
        this.instanceClass = instanceClass;

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

    public StructureInstance place(BlockLocation location, Direction direction) {
        StructureInstance instance = instantiate(location, direction);
        instance.setup();

        location.world.placeStructure(instance);
        return instance;
    }

    protected abstract StructureInstance instantiate(@NotNull BlockLocation location, Direction direction);

}
