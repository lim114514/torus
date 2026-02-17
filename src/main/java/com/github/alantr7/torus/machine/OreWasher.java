package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.TorusPlugin;
import com.github.alantr7.torus.item.ItemCriteria;
import com.github.alantr7.torus.structure.StructureFlag;
import com.github.alantr7.torus.structure.property.Property;
import com.github.alantr7.torus.structure.property.PropertyType;
import com.github.alantr7.torus.utils.ByteArrayBuilder;
import com.github.alantr7.torus.world.Direction;
import com.github.alantr7.torus.structure.Structure;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.structure.builder.StructureBodyDef;
import com.github.alantr7.torus.structure.builder.StructurePartDef;
import com.github.alantr7.torus.structure.builder.StructureSocketDef;
import com.github.alantr7.torus.structure.socket.Socket;
import com.github.alantr7.torus.world.BlockLocation;
import com.github.alantr7.torus.world.Pitch;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import static com.github.alantr7.torus.lang.Localization.translatable;

public class OreWasher extends Structure {

    public static ItemCriteria INPUT_CRITERIA = new ItemCriteria();

    public OreWasher() {
        super(TorusPlugin.DEFAULT_ADDON, "ore_washer", translatable("structure.ore_washer.name"), OreWasherInstance.class);
        setFlags(StructureFlag.COLLIDABLE | StructureFlag.TICKABLE | StructureFlag.HEAVY);
        portableData.add("energy");
        portableData.add("fluid");
        registerProperty(new Property<>("energy_settings.capacity", PropertyType.INT, 2000));
        registerProperty(new Property<>("energy_settings.consumption", PropertyType.INT, 300));
        registerProperty(new Property<>("energy_settings.maximum_input", PropertyType.INT, 500));
        registerProperty(new Property<>("fluid_settings.capacity", PropertyType.INT, 1000));
        registerProperty(new Property<>("fluid_settings.consumption", PropertyType.INT, 100));
    }

    @Override
    protected void createBounds(ByteArrayBuilder builder) {
        builder.add(0, 0, 0);
        builder.add(0, 0, 1);
        builder.add(0, 1, 0);
    }

    @Override
    protected StructureInstance instantiate(@NotNull BlockLocation location, Direction direction, Pitch pitch) {
        return new OreWasherInstance(location, new StructureBodyDef(
          new StructurePartDef[] {
            new StructurePartDef("body", new Vector3f()),
            new StructurePartDef("item_connector", new Vector3f(0f, 1f, 0f), new StructureSocketDef(
              Socket.Medium.ITEM, Socket.FlowDirection.IN, Direction.UP.mask()
            )),
            new StructurePartDef("out_connector", new Vector3f(0f, 0, 0f), new StructureSocketDef(
              Socket.Medium.ITEM, Socket.FlowDirection.OUT, direction.mask()
            )),
            new StructurePartDef("power_connector", new Vector3f(0f, 0, 1f), new StructureSocketDef(
              Socket.Medium.ENERGY, Socket.FlowDirection.IN, direction.getOpposite().mask()
            )),
            new StructurePartDef("fluid_connector", new Vector3f(0f, 0, 1f), new StructureSocketDef(
              Socket.Medium.FLUID, Socket.FlowDirection.IN, Direction.UP.mask()
            ))
          }
        ), direction);
    }

}
