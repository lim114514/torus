package com.github.alantr7.torus.structure.property;

public class Property<T> {

    public final String name;

    public PropertyType<T> type;

    public T value;

    public Property(String name, PropertyType<T> type, T defaultValue) {
        this.name = name;
        this.type = type;
        this.value = defaultValue;
    }

}
