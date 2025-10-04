package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.math.Direction;
import com.github.alantr7.torus.structure.*;
import com.github.alantr7.torus.structure.builder.StructureBodyDef;
import com.github.alantr7.torus.structure.component.Connector;
import com.github.alantr7.torus.structure.component.StructureComponent;
import com.github.alantr7.torus.structure.data.Data;
import com.github.alantr7.torus.world.BlockLocation;
import lombok.Getter;
import lombok.Setter;
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
    }

    @Override
    public void tick() {
        if (storedEnergy.get() < energyCapacity) {
            connector.updateConnections();
            if (!connector.connectedStructures.isEmpty()) {
                supplyEnergy((int) connector.consumeEnergy(Math.min(energyCapacity - storedEnergy.get(), 500)));
                updateModel();
            }
        }
    }

    private void updateModel() {
        float ratio = (float) storedEnergy.get() / (float) energyCapacity;
        float height = 1.125f * ratio;

        StructureComponent component = getComponent("charge");

        ItemDisplay entity = component.getModel().entities.getFirst();
        Transformation transformation = entity.getTransformation();
        transformation.getScale().y = height;
        transformation.getTranslation().y = height / 2f;
        entity.setTransformation(transformation);
    }

}
