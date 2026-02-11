package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.TorusPlugin;
import com.github.alantr7.torus.structure.property.Property;
import com.github.alantr7.torus.structure.property.PropertyType;
import com.github.alantr7.torus.utils.ByteArrayBuilder;
import com.github.alantr7.torus.structure.Structure;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.structure.builder.StructureBodyDef;
import com.github.alantr7.torus.structure.builder.StructurePartDef;
import com.github.alantr7.torus.structure.builder.StructureSocketDef;
import com.github.alantr7.torus.structure.socket.Socket;
import com.github.alantr7.torus.structure.state.State;
import com.github.alantr7.torus.structure.state.StateType;
import com.github.alantr7.torus.world.BlockLocation;
import com.github.alantr7.torus.world.Direction;
import com.github.alantr7.torus.world.Pitch;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class Windmill extends Structure {

    public static final float MAXIMUM_SPEED = 1.85f * (float) Math.PI / 3f;

    public static final State<Boolean> STATE_ACTIVE = new State<>("active", StateType.BOOLEAN, false);

    public Windmill() {
        super(TorusPlugin.DEFAULT_ADDON, "windmill", "Windmill", WindmillInstance.class);
        portableData.add("energy");
        registerState(STATE_ACTIVE);
        registerProperty(new Property<>("energy_settings.production", PropertyType.INT, 3000));
        registerProperty(new Property<>("energy_settings.capacity", PropertyType.INT, 75));
        registerProperty(new Property<>("energy_settings.maximum_output", PropertyType.INT, 100));
    }

    @Override
    protected void createBounds(ByteArrayBuilder builder) {
        builder.add(0, 0, 0);
        builder.add(0, 1, 0);
        builder.add(0, 2, 0);
        builder.add(0, 3, 0);
    }

    @Override
    protected StructureInstance instantiate(@NotNull BlockLocation location, Direction direction, Pitch pitch) {
        return new WindmillInstance(location, new StructureBodyDef(new StructurePartDef[]{
          new StructurePartDef("base", new Vector3f()),
          new StructurePartDef("out_energy", new Vector3f(), new StructureSocketDef(
            Socket.Medium.ENERGY, Socket.FlowDirection.OUT, direction.mask()
          ))
        }), direction);
    }

}
