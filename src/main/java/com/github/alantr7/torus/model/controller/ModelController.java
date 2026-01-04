package com.github.alantr7.torus.model.controller;

import com.github.alantr7.torus.model.ModelTemplate;
import com.github.alantr7.torus.model.ModelType;
import com.github.alantr7.torus.structure.state.StructureState;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ModelController {

    public final ModelType type;

    public final Collection<ModelCase> cases;

    public ModelController(ModelType type, Collection<ModelCase> cases) {
        this.type = type;
        this.cases = cases;
    }

    public ModelContainer getModel(StructureState state) {
        List<ModelCase> matches = new ArrayList<>();
        for (ModelCase case1 : this.cases) {
            if (case1.test(state)) {
                matches.add(case1);
                if (type == ModelType.SINGLEPART) {
                    return new ModelContainer(matches, generateCompositeModel(Collections.singletonList(case1)));
                }
            }
        }
        return matches.isEmpty() ? null : new ModelContainer(matches, generateCompositeModel(matches));
    }

    private ModelTemplate generateCompositeModel(List<ModelCase> matches) {
        ModelTemplate composite = new ModelTemplate(1);
        for (ModelCase modelCase : matches) {
            modelCase.template.parts.forEach((partName, part) -> {
                composite.parts.put(modelCase.stateSet + "." + partName, part);
            });
            composite.children.put(modelCase.stateSet, modelCase.template);
        }

        return composite;
    }

}
