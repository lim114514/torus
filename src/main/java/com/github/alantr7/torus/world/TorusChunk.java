package com.github.alantr7.torus.world;

import com.github.alantr7.torus.structure.StructureInstance;
import org.joml.Vector2i;

import java.util.HashMap;
import java.util.Map;

public class TorusChunk {

    public final TorusWorld world;

    public final Vector2i position;

    public boolean isDirty;

    final Map<BlockLocation, StructureInstance> structures = new HashMap<>();

    final Map<BlockLocation, BlockLocation> occupations = new HashMap<>();

    public TorusChunk(TorusWorld world, Vector2i position) {
        this.world = world;
        this.position = position;
    }

    protected void _placeStructureWithOccupations(StructureInstance structure) {
        structures.put(structure.location, structure);

        byte[] bounds = structure.getBounds();
        for (int i = 0; i < bounds.length; i += 3) {
            BlockLocation relative = structure.location.getRelative(bounds[i], bounds[i + 1], bounds[i + 2]);
            if (contains(relative)) {
                occupations.put(relative, structure.location);
            }
        }
    }

    public boolean contains(BlockLocation location) {
        return location.x >> 4 == position.x && location.z >> 4 == position.y;
    }

}
