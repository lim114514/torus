package com.github.alantr7.torus.model;

import com.github.alantr7.torus.world.BlockLocation;
import com.github.alantr7.torus.world.Direction;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;

public class ModelTemplate {

    public final int version;

    public final Map<String, PartModelTemplate> parts = new HashMap<>();

    public static final ModelTemplate EMPTY = new ModelTemplate(1);

    public ModelTemplate(int version) {
        this.version = version;
    }

    public void add(PartModelTemplate template) {
        parts.put(template.name, template);
    }

    public Model toModel(BlockLocation location, Direction direction) {
        Model model = new Model(this);
        parts.forEach((name, part) -> {
            model.parts.put(name, part.build(location.toBukkit(), direction));
        });

        return model;
    }

}
