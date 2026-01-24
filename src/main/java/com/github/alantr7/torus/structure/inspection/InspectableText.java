package com.github.alantr7.torus.structure.inspection;

import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class InspectableText {

    public final Supplier<String> valueSupplier;

    public InspectableText(Supplier<String> valueSupplier) {
        this.valueSupplier = valueSupplier;
    }

    @Nullable
    public String getText() {
        return valueSupplier.get();
    }

}
