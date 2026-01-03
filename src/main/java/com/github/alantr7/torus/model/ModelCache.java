package com.github.alantr7.torus.model;

import com.github.alantr7.bukkitplugin.annotations.core.Singleton;

import java.util.HashMap;
import java.util.Map;

@Singleton
public class ModelCache {

    private final Map<String, ModelTemplate> models = new HashMap<>();

    public ModelTemplate getModel(String name) {
        return models.get(name);
    }

    public void save(String name, ModelTemplate template) {
        models.put(name, template);
    }

}
