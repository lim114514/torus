package com.github.alantr7.torus.structure.state;

public class StateType<T> {

    private final Class<T> typeClass;

    private final String name;

    private StateType(Class<T> typeClass, String name) {
        this.typeClass = typeClass;
        this.name = name;
    }

    public static final StateType<Integer> INT = new StateType<>(Integer.class, "int");

    public static final StateType<Boolean> BOOLEAN = new StateType<>(Boolean.class, "boolean");

    @Override
    public String toString() {
        return name;
    }

}
