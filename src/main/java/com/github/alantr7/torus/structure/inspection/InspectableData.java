package com.github.alantr7.torus.structure.inspection;

import com.github.alantr7.torus.math.MathUtils;
import com.github.alantr7.torus.structure.EnergyContainer;
import com.github.alantr7.torus.world.BlockLocation;
import org.bukkit.ChatColor;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

public class InspectableData {

    public final InspectableProperty[] properties;

    public float[] hologramOffset = {0f, 0f, 0f};

    public Set<BlockLocation> inspectableBlocks = new HashSet<>();

    private byte nextPosition = 0;

    public InspectableData(byte properties) {
        this.properties = new InspectableProperty[properties];
    }

    public InspectableData property(String name, Supplier<String> valueSupplier) {
        properties[nextPosition++] = new InspectableProperty(name, valueSupplier);
        return this;
    }

    public InspectableData offset(float x, float y, float z) {
        hologramOffset = new float[] { x, y, z };
        return this;
    }

    public static Function<EnergyContainer, Supplier<String>> TEMPLATE_RF =
      container -> () -> MathUtils.formatNumber(container.getStoredEnergy().get()) + "/" + MathUtils.formatNumber(container.getEnergyCapacity()) + (container.getFlowMeter().isNeutral() ? "" : (((container.getFlowMeter().getSupplied() > container.getFlowMeter().getConsumed()) ? (ChatColor.GREEN + " ↑ " + (container.getFlowMeter().getSupplied() - container.getFlowMeter().getConsumed())) : (ChatColor.RED + " ↓ " + (container.getFlowMeter().getConsumed() - container.getFlowMeter().getSupplied())))));

}
