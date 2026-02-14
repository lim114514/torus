package com.github.alantr7.torus.structure.inspection;

import com.github.alantr7.torus.lang.Translatable;
import com.github.alantr7.torus.utils.MathUtils;
import com.github.alantr7.torus.structure.EnergyContainer;
import com.github.alantr7.torus.world.BlockLocation;
import org.bukkit.ChatColor;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

public class InspectableDataContainer {

    public final InspectableText[] lines;

    public Set<BlockLocation> inspectableBlocks = new HashSet<>();

    private byte nextPosition = 0;

    public InspectableDataContainer(byte lines) {
        this.lines = new InspectableText[lines];
    }

    public InspectableDataContainer property(String name, Supplier<String> valueSupplier) {
        lines[nextPosition++] = new InspectableProperty(name, null, valueSupplier);
        return this;
    }

    public InspectableDataContainer property(Translatable translatable, Supplier<String> valueSupplier) {
        lines[nextPosition++] = new InspectableProperty(null, translatable, valueSupplier);
        return this;
    }

    public InspectableDataContainer line(Supplier<String> valueSupplier) {
        lines[nextPosition++] = new InspectableText(valueSupplier);
        return this;
    }

    public static Function<EnergyContainer, Supplier<String>> TEMPLATE_RF =
      container -> () -> MathUtils.formatNumber(container.getStoredEnergy().get()) + "/" + MathUtils.formatNumber(container.getEnergyCapacity()) + (container.getFlowMeter().isNeutral() ? "" : (((container.getFlowMeter().getSupplied() > container.getFlowMeter().getConsumed()) ? (ChatColor.GREEN + " ↑ " + (container.getFlowMeter().getSupplied() - container.getFlowMeter().getConsumed())) : (ChatColor.RED + " ↓ " + (container.getFlowMeter().getConsumed() - container.getFlowMeter().getSupplied())))));

}
