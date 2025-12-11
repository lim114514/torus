package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.TorusPlugin;
import com.github.alantr7.torus.model.ModelLocation;
import com.github.alantr7.torus.world.Direction;
import com.github.alantr7.torus.math.ByteArrayBuilder;
import com.github.alantr7.torus.structure.Structure;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.structure.builder.StructureBodyDef;
import com.github.alantr7.torus.structure.builder.StructureComponentDef;
import com.github.alantr7.torus.structure.builder.StructureConnectorDef;
import com.github.alantr7.torus.structure.component.Socket;
import com.github.alantr7.torus.world.BlockLocation;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class PowerBank extends Structure {

    public static int ENERGY_CAPACITY = 20_000;

    public static int ENERGY_MAXIMUM_INPUT = 500;

    public static int ENERGY_MAXIMUM_OUTPUT = 500;

    public PowerBank() {
        super(TorusPlugin.DEFAULT_PACK, "power_bank", "Power Bank", PowerBankInstance.class);
        portableData.add("energy");
        modelLocation = new ModelLocation("torus", "power_bank");
    }

    @Override
    public void createBounds(ByteArrayBuilder builder) {
        builder.add(0, 0, 0);
        builder.add(0, 1, 0);
    }

    @Override
    protected StructureInstance instantiate(@NotNull BlockLocation location, Direction direction) {
        return new PowerBankInstance(location, new StructureBodyDef(new StructureComponentDef[]{
          new StructureComponentDef("base", new Vector3f()),
          new StructureComponentDef(
            "power_connector", new Vector3f(0, 1, 0), new StructureConnectorDef(
            Socket.Matter.ENERGY, Socket.FlowDirection.ALL, Direction.UP.mask()
          )),
          new StructureComponentDef("charge", new Vector3f())
        }), direction);
    }

    @Override
    protected void loadConfig() {
        super.loadConfig();
        ENERGY_CAPACITY = config.getInt("energy_settings.capacity", ENERGY_CAPACITY);
        ENERGY_MAXIMUM_INPUT = config.getInt("energy_settings.maximum_input", ENERGY_MAXIMUM_INPUT);
        ENERGY_MAXIMUM_OUTPUT = config.getInt("energy_settings.maximum_output", ENERGY_MAXIMUM_OUTPUT);
    }

}
