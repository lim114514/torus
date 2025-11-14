package com.github.alantr7.torus.structure.inspection;

import java.util.function.Supplier;

public class InspectableProperty {

    public final String name;

    public final Supplier<String> valueSupplier;

    public InspectableProperty(String name, Supplier<String> valueSupplier) {
        this.name = name;
        this.valueSupplier = valueSupplier;
    }

}
