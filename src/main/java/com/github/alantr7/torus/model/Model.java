package com.github.alantr7.torus.model;

import java.util.HashMap;
import java.util.Map;

public class Model {

    public final ModelTemplate template;

    public final Map<String, PartModel> parts = new HashMap<>();

    public Model(ModelTemplate template) {
        this.template = template;
    }

    public PartModel getPartById(String id) {
        return parts.get(id);
    }

    public PartModel getPartByName(String name) {
        for (PartModel part : parts.values()) {
            if (name.equals(part.name))
                return part;
        }
        return null;
    }

    public void remove() {
        parts.values().forEach(PartModel::remove);
    }

}
