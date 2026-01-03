package com.github.alantr7.torus.model.controller;

import com.github.alantr7.torus.model.ModelType;
import com.github.alantr7.torus.structure.state.StructureState;

import java.util.Collection;

public class ModelController {

    public final ModelType type;

    public final Collection<ModelCase> cases;

    public ModelController(ModelType type, Collection<ModelCase> cases) {
        this.type = type;
        this.cases = cases;
    }

    public ModelCase getModel(StructureState state) {
        for (ModelCase case1 : this.cases) {
            if (case1.test(state))
                return case1;
        }
        return null;
    }

}
