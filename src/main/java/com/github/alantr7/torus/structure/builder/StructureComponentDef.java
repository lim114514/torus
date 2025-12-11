package com.github.alantr7.torus.structure.builder;

import org.joml.Vector3f;

public class StructureComponentDef {

    public final String name;
    public final Vector3f offset;
    public final StructureSocketDef socketDef;

    public StructureComponentDef(String name, Vector3f offset) {
        this(name, offset, null);
    }

    public StructureComponentDef(String name, Vector3f offset, StructureSocketDef socketDef) {
        this.name = name;
        this.offset = offset;
        this.socketDef = socketDef;
    }

}
