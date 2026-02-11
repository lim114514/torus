package com.github.alantr7.torus.structure.property;

public class PropertyType<T> {

    public static final PropertyType<Integer> INT = new PropertyType<>("int");
    public static final PropertyType<Float> FLOAT = new PropertyType<>("float");
    public static final PropertyType<String> STRING = new PropertyType<>("string");

    public final String name;

    public PropertyType(String name) {
        this.name = name;
    }

}
