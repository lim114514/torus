package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.TorusPlugin;
import com.github.alantr7.torus.item.ItemCriteria;
import com.github.alantr7.torus.structure.property.Property;
import com.github.alantr7.torus.structure.property.PropertyType;
import com.github.alantr7.torus.structure.state.State;
import com.github.alantr7.torus.structure.state.StateType;
import com.github.alantr7.torus.world.Direction;
import com.github.alantr7.torus.utils.ByteArrayBuilder;
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

public class OreCrusher extends Structure {

    public static ItemCriteria INPUT_CRITERIA = new ItemCriteria();

    public static final State<Boolean> STATE_WORKING = new State<>("working", StateType.BOOLEAN, false);

    public OreCrusher() {
        super(TorusPlugin.DEFAULT_ADDON, "ore_crusher", translatable("structure.ore_crusher.name"), OreCrusherInstance.class);
        offset = new byte[]{ 0, 0, -1 };
        hologramOffset = new float[] { 0, 1f, 0 };
        portableData.add("energy");
        registerState(STATE_WORKING);
        registerProperty(new Property<>("energy_settings.consumption", PropertyType.INT, 300));
        registerProperty(new Property<>("energy_settings.capacity", PropertyType.INT, 2000));
        registerProperty(new Property<>("energy_settings.maximum_input", PropertyType.INT, 500));
    }

    @Override
    protected void createBounds(ByteArrayBuilder builder) {
        builder.add(0, 0, 0);
        builder.add(0, 1, 0);
        builder.add(0, 2, 0);
        builder.add(1, 0, 0);
        builder.add(-2, 0, 0);
        builder.add(-2, 1, 0);
        builder.add(-1, 0, 0);
        builder.add(0, 0, -1);
        builder.add(0, 1, -1);
    }

    @Override
    protected StructureInstance instantiate(@NotNull BlockLocation location, Direction direction, Pitch pitch) {
        return new OreCrusherInstance(location, new StructureBodyDef(
          new StructurePartDef[]{
            new StructurePartDef("body", new Vector3f()),
            new StructurePartDef("power_connector", new Vector3f(-2, 1, 0), new StructureSocketDef(
              Socket.Medium.ENERGY, Socket.FlowDirection.IN, direction.getLeft().mask()
            )),
            new StructurePartDef("item_connector", new Vector3f(0, 2, 0), new StructureSocketDef(
              Socket.Medium.ITEM, Socket.FlowDirection.IN, Direction.UP.mask()
            )),
            new StructurePartDef("out_connector", new Vector3f(1, 0, 0), new StructureSocketDef(
              Socket.Medium.ITEM, Socket.FlowDirection.OUT, direction.getRight().mask()
            )),
            new StructurePartDef("wheel_left", new Vector3f()),
            new StructurePartDef("wheel_right", new Vector3f())
          }
        ), direction);
    }

}
