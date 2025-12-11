package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.TorusPlugin;
import com.github.alantr7.torus.model.ModelLocation;
import com.github.alantr7.torus.world.BlockLocation;
import com.github.alantr7.torus.world.Direction;
import com.github.alantr7.torus.math.ByteArrayBuilder;
import com.github.alantr7.torus.structure.Structure;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.structure.builder.StructureBodyDef;
import com.github.alantr7.torus.structure.builder.StructureComponentDef;
import com.github.alantr7.torus.structure.builder.StructureSocketDef;
import com.github.alantr7.torus.structure.component.Socket;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class Pump extends Structure {

    public static int ENERGY_CAPACITY = 500;

    public static int ENERGY_CONSUMPTION = 50;

    public static int ENERGY_MAXIMUM_INPUT = 25;

    public static int FLUID_CAPACITY = 1_000;

    public static int MAXIMUM_PIPE_LENGTH = 32;

    public Pump() {
        super(TorusPlugin.DEFAULT_PACK, "pump", "Pump", PumpInstance.class);
        portableData.add("energy");
        portableData.add("fluid");
        portableData.add("amount");
        modelLocation = new ModelLocation("torus", "pump");
    }

    @Override
    protected void createBounds(ByteArrayBuilder builder) {
        builder.add(0, 0, 0);
        builder.add(0, 1, 0);
    }

    @Override
    protected StructureInstance instantiate(@NotNull BlockLocation location, Direction direction) {
        return new PumpInstance(location, new StructureBodyDef(
          new StructureComponentDef[]{
            new StructureComponentDef("base", new Vector3f()),
            new StructureComponentDef("power_connector", new Vector3f(0, 1, 0), new StructureSocketDef(
              Socket.Matter.ENERGY, Socket.FlowDirection.IN, direction.getOpposite().mask()
            )),
            new StructureComponentDef("fluid_connector", new Vector3f(0, 1, 0), new StructureSocketDef(
              Socket.Matter.FLUID, Socket.FlowDirection.OUT, Direction.UP.mask()
            ))
          }
        ), direction);
    }

    @Override
    protected void loadConfig() {
        super.loadConfig();
        ENERGY_CAPACITY = config.getInt("energy_settings.capacity", ENERGY_CAPACITY);
        ENERGY_CONSUMPTION = config.getInt("energy_settings.consumption", ENERGY_CONSUMPTION);
        ENERGY_MAXIMUM_INPUT = config.getInt("energy_settings.maximum_input", ENERGY_MAXIMUM_INPUT);
        FLUID_CAPACITY = config.getInt("fluid_settings.capacity", FLUID_CAPACITY);
        MAXIMUM_PIPE_LENGTH = config.getInt("special_settings.maximum_pipe_length");
    }

}
