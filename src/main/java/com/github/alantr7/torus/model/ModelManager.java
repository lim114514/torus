package com.github.alantr7.torus.model;

import com.github.alantr7.bukkitplugin.annotations.core.InvokePeriodically;
import com.github.alantr7.bukkitplugin.annotations.core.Singleton;
import com.github.alantr7.torus.structure.StructureInstance;

import java.util.HashMap;
import java.util.Map;

@Singleton
public class ModelManager {

    private final Map<String, ModelTemplate> models = new HashMap<>();

    public ModelTemplate getModel(String name) {
        return models.get(name);
    }

    public void cache(String name, ModelTemplate template) {
        models.put(name, template);
    }

}
