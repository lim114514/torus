package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.exception.SetupException;
import com.github.alantr7.torus.structure.socket.EnergySocket;
import com.github.alantr7.torus.utils.MathUtils;
import com.github.alantr7.torus.structure.EnergyContainer;
import com.github.alantr7.torus.structure.LoadContext;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.structure.Structures;
import com.github.alantr7.torus.structure.builder.StructureBodyDef;
import com.github.alantr7.torus.structure.data.Data;
import com.github.alantr7.torus.structure.inspection.InspectableDataContainer;
import com.github.alantr7.torus.world.BlockLocation;
import com.github.alantr7.torus.world.Direction;
import org.bukkit.ChatColor;

import static com.github.alantr7.torus.lang.Localization.translatable;
import static com.github.alantr7.torus.lang.Localization.translate;

public class ElectricityMeterInstance extends StructureInstance implements EnergyContainer {

    protected EnergySocket inEnergy;

    protected EnergySocket outEnergy;

    protected Data<Integer> dummyStoredEnergy = new Data<>(dataContainer, Data.Type.INT, 0);

    protected Data<Integer> totalTransferred = dataContainer.persist("total", Data.Type.INT, 0);

    ElectricityMeterInstance(LoadContext context) {
        super(context);
    }

    public ElectricityMeterInstance(BlockLocation location, StructureBodyDef bodyDef, Direction direction) {
        super(Structures.ELECTRICITY_METER, location, bodyDef, direction);
    }

    @Override
    protected void setup() throws SetupException {
        inEnergy = requireSocket("in_energy", EnergySocket.class);
        inEnergy.maximumInput = Integer.MAX_VALUE;

        outEnergy = requireSocket("out_energy", EnergySocket.class);
        outEnergy.maximumOutput = Integer.MAX_VALUE;
    }

    @Override
    public InspectableDataContainer setupInspectableData() {
        return new InspectableDataContainer((byte) 2)
          .property(translatable("inspection.electricity_meter.transfer_rate"), () -> ChatColor.GREEN.toString() + getFlowMeter().getSupplied() + ChatColor.GRAY + " " + translate("inspection.energy_unit") + "/s")
          .property(translatable("inspection.electricity_meter.total_transferred"), () -> MathUtils.formatNumber(totalTransferred.get()) + " " + translate("inspection.energy_unit"));
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
