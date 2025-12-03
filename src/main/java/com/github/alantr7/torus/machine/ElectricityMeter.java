package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.TorusPlugin;
import com.github.alantr7.torus.model.ModelLocation;
import com.github.alantr7.torus.structure.Structure;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.structure.builder.StructureBodyDef;
import com.github.alantr7.torus.structure.builder.StructureComponentDef;
import com.github.alantr7.torus.structure.builder.StructureConnectorDef;
import com.github.alantr7.torus.structure.component.Connector;
import com.github.alantr7.torus.world.BlockLocation;
import com.github.alantr7.torus.world.Direction;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class ElectricityMeter extends Structure {

    public ElectricityMeter() {
        super(TorusPlugin.DEFAULT_PACK, "electricity_meter", "Electricity Meter", ElectricityMeterInstance.class);
        isHeavy = false;
        portableData.add("total");
        hologramTranslation = new float[] { 1.4f, 0.1f, 0f };
        modelLocation = new ModelLocation("torus", "electricity_meter");
    }

    @Override
    protected StructureInstance instantiate(@NotNull BlockLocation location, Direction direction) {
        return new ElectricityMeterInstance(location, new StructureBodyDef(new StructureComponentDef[]{
          new StructureComponentDef("base", new Vector3f()),
          new StructureComponentDef("in_energy", new Vector3f(), new StructureConnectorDef(Connector.Matter.ENERGY, Connector.FlowDirection.IN, Direction.UP.mask())),
          new StructureComponentDef("out_energy", new Vector3f(), new StructureConnectorDef(Connector.Matter.ENERGY, Connector.FlowDirection.OUT, Direction.DOWN.mask())),
        }), direction);
    }

}
