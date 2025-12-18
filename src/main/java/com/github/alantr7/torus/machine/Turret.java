package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.TorusPlugin;
import com.github.alantr7.torus.math.ByteArrayBuilder;
import com.github.alantr7.torus.model.ModelLocation;
import com.github.alantr7.torus.world.Direction;
import com.github.alantr7.torus.structure.Structure;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.structure.builder.StructureBodyDef;
import com.github.alantr7.torus.structure.builder.StructureComponentDef;
import com.github.alantr7.torus.structure.builder.StructureSocketDef;
import com.github.alantr7.torus.structure.component.Socket;
import com.github.alantr7.torus.world.BlockLocation;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class Turret extends Structure {

    public static int ENERGY_CAPACITY = 5_000;

    public static int ENERGY_CONSUMPTION = 250;

    public static int ENERGY_MAXIMUM_INPUT = 100;

    public Turret() {
        super(TorusPlugin.DEFAULT_ADDON, "turret", "Laser Turret", TurretInstance.class);
        portableData.add("energy");
        modelLocation = new ModelLocation("torus", "turret");
    }

    @Override
    protected void createBounds(ByteArrayBuilder builder) {
        builder.add(0, 0, 0);
        builder.add(0, 1, 0);
    }

    @Override
    protected StructureInstance instantiate(@NotNull BlockLocation location, Direction direction) {
        return new TurretInstance(location, new StructureBodyDef(new StructureComponentDef[]{
          new StructureComponentDef("base", new Vector3f()),
          new StructureComponentDef("head", new Vector3f()),
          new StructureComponentDef("in_item", new Vector3f(), new StructureSocketDef(
            Socket.Matter.ITEM, Socket.FlowDirection.IN, direction.getOpposite().mask()
          )),
          new StructureComponentDef("in_energy", new Vector3f(), new StructureSocketDef(
            Socket.Matter.ENERGY, Socket.FlowDirection.IN, Direction.DOWN.mask()
          ))
        }), direction);
    }

    @Override
    protected void loadConfig() {
        super.loadConfig();
        ENERGY_CAPACITY = config.getInt("energy_settings.capacity", ENERGY_CAPACITY);
        ENERGY_CONSUMPTION = config.getInt("energy_settings.consumption", ENERGY_CONSUMPTION);
        ENERGY_MAXIMUM_INPUT = config.getInt("energy_settings.maximum_input", ENERGY_MAXIMUM_INPUT);
    }

}
