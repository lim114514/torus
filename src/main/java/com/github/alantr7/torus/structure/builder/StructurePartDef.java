package com.github.alantr7.torus.structure.builder;

import org.joml.Vector3f;

public class StructurePartDef {

    public final String name;
    public final Vector3f offset;
    public final StructureSocketDef socketDef;

    public StructurePartDef(String name, Vector3f offset) {
        this(name, offset, null);
    }

    public StructurePartDef(String name, Vector3f offset, StructureSocketDef socketDef) {
        this.name = name;
        this.offset = offset;
        this.socketDef = socketDef;
    }

}
