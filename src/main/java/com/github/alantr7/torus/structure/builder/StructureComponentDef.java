package com.github.alantr7.torus.structure.builder;

import com.github.alantr7.torus.model.PartModel;
import com.github.alantr7.torus.model.PartModelTemplate;
import org.joml.Vector3f;

public class StructureComponentDef {

    public final String name;
    public final Vector3f offset;
    public final PartModel model;
    public final PartModelTemplate template;
    public final StructureConnectorDef connectorDef;

    public StructureComponentDef(String name, Vector3f offset, PartModel model) {
        this.name = name;
        this.offset = offset;
        this.model = model;
        this.template = null;
        this.connectorDef = null;
    }

    public StructureComponentDef(String name, Vector3f offset, PartModelTemplate template) {
        this.name = name;
        this.offset = offset;
        this.model = null;
        this.template = template;
        this.connectorDef = null;
    }

    public StructureComponentDef(String name, Vector3f offset, PartModel model, StructureConnectorDef connectorDef) {
        this.name = name;
        this.offset = offset;
        this.model = model;
        this.template = null;
        this.connectorDef = connectorDef;
    }

    public StructureComponentDef(String name, Vector3f offset, PartModelTemplate template, StructureConnectorDef connectorDef) {
        this.name = name;
        this.offset = offset;
        this.model = null;
        this.template = template;
        this.connectorDef = connectorDef;
    }

}
