package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.exception.SetupException;
import com.github.alantr7.torus.math.MathUtils;
import com.github.alantr7.torus.structure.EnergyContainer;
import com.github.alantr7.torus.structure.LoadContext;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.structure.Structures;
import com.github.alantr7.torus.structure.builder.StructureBodyDef;
import com.github.alantr7.torus.structure.component.Socket;
import com.github.alantr7.torus.structure.data.Data;
import com.github.alantr7.torus.structure.inspection.InspectableData;
import com.github.alantr7.torus.world.BlockLocation;
import com.github.alantr7.torus.world.Direction;
import org.bukkit.ChatColor;

public class ElectricityMeterInstance extends StructureInstance implements EnergyContainer {

    protected Socket inEnergy;

    protected Socket outEnergy;

    protected Data<Integer> dummyStoredEnergy = new Data<>(dataContainer, Data.Type.INT, 0);

    protected Data<Integer> totalTransferred = dataContainer.persist("total", Data.Type.INT, 0);

    ElectricityMeterInstance(LoadContext context) {
        super(context);
    }

    public ElectricityMeterInstance(BlockLocation location, StructureBodyDef bodyDef, Direction direction) {
        super(Structures.ELECTRICITY_METER, location, bodyDef, direction);
    }

    @Override
    public void tick() {
        inEnergy.updateNetwork();
    }

    @Override
    protected void setup() throws SetupException {
        inEnergy = requireConnector("in_energy");
        inEnergy.maximumInput = Integer.MAX_VALUE;

        outEnergy = requireConnector("out_energy");
        outEnergy.maximumOutput = Integer.MAX_VALUE;
    }

    @Override
    public InspectableData setupInspectableData() {
        return new InspectableData((byte) 2)
          .property("Transfer", () -> ChatColor.GREEN.toString() + getFlowMeter().getSupplied() + ChatColor.GRAY + " RF/s")
          .property("Total", () -> MathUtils.formatNumber(totalTransferred.get()) + " RF");
    }

    @Override
    public int getEnergyCapacity() {
        return 0;
    }

    @Override
    public Data<Integer> getStoredEnergy() {
        return dummyStoredEnergy;
    }

    @Override
    public int consumeEnergy(int amount) {
        int consumed = inEnergy.consumeEnergy(amount);
        totalTransferred.update(totalTransferred.get() + consumed);

        getFlowMeter().update(consumed);
        return consumed;
    }

}
