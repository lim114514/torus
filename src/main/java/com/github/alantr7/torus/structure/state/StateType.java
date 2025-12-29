package com.github.alantr7.torus.structure.state;

public class StateType<T> {

    private final Class<T> typeClass;

    private StateType(Class<T> typeClass) {
        this.typeClass = typeClass;
    }

    public static final StateType<Integer> INT = new StateType<>(Integer.class);

    public static final StateType<Boolean> BOOLEAN = new StateType<>(Boolean.class);

}
