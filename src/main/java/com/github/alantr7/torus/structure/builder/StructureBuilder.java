package com.github.alantr7.torus.structure.builder;

import com.github.alantr7.torus.api.addon.TorusAddon;
import com.github.alantr7.torus.structure.Structure;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.structure.property.Property;
import com.github.alantr7.torus.structure.property.PropertyType;
import com.github.alantr7.torus.world.Transform;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class StructureBuilder<T extends StructureInstance> {

    protected final TorusAddon addon;

    protected final String id;

    protected final Class<T> instanceClass;

    protected final Map<String, Property<?>> properties = new HashMap<>();

    protected Function<Transform, StructurePartDef[]> bodyDef;

    public StructureBuilder(TorusAddon addon, String id, Class<T> instanceClass) {
        this.addon = addon;
        this.id = id;
        this.instanceClass = instanceClass;
    }

    public StructureBuilder<T> name(String name) {
        properties.put("general_settings.name", new Property<>("general_settings.name", PropertyType.STRING, name));
        return this;
    }

    public StructureBuilder<T> properties(Property<?>... properties) {
        for (Property<?> prop : properties) {
            this.properties.put(prop.name, prop);
        }
        return this;
    }

    public StructureBuilder<T> body(Function<Transform, StructurePartDef[]> bodyDef) {
        this.bodyDef = bodyDef;
        return this;
    }

    public Structure build() {
        return null;
    }

}
