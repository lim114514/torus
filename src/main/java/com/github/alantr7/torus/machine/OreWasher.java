package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.TorusPlugin;
import com.github.alantr7.torus.item.ItemCriteria;
import com.github.alantr7.torus.math.ByteArrayBuilder;
import com.github.alantr7.torus.model.ModelLocation;
import com.github.alantr7.torus.world.Direction;
import com.github.alantr7.torus.structure.Structure;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.structure.builder.StructureBodyDef;
import com.github.alantr7.torus.structure.builder.StructureComponentDef;
import com.github.alantr7.torus.structure.builder.StructureConnectorDef;
import com.github.alantr7.torus.structure.component.Socket;
import com.github.alantr7.torus.world.BlockLocation;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class OreWasher extends Structure {

    public static ItemCriteria INPUT_CRITERIA = new ItemCriteria();

    public static int ENERGY_CAPACITY = 10_000;

    public static int ENERGY_CONSUMPTION = 300;

    public static int ENERGY_MAXIMUM_INPUT = 500;

    public static int FLUID_CAPACITY = 1_000;

    public static int FLUID_CONSUMPTION = 100;

    public OreWasher() {
        super(TorusPlugin.DEFAULT_PACK, "ore_washer", "Ore Washer", OreWasherInstance.class);
        portableData.add("energy");
        portableData.add("fluid");
        modelLocation = new ModelLocation("torus", "ore_washer");
    }

    @Override
    protected void createBounds(ByteArrayBuilder builder) {
        builder.add(0, 0, 0);
        builder.add(0, 0, 1);
        builder.add(0, 1, 0);
    }

    @Override
    protected StructureInstance instantiate(@NotNull BlockLocation location, Direction direction) {
        return new OreWasherInstance(location, new StructureBodyDef(
          new StructureComponentDef[] {
            new StructureComponentDef("body", new Vector3f()),
            new StructureComponentDef("item_connector", new Vector3f(0f, 1f, 0f), new StructureConnectorDef(
              Socket.Matter.ITEM, Socket.FlowDirection.IN, Direction.UP.mask()
            )),
            new StructureComponentDef("out_connector", new Vector3f(0f, 0, 0f), new StructureConnectorDef(
              Socket.Matter.ITEM, Socket.FlowDirection.OUT, direction.mask()
            )),
            new StructureComponentDef("power_connector", new Vector3f(0f, 0, 1f), new StructureConnectorDef(
              Socket.Matter.ENERGY, Socket.FlowDirection.IN, direction.getOpposite().mask()
            )),
            new StructureComponentDef("fluid_connector", new Vector3f(0f, 0, 1f), new StructureConnectorDef(
              Socket.Matter.FLUID, Socket.FlowDirection.IN, Direction.UP.mask()
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
        FLUID_CONSUMPTION = config.getInt("fluid_settings.consumption", FLUID_CONSUMPTION);
    }

}
