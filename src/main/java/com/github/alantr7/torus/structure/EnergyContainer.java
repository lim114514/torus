package com.github.alantr7.torus.structure;

public interface EnergyContainer {

    double getEnergyCapacity();

    void setEnergyCapacity(double capacity);

    double getStoredEnergy();

    void setStoredEnergy(double energy);

    default boolean hasSufficientEnergy(double amount) {
        return getStoredEnergy() >= amount;
    }

    default double consumeEnergy(double amount) {
        double starting = getStoredEnergy();
        setStoredEnergy(Math.max(starting - amount, 0));

        return starting - getStoredEnergy();
    }

    default double supplyEnergy(double amount) {
        double starting = getStoredEnergy();
        setStoredEnergy(Math.min(starting + amount, getEnergyCapacity()));

        return getStoredEnergy() - starting;
    }

}
