package com.github.alantr7.torus.model;

import java.util.HashMap;
import java.util.Map;

public class Model {

    public final ModelTemplate template;

    public final Map<String, PartModel> parts = new HashMap<>();

    public Model(ModelTemplate template) {
        this.template = template;
    }

    public PartModel getPart(String name) {
        return parts.get(name);
    }

    public void remove() {
        parts.values().forEach(PartModel::remove);
    }

}
