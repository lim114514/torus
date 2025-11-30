package com.github.alantr7.torus.model;

import com.github.alantr7.torus.world.BlockLocation;
import com.github.alantr7.torus.world.Direction;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;

public class ModelTemplate {

    public final int version;

    public final Map<PartModelTemplate, Vector3f> parts = new HashMap<>();

    public final Map<String, PartModelTemplate> partsByName = new HashMap<>();

    public ModelTemplate(int version) {
        this.version = version;
    }

    public void add(PartModelTemplate template) {
        add(template, new Vector3f(.5f, 0, .5f));
    }

    public void add(PartModelTemplate template, Vector3f offset) {
        parts.put(template, offset);
        partsByName.put(template.name, template);
    }

    public Model toModel(BlockLocation location, Direction direction) {
        Model model = new Model();
        parts.forEach((part, offset) -> {
            model.parts.put(part.name, part.build(location.toBukkit().add(offset.x, offset.y, offset.z), direction));
        });

        return model;
    }

}
