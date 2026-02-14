package com.github.alantr7.torus.structure.inspection;

import com.github.alantr7.torus.lang.Translatable;
import lombok.Getter;
import lombok.Setter;

import java.util.function.Supplier;

public class InspectableProperty extends InspectableText {

    private String name;

    private Translatable translatableName;

    InspectableProperty(String name, Translatable translatable, Supplier<String> valueSupplier) {
        super(valueSupplier);
        this.name = name;
        this.translatableName = translatable;
    }

    public String getName() {
        return translatableName != null ? translatableName.get() : name;
    }

    public void setName(String name) {
        this.name = name;
        this.translatableName = null;
    }

    public void setName(Translatable translatable) {
        this.translatableName = translatable;
        this.name = null;
    }

    @Override
    public String getText() {
        String value = valueSupplier.get();
        if (value != null) {
            return getName() + ": " + value;
        }
        return null;
    }

}
