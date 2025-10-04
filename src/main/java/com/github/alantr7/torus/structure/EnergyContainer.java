package com.github.alantr7.torus.structure;

import com.github.alantr7.torus.world.BlockLocation;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public interface EnergyContainer extends Inspectable {

    double getEnergyCapacity();

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

    @Override
    default String getInspectionText(BlockLocation location, Player player) {
        return ChatColor.GOLD + getClass().getSimpleName() + ChatColor.RESET + " [" + getStoredEnergy() + " / " + getEnergyCapacity() + " RF]";
    }

}
