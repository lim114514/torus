package com.github.alantr7.torus.structure;

import com.github.alantr7.torus.world.Fluid;
import org.jetbrains.annotations.Nullable;

public interface FluidContainer {

    @Nullable
    Fluid getFluid();

    int getFluidCapacity();

    int getStoredFluid();

    void setStoredFluid(int fluid);

    default boolean hasSufficientFluid(int amount) {
        return getStoredFluid() >= amount;
    }

    default int consumeFluid(int amount) {
        int starting = getStoredFluid();
        setStoredFluid(Math.max(starting - amount, 0));

        return starting - getStoredFluid();
    }

    default int supplyFluid(int amount) {
        int starting = getStoredFluid();
        setStoredFluid(Math.min(starting + amount, getFluidCapacity()));

        return getStoredFluid() - starting;
    }

}
