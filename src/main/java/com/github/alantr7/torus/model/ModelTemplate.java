package com.github.alantr7.torus.model;

import com.github.alantr7.torus.world.BlockLocation;
import com.github.alantr7.torus.world.Direction;

import java.util.HashMap;
import java.util.Map;

public class ModelTemplate {

    public final int version;

    public final Map<String, PartModelTemplate> parts = new HashMap<>();

    public final Map<String, ModelTemplate> children = new HashMap<>();

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

    public Model upgradeModel(Model previous, BlockLocation location, Direction direction) {
        if (previous == null)
            return toModel(location, direction);

        if (previous.template == null || previous.template.children.isEmpty()) {
            previous.remove();
            return toModel(location, direction);
        }

        ModelTemplate previousTemplate = previous.template;

        Model model = new Model(this);
        children.forEach((stateSet, child) -> {
            // Reuse the same parts
            if (previousTemplate.children.get(stateSet) == child) {
                previous.parts.forEach((partName, part) -> {
                    if (partName.startsWith(stateSet + ".")) {
                        model.parts.put(partName, part);
                    }
                });
            }

            // Generate missing parts
            else {
                child.parts.forEach((partName, part) -> {
                    model.parts.put(stateSet + "." + partName, part.build(location.toBukkit(), direction));
                });
            }
        });

        // Remove old parts that are not used in the new model
        previousTemplate.children.forEach((stateSet, child) -> {
            if (children.containsKey(stateSet))
                return;

            for (String partName : child.parts.keySet()) {
                PartModel part = previous.parts.get(stateSet + "." + partName);
                if (part != null) {
                    part.remove();
                }
            }
        });

        return model;
    }

}
