package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.TorusPlugin;
import com.github.alantr7.torus.api.resource.ResourceLocation;
import com.github.alantr7.torus.structure.Structure;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.structure.builder.StructureBodyDef;
import com.github.alantr7.torus.structure.builder.StructureComponentDef;
import com.github.alantr7.torus.structure.builder.StructureSocketDef;
import com.github.alantr7.torus.structure.component.Socket;
import com.github.alantr7.torus.world.BlockLocation;
import com.github.alantr7.torus.world.Direction;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class ElectricityMeter extends Structure {

    public ElectricityMeter() {
        super(TorusPlugin.DEFAULT_ADDON, "electricity_meter", "Electricity Meter", ElectricityMeterInstance.class);
        isHeavy = false;
        portableData.add("total");
        hologramTranslation = new float[] { 1.4f, 0.1f, 0f };
        modelLocation = new ResourceLocation(
          addon.externalContainer, "models/electricity_meter.model.yml",
          addon.classpathContainer, "configs/torus/models/electricity_meter.model.yml"
        );
    }

    @Override
    protected StructureInstance instantiate(@NotNull BlockLocation location, Direction direction) {
        return new ElectricityMeterInstance(location, new StructureBodyDef(new StructureComponentDef[]{
          new StructureComponentDef("base", new Vector3f()),
          new StructureComponentDef("in_energy", new Vector3f(), new StructureSocketDef(Socket.Matter.ENERGY, Socket.FlowDirection.IN, Direction.UP.mask())),
          new StructureComponentDef("out_energy", new Vector3f(), new StructureSocketDef(Socket.Matter.ENERGY, Socket.FlowDirection.OUT, Direction.DOWN.mask())),
        }), direction);
    }

}
