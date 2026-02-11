package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.TorusPlugin;
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

public class Turret extends Structure {

    public Turret() {
        super(TorusPlugin.DEFAULT_ADDON, "turret", "Laser Turret", TurretInstance.class);
        portableData.add("energy");
        registerProperty(new Property<>("energy_settings.capacity", PropertyType.INT, 3_000));
        registerProperty(new Property<>("energy_settings.consumption", PropertyType.INT, 250));
        registerProperty(new Property<>("energy_settings.maximum_input", PropertyType.INT, 100));
    }

    @Override
    protected void createBounds(ByteArrayBuilder builder) {
        builder.add(0, 0, 0);
        builder.add(0, 1, 0);
    }

    @Override
    protected StructureInstance instantiate(@NotNull BlockLocation location, Direction direction, Pitch pitch) {
        return new TurretInstance(location, new StructureBodyDef(new StructurePartDef[]{
          new StructurePartDef("base", new Vector3f()),
          new StructurePartDef("head", new Vector3f()),
          new StructurePartDef("in_item", new Vector3f(), new StructureSocketDef(
            Socket.Medium.ITEM, Socket.FlowDirection.IN, direction.getOpposite().mask()
          )),
          new StructurePartDef("in_energy", new Vector3f(), new StructureSocketDef(
            Socket.Medium.ENERGY, Socket.FlowDirection.IN, Direction.DOWN.mask()
          ))
        }), direction);
    }

}
