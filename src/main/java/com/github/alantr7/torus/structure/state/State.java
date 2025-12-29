package com.github.alantr7.torus.structure.state;

public class State<T> {

    public final String key;

    public final StateType<T> type;

    public final T defaultValue;

    public State(String key, StateType<T> type, T defaultValue) {
        this.key = key;
        this.type = type;
        this.defaultValue = defaultValue;
    }

}
