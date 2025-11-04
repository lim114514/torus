package com.github.alantr7.torus.model;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class Model {

    public final Map<String, PartModel> parts = new HashMap<>();

    @Nullable
    public PartModel getPart(String name) {
        return parts.get(name);
    }

}
