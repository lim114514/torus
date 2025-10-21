package com.github.alantr7.torus.structure.builder;

import com.github.alantr7.torus.structure.display.Model;
import com.github.alantr7.torus.structure.display.ModelTemplate;
import org.joml.Vector3f;

public class StructureComponentDef {

    public final String name;
    public final Vector3f offset;
    public final Model model;
    public final ModelTemplate template;
    public final StructureConnectorDef connectorDef;

    public StructureComponentDef(String name, Vector3f offset, Model model) {
        this.name = name;
        this.offset = offset;
        this.model = model;
        this.template = null;
        this.connectorDef = null;
    }

    public StructureComponentDef(String name, Vector3f offset, ModelTemplate template) {
        this.name = name;
        this.offset = offset;
        this.model = null;
        this.template = template;
        this.connectorDef = null;
    }

    public StructureComponentDef(String name, Vector3f offset, Model model, StructureConnectorDef connectorDef) {
        this.name = name;
        this.offset = offset;
        this.model = model;
        this.template = null;
        this.connectorDef = connectorDef;
    }

    public StructureComponentDef(String name, Vector3f offset, ModelTemplate template, StructureConnectorDef connectorDef) {
        this.name = name;
        this.offset = offset;
        this.model = null;
        this.template = template;
        this.connectorDef = connectorDef;
    }

}
