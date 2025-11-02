package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.world.Direction;
import com.github.alantr7.torus.structure.*;
import com.github.alantr7.torus.structure.builder.StructureBodyDef;
import com.github.alantr7.torus.structure.component.Connector;
import com.github.alantr7.torus.structure.component.StructureComponent;
import com.github.alantr7.torus.structure.data.Data;
import com.github.alantr7.torus.world.BlockLocation;
import lombok.Getter;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.util.Transformation;

public class PowerBankInstance extends StructureInstance implements EnergyContainer {

    @Getter
    protected int energyCapacity = 20_000;

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
        connector.maximumInput = 500;
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

    public void updateModel() {
        float ratio = (float) storedEnergy.get() / (float) energyCapacity;
        float height = 1.125f * ratio;

        ItemDisplay entity = this.model.getPart("progress").entityReferences.getFirst().getEntity();
        Transformation transformation = entity.getTransformation();
        transformation.getScale().y = height;
        transformation.getTranslation().y = PowerBank.PROGRESS_MODEL.parts.getFirst().offset[1] + height / 2f;
        entity.setTransformation(transformation);
    }

}
