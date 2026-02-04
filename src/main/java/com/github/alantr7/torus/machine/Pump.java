package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.TorusPlugin;
import com.github.alantr7.torus.model.ModelTemplate;
import com.github.alantr7.torus.model.de_provider.DisplayEntitiesPartModelTemplate;
import com.github.alantr7.torus.model.de_provider.PartModelElementItemDisplayRenderer;
import com.github.alantr7.torus.world.BlockLocation;
import com.github.alantr7.torus.world.Direction;
import com.github.alantr7.torus.utils.ByteArrayBuilder;
import com.github.alantr7.torus.structure.Structure;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.structure.builder.StructureBodyDef;
import com.github.alantr7.torus.structure.builder.StructurePartDef;
import com.github.alantr7.torus.structure.builder.StructureSocketDef;
import com.github.alantr7.torus.structure.socket.Socket;
import com.github.alantr7.torus.world.Pitch;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class Pump extends Structure {

    public static int ENERGY_CAPACITY = 500;

    public static int ENERGY_CONSUMPTION = 50;

    public static int ENERGY_MAXIMUM_INPUT = 25;

    public static int FLUID_CAPACITY = 1_000;

    public static int MAXIMUM_PIPE_LENGTH = 32;

    static ModelTemplate MODEL_PIPE = new ModelTemplate(1);
    static {
        DisplayEntitiesPartModelTemplate part = new DisplayEntitiesPartModelTemplate("pipe");
        part.add(new PartModelElementItemDisplayRenderer(
          Material.LIGHT_BLUE_TERRACOTTA,
          new Vector3f(0, 0, 0),
          new Vector3f(0.1875f, 2, 0.1875f),
          0, 0
        ));
        MODEL_PIPE.add(part);
    }

    public Pump() {
        super(TorusPlugin.DEFAULT_ADDON, "pump", "Pump", PumpInstance.class);
        portableData.add("energy");
        portableData.add("fluid");
        portableData.add("amount");
    }

    @Override
    protected void createBounds(ByteArrayBuilder builder) {
        builder.add(0, 0, 0);
        builder.add(0, 1, 0);
    }

    @Override
    protected StructureInstance instantiate(@NotNull BlockLocation location, Direction direction, Pitch pitch) {
        return new PumpInstance(location, new StructureBodyDef(
          new StructurePartDef[]{
            new StructurePartDef("base", new Vector3f()),
            new StructurePartDef("power_connector", new Vector3f(0, 1, 0), new StructureSocketDef(
              Socket.Medium.ENERGY, Socket.FlowDirection.IN, direction.getOpposite().mask()
            )),
            new StructurePartDef("fluid_connector", new Vector3f(0, 1, 0), new StructureSocketDef(
              Socket.Medium.FLUID, Socket.FlowDirection.OUT, Direction.UP.mask()
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
