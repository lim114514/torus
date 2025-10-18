package com.github.alantr7.torus.structure.builder;

import com.github.alantr7.torus.structure.display.Model;
import com.github.alantr7.torus.structure.display.ModelTemplate;
import org.joml.Vector3f;

public class StructureComponentDef {

    public final String name;
    public final Vector3f offset;
    public final Model model;
    public final ModelTemplate template;

    public StructureComponentDef(String name, Vector3f offset, Model model) {
        this.name = name;
        this.offset = offset;
        this.model = model;
        this.template = null;
    }

    public StructureComponentDef(String name, Vector3f offset, ModelTemplate template) {
        this.name = name;
        this.offset = offset;
        this.model = null;
        this.template = template;
    }

}
