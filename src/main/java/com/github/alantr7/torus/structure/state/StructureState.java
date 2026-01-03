package com.github.alantr7.torus.structure.state;

import com.github.alantr7.torus.structure.StructureInstance;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class StructureState {

    private final StructureInstance structure;

    private final Map<String, Object> data = new HashMap<>();

    public StructureState(StructureInstance structure) {
        this.structure = structure;
    }

    public Set<Map.Entry<String, Object>> getEntries() {
        return data.entrySet();
    }

    @SuppressWarnings("unchecked")
    public <T> T get(State<T> state) {
        return (T) data.getOrDefault(state.key, state.defaultValue);
    }

    public <T> void set(State<T> state, @NotNull T value) {
        set(state, value, true);
    }

    public <T> void set(State<T> state, @NotNull T value, boolean triggerModelUpdate) {
        if (value.equals(data.get(state.key)))
            return;

        data.put(state.key, value);
        if (triggerModelUpdate) {
            structure.scheduleModelUpdate();
        }
    }

}
