package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.TorusPlugin;
import com.github.alantr7.torus.world.BlockLocation;
import com.github.alantr7.torus.world.Direction;
import com.github.alantr7.torus.utils.ByteArrayBuilder;
import com.github.alantr7.torus.structure.Structure;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.structure.builder.StructureBodyDef;
import com.github.alantr7.torus.structure.builder.StructureComponentDef;
import com.github.alantr7.torus.structure.builder.StructureSocketDef;
import com.github.alantr7.torus.structure.component.Socket;
import com.github.alantr7.torus.world.Pitch;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class SolarGenerator extends Structure {

    public static int ENERGY_PRODUCTION = 50;

    public static int ENERGY_CAPACITY = 2000;

    public static int ENERGY_MAXIMUM_OUTPUT = 100;

    public SolarGenerator() {
        super(TorusPlugin.DEFAULT_ADDON, "solar_generator", "Solar Generator", SolarGeneratorInstance.class);
        portableData.add("energy");
    }

    @Override
    public void createBounds(ByteArrayBuilder builder) {
        builder.add(0, 0, 0);
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                builder.add(x, 1, z);
            }
        }
    }

    @Override
    protected StructureInstance instantiate(@NotNull BlockLocation location, Direction direction, Pitch pitch) {
        return new SolarGeneratorInstance(location, new StructureBodyDef(new StructureComponentDef[]{
          new StructureComponentDef("base", new Vector3f()),
          new StructureComponentDef("out_energy", new Vector3f(), new StructureSocketDef(
            Socket.Medium.ENERGY, Socket.FlowDirection.OUT, direction.getOpposite().mask()
          ))
        }), direction);
    }

    @Override
    protected void loadConfig() {
        super.loadConfig();
        ENERGY_PRODUCTION = config.getInt("energy_settings.production", ENERGY_PRODUCTION);
        ENERGY_CAPACITY = config.getInt("energy_settings.capacity", ENERGY_PRODUCTION);
        ENERGY_MAXIMUM_OUTPUT = config.getInt("energy_settings.maximum_output", ENERGY_PRODUCTION);
    }

}
