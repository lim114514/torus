package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.model.de_provider.DisplayEntitiesPartModel;
import com.github.alantr7.torus.structure.inspection.InspectableData;
import com.github.alantr7.torus.world.Direction;
import com.github.alantr7.torus.structure.*;
import com.github.alantr7.torus.structure.builder.StructureBodyDef;
import com.github.alantr7.torus.structure.component.Socket;
import com.github.alantr7.torus.structure.data.Data;
import com.github.alantr7.torus.world.BlockLocation;
import lombok.Getter;
import org.bukkit.entity.Display;
import org.bukkit.util.Transformation;

public class PowerBankInstance extends StructureInstance implements EnergyContainer {

    @Getter
    protected Data<Integer> storedEnergy = dataContainer.persist("energy", Data.Type.INT, 0);

    Socket socket;

    Display chargeIndicatorDisplay;

    public PowerBankInstance(BlockLocation location, StructureBodyDef bodyDef, Direction direction) {
        super(Structures.POWER_BANK, location, bodyDef, direction);
    }

    PowerBankInstance(LoadContext context) {
        super(context);
    }

    @Override
    protected void setup() {
        socket = getSocket("power_connector");
        socket.maximumInput = PowerBank.ENERGY_MAXIMUM_INPUT;
        socket.maximumOutput = PowerBank.ENERGY_MAXIMUM_OUTPUT;

        chargeIndicatorDisplay = ((DisplayEntitiesPartModel) PowerBank.MODEL_CHARGE_INDICATOR.toModel(location, direction)
          .parts.get("charge")).entityReferences.getFirst().getEntity();
    }

    @Override
    public InspectableData setupInspectableData() {
        return new InspectableData((byte) 1)
          .property("RF", InspectableData.TEMPLATE_RF.apply(this));
    }

    int energyAtLastTick;
    @Override
    public void tick() {
        socket.maintainEnergy(this);
        if (energyAtLastTick != storedEnergy.get()) {
            updateChargeIndicator();
        }

        energyAtLastTick = storedEnergy.get();
    }

    @Override
    public void handleModelDestroy() {
        super.handleModelDestroy();
        chargeIndicatorDisplay.remove();
    }

    @Override
    public int getEnergyCapacity() {
        return PowerBank.ENERGY_CAPACITY;
    }

    private void updateChargeIndicator() {
        float ratio = (float) storedEnergy.get() / PowerBank.ENERGY_CAPACITY;
        float height = 1.125f * ratio;
        Transformation transformation = chargeIndicatorDisplay.getTransformation();
        transformation.getScale().y = height;
        transformation.getTranslation().y = 0.1675f + height / 2f;
        chargeIndicatorDisplay.setTransformation(transformation);
    }

}
