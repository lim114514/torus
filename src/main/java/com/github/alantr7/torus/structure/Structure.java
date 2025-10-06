package com.github.alantr7.torus.structure;

import com.github.alantr7.torus.math.MathUtils;
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

    public boolean isPlaceableAt(BlockLocation location, Direction direction) {
        byte[] bounds = MathUtils.rotateBounds(this.bounds, direction);
        for (int i = 0; i < bounds.length; i += 3) {
            if (location.getRelative(bounds[i], bounds[i + 1], bounds[i + 2]).getBlock().getType().isSolid()) {
                return false;
            }
        }
        return true;
    }

    public StructureInstance place(BlockLocation location, Direction direction) {
        StructureInstance instance = instantiate(location, direction);
        try {
            instance.setup();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println("Corrupted structure. It will be loaded but not ticked.");
            instance.isCorrupted = true;
        }

        location.world.placeStructure(instance);
        return instance;
    }

    @Override
    public String toString() {
        return id;
    }

    protected abstract StructureInstance instantiate(@NotNull BlockLocation location, Direction direction);

}
