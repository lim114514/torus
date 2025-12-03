package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.structure.inspection.InspectableData;
import com.github.alantr7.torus.world.Direction;
import com.github.alantr7.torus.structure.*;
import com.github.alantr7.torus.structure.builder.StructureBodyDef;
import com.github.alantr7.torus.structure.component.Connector;
import com.github.alantr7.torus.structure.data.Data;
import com.github.alantr7.torus.world.BlockLocation;
import lombok.Getter;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.util.Transformation;

public class PowerBankInstance extends StructureInstance implements EnergyContainer {

    @Getter
    protected Data<Integer> storedEnergy = dataContainer.persist("energy", Data.Type.INT, 0);

    Connector connector;

    public PowerBankInstance(BlockLocation location, StructureBodyDef bodyDef, Direction direction) {
        super(Structures.POWER_BANK, location, bodyDef, direction);
    }

    PowerBankInstance(LoadContext context) {
        super(context);
    }

    @Override
    protected void setup() {
        connector = getConnector("power_connector");
        connector.maximumInput = PowerBank.ENERGY_MAXIMUM_INPUT;
        connector.maximumOutput = PowerBank.ENERGY_MAXIMUM_OUTPUT;
    }

    @Override
    public InspectableData setupInspectableData() {
        return new InspectableData((byte) 1)
          .property("RF", InspectableData.TEMPLATE_RF.apply(this));
    }

    int energyAtLastTick;
    @Override
    public void tick() {
        connector.maintainEnergy(this);
        if (energyAtLastTick != storedEnergy.get()) {
            updateModel();
        }

        energyAtLastTick = storedEnergy.get();
    }

    @Override
    public int getEnergyCapacity() {
        return PowerBank.ENERGY_CAPACITY;
    }

    public void updateModel() {
        float ratio = (float) storedEnergy.get() / PowerBank.ENERGY_CAPACITY;
        float height = 1.125f * ratio;

        ItemDisplay entity = this.model.getPart("progress").entityReferences.getFirst().getEntity();
        Transformation transformation = entity.getTransformation();
        transformation.getScale().y = height;
        transformation.getTranslation().y = 0.1675f + height / 2f;
        entity.setTransformation(transformation);
    }

}
