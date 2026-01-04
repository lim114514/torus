package com.github.alantr7.torus.model;

import com.github.alantr7.bukkitplugin.annotations.core.InvokePeriodically;
import com.github.alantr7.bukkitplugin.annotations.core.Singleton;
import com.github.alantr7.torus.structure.StructureInstance;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

@Singleton
public class ModelManager {

    private final Map<String, ModelTemplate> models = new HashMap<>();

    private final Queue<StructureInstance> updateQueue = new LinkedList<>();

    public ModelTemplate getCached(String name) {
        return models.get(name);
    }

    public void cache(String name, ModelTemplate template) {
        models.put(name, template);
    }

    public void scheduleModelUpdate(StructureInstance instance) {
        if (instance.isModelUpdateScheduled())
            return;

        updateQueue.add(instance);
    }

    @InvokePeriodically(interval = 1)
    void performModelUpdates() {
        while (!updateQueue.isEmpty()) {
            updateQueue.remove().updateModel();
        }
    }

}
