package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.TorusPlugin;
import com.github.alantr7.torus.math.ByteArrayBuilder;
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

public class Windmill extends Structure {

    public static int ENERGY_CAPACITY = 75;

    public static int ENERGY_PRODUCTION = 3_000;

    public static int ENERGY_MAXIMUM_OUTPUT = 100;

    public static final float MAXIMUM_SPEED = 1.85f * (float) Math.PI / 3f;

    public Windmill() {
        super(TorusPlugin.DEFAULT_PACK, "windmill", "Windmill", WindmillInstance.class);
        portableData.add("energy");
        modelLocation = new ModelLocation("torus", "windmill");
    }

    @Override
    protected void createBounds(ByteArrayBuilder builder) {
        builder.add(0, 0, 0);
        builder.add(0, 1, 0);
        builder.add(0, 2, 0);
        builder.add(0, 3, 0);
    }

    @Override
    protected StructureInstance instantiate(@NotNull BlockLocation location, Direction direction) {
        return new WindmillInstance(location, new StructureBodyDef(new StructureComponentDef[]{
          new StructureComponentDef("base", new Vector3f()),
          new StructureComponentDef("out_energy", new Vector3f(), new StructureConnectorDef(
            Connector.Matter.ENERGY, Connector.FlowDirection.OUT, direction.mask()
          ))
        }), direction);
    }

    @Override
    protected void loadConfig() {
        super.loadConfig();
        ENERGY_CAPACITY = config.getInt("energy_settings.capacity", ENERGY_CAPACITY);
        ENERGY_PRODUCTION = config.getInt("energy_settings.production", ENERGY_PRODUCTION);
        ENERGY_MAXIMUM_OUTPUT = config.getInt("energy_settings.maximum_output", ENERGY_MAXIMUM_OUTPUT);
    }

}
