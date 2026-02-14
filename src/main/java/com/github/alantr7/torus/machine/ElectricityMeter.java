package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.TorusPlugin;
import com.github.alantr7.torus.structure.Structure;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.structure.builder.StructureBodyDef;
import com.github.alantr7.torus.structure.builder.StructurePartDef;
import com.github.alantr7.torus.structure.builder.StructureSocketDef;
import com.github.alantr7.torus.structure.socket.Socket;
import com.github.alantr7.torus.world.BlockLocation;
import com.github.alantr7.torus.world.Direction;
import com.github.alantr7.torus.world.Pitch;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import static com.github.alantr7.torus.lang.Localization.translatable;

public class ElectricityMeter extends Structure {

    public ElectricityMeter() {
        super(TorusPlugin.DEFAULT_ADDON, "electricity_meter", translatable("structure.electricity_meter.name"), ElectricityMeterInstance.class);
        isTickable = false;
        isHeavy = false;
        portableData.add("total");
        hologramTranslation = new float[] { 1.4f, 0.1f, 0f };
    }

    @Override
    protected StructureInstance instantiate(@NotNull BlockLocation location, Direction direction, Pitch pitch) {
        return new ElectricityMeterInstance(location, new StructureBodyDef(new StructurePartDef[]{
          new StructurePartDef("base", new Vector3f()),
          new StructurePartDef("in_energy", new Vector3f(), new StructureSocketDef(Socket.Medium.ENERGY, Socket.FlowDirection.IN, Direction.UP.mask())),
          new StructurePartDef("out_energy", new Vector3f(), new StructureSocketDef(Socket.Medium.ENERGY, Socket.FlowDirection.OUT, Direction.DOWN.mask())),
        }), direction);
    }

}
