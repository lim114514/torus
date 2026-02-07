package com.github.alantr7.torus.structure.inspection;

import lombok.Getter;
import lombok.Setter;

import java.util.function.Supplier;

public class InspectableProperty extends InspectableText {

    @Getter @Setter
    private String name;

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
