package com.github.alantr7.torus.structure;

import com.github.alantr7.torus.structure.data.Data;
import com.github.alantr7.torus.structure.inspection.InspectableData;

public interface EnergyContainer extends Inspectable {

    int getEnergyCapacity();

    Data<Integer> getStoredEnergy();

    default boolean hasSufficientEnergy(int amount) {
        return getStoredEnergy().get() >= amount;
    }

    default int consumeEnergy(int amount) {
        int starting = getStoredEnergy().get();
        getStoredEnergy().update(Math.max(starting - amount, 0));

        return starting - getStoredEnergy().get();
    }

    default int supplyEnergy(int amount) {
        int starting = getStoredEnergy().get();
        getStoredEnergy().update(Math.min(starting + amount, getEnergyCapacity()));

        return getStoredEnergy().get() - starting;
    }

    @Override
    default InspectableData setupInspectableData() {
        return new InspectableData((byte) 1).property("RF", InspectableData.TEMPLATE_RF.apply(this));
    }

}
