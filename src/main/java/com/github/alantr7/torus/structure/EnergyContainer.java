package com.github.alantr7.torus.structure;

import com.github.alantr7.torus.structure.data.Data;
import com.github.alantr7.torus.world.BlockLocation;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

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
    default String getInspectionText(BlockLocation location, Player player) {
        return ChatColor.GOLD + getClass().getSimpleName() + ChatColor.RESET + " [" + getStoredEnergy().get() + " / " + getEnergyCapacity() + " RF]";
    }

}
