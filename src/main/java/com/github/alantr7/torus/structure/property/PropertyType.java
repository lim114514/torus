package com.github.alantr7.torus.structure.property;

import org.joml.Vector3f;
import org.joml.Vector3i;

import java.util.List;

public class PropertyType<T> {

    public static final PropertyType<Integer> INT               = new PropertyType<>("int");
    public static final PropertyType<Boolean> BOOLEAN           = new PropertyType<>("boolean");
    public static final PropertyType<Float> FLOAT               = new PropertyType<>("float");
    public static final PropertyType<String> STRING             = new PropertyType<>("string");
    public static final PropertyType<List<String>> STRING_LIST  = new PropertyType<>("string_list");
    public static final PropertyType<Vector3i> VECTOR3I         = new PropertyType<>("vector3i");
    public static final PropertyType<Vector3f> VECTOR3F         = new PropertyType<>("vector3f");

    public final String name;

    public PropertyType(String name) {
        this.name = name;
    }

}
