package com.github.alantr7.torus.structure;

import com.github.alantr7.torus.math.MathUtils;
import com.github.alantr7.torus.model.ModelTemplate;
import com.github.alantr7.torus.model.PartModelTemplate;
import com.github.alantr7.torus.world.BlockLocation;
import com.github.alantr7.torus.world.Direction;
import com.github.alantr7.torus.math.ByteArrayBuilder;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public abstract class Structure {

    public final String id;

    public int numericId = -1;

    protected final Class<? extends StructureInstance> instanceClass;

    protected byte[] bounds = { 0, 0, 0 };

    @Getter
    protected byte[] size;

    protected byte[] offset;

    public boolean isHeavy = true;

    protected final Map<String, PartModelTemplate> namedModelTemplates = new HashMap<>();

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

        byte[] min = { 127, 127, 127 };
        byte[] max = { -128, -128, -128 };

        for (int i = 0; i < bounds.length; i+=3) {
            min[0] = (byte) Math.min(bounds[i], min[0]);
            min[1] = (byte) Math.min(bounds[i+1], min[1]);
            min[2] = (byte) Math.min(bounds[i+2], min[2]);

            max[0] = (byte) Math.max(bounds[i], max[0]);
            max[1] = (byte) Math.max(bounds[i+1], max[1]);
            max[2] = (byte) Math.max(bounds[i+2], max[2]);
        }

        size = new byte[] { (byte) (max[0] - min[0] + 1), (byte) (max[1] - min[1] + 1), (byte) (max[2] - min[2] + 1) };
    }

    protected void createBounds(ByteArrayBuilder builder) {
    }

    public ModelTemplate getInitialModel() {
        return null;
    }

    public boolean isPlaceableAt(BlockLocation location, Direction direction) {
        byte[] offset = calculateOffset(direction.getOpposite());
        byte[] bounds = MathUtils.rotateVectors(this.bounds, direction);

        location = location.getRelative(offset[0], offset[1], offset[2]);
        for (int i = 0; i < bounds.length; i += 3) {
            if (location.getRelative(bounds[i], bounds[i + 1], bounds[i + 2]).getBlock().getType().isSolid()) {
                return false;
            }
        }
        return true;
    }

    public StructureInstance place(BlockLocation location, Direction direction) {
        byte[] offset = calculateOffset(direction.getOpposite());
        location = location.getRelative(offset[0], offset[1], offset[2]);
        StructureInstance instance = instantiate(location, direction);
        try {
            ModelTemplate modelTemplate = getInitialModel();
            if (modelTemplate != null) {
                instance.model = modelTemplate.toModel(location, direction);
            }
            instance.setup();
            instance.handleModelInit();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println("Corrupted structure. It will be loaded but not ticked.");
            instance.isCorrupted = true;
        }

        location.world.placeStructure(instance);
        return instance;
    }

    private byte[] calculateOffset(Direction direction) {
        if (offset == null)
            return new byte[] { 0, 0, 0 };

        return MathUtils.rotateVectors(this.offset, direction);
    }

    public PartModelTemplate getNamedModelTemplate(String name) {
        if (name == null)
            return null;

        return namedModelTemplates.get(name);
    }

    protected void registerNamedModelTemplate(PartModelTemplate template) {
        namedModelTemplates.put(template.name, template);
    }

    @Override
    public String toString() {
        return id;
    }

    protected abstract StructureInstance instantiate(@NotNull BlockLocation location, Direction direction);

}
