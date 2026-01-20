package com.github.alantr7.torus.model;

import com.github.alantr7.torus.world.BlockLocation;
import com.github.alantr7.torus.world.Direction;
import com.github.alantr7.torus.world.Pitch;

import java.util.HashMap;
import java.util.Map;

public class ModelTemplate {

    public final ModelType modelType;

    public final int version;

    public final Map<String, PartModelTemplate> parts = new HashMap<>();

    public final Map<String, ModelTemplate> children = new HashMap<>();

    public static final ModelTemplate EMPTY = new ModelTemplate(1);

    public ModelTemplate(int version) {
        this(ModelType.SINGLEPART, version);
    }

    public ModelTemplate(ModelType modelType, int version) {
        this.modelType = modelType;
        this.version = version;
    }

    public void add(PartModelTemplate template) {
        parts.put(template.name, template);
    }

    public Model toModel(BlockLocation location, Direction direction, Pitch pitch) {
        Model model = new Model(this);
        parts.forEach((name, part) -> {
            model.parts.put(name, part.build(location.toBukkit(), direction, pitch));
        });

        return model;
    }

    public Model upgradeModel(Model previous, BlockLocation location, Direction direction, Pitch pitch) {
        if (previous == null)
            return toModel(location, direction, pitch);

        if (previous.template == null || previous.template.children.isEmpty()) {
            previous.remove();
            return toModel(location, direction, pitch);
        }

        ModelTemplate previousTemplate = previous.template;

        // Rebuild multi-part models
        if (modelType == ModelType.MULTIPART) {
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
                        model.parts.put(stateSet + "." + partName, part.build(location.toBukkit(), direction, pitch));
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

        // Rebuild single-part models
        else {
            ModelTemplate newModelTemplate = children.values().iterator().next();
            ModelTemplate previousModelTemplate = previousTemplate.children.values().iterator().next();

            if (newModelTemplate == previousModelTemplate) {
                Model model = new Model(previousTemplate);
                model.parts.putAll(previous.parts);

                return model;
            } else {
                previous.remove();
                return toModel(location, direction, pitch);
            }
        }
    }

}
