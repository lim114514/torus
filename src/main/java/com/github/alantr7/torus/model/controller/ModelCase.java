package com.github.alantr7.torus.model.controller;

import com.github.alantr7.torus.model.ModelTemplate;
import com.github.alantr7.torus.structure.state.State;
import com.github.alantr7.torus.structure.state.StructureState;

import java.util.Map;

public class ModelCase {

    public final Map<State<Object>, Object> conditions;

    public final ModelTemplate template;

    // TODO: Support multiple animations
    public final String animations;

    public ModelCase(Map<State<Object>, Object> conditions, ModelTemplate template, String animations) {
        this.conditions = conditions;
        this.template = template;
        this.animations = animations;
    }

    public boolean isFallback() {
        return conditions.isEmpty();
    }

    public boolean test(StructureState state) {
        if (isFallback()) {
            return true;
        }

        for (State<Object> item : conditions.keySet()) {
            Object value = state.get(item);
            if (!value.equals(conditions.get(item))) {
                return false;
            }
        }
        return true;
    }

}
