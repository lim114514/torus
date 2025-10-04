package com.github.alantr7.torus.structure;

import com.github.alantr7.torus.world.BlockLocation;
import com.github.alantr7.torus.math.Direction;
import com.github.alantr7.torus.math.ByteArrayBuilder;
import org.jetbrains.annotations.NotNull;

public abstract class Structure {

    public final String id;

    public int numericId;

    protected final Class<? extends StructureInstance> instanceClass;

    protected byte[] bounds = { 0, 0, 0 };

    public Structure(String id, Class<? extends StructureInstance> instanceClass) {
        this.id = id;
        this.instanceClass = instanceClass;

        ByteArrayBuilder builder = new ByteArrayBuilder();
        createBounds(builder);

        bounds = builder.build();
        if (bounds.length == 0) {
            bounds = new byte[] { 0, 0, 0 };
        } else if (bounds.length % 3 != 0) {
            throw new RuntimeException("Invalid structure bounds!");
        }
    }

    protected void createBounds(ByteArrayBuilder builder) {
    }

    public StructureInstance place(BlockLocation location, Direction direction) {
        StructureInstance instance = instantiate(location, direction);
        instance.setup();

        location.world.placeStructure(instance);
        return instance;
    }

    @Override
    public String toString() {
        return id;
    }

    protected abstract StructureInstance instantiate(@NotNull BlockLocation location, Direction direction);

}
