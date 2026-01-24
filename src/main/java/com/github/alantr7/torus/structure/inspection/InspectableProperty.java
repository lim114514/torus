package com.github.alantr7.torus.structure.inspection;

import java.util.function.Supplier;

public class InspectableProperty extends InspectableText {

    public final String name;

    public InspectableProperty(String name, Supplier<String> valueSupplier) {
        super(valueSupplier);
        this.name = name;
    }

    @Override
    public String getText() {
        String value = valueSupplier.get();
        if (value != null) {
            return name + ": " + value;
        }
        return null;
    }

}
