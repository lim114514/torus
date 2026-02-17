package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.TorusPlugin;
import com.github.alantr7.torus.structure.StructureFlag;
import com.github.alantr7.torus.structure.property.Property;
import com.github.alantr7.torus.structure.property.PropertyType;
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
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import static com.github.alantr7.torus.lang.Localization.translatable;

public class SolarGenerator extends Structure {

    public SolarGenerator() {
        super(TorusPlugin.DEFAULT_ADDON, "solar_generator", translatable("structure.solar_generator.name"), SolarGeneratorInstance.class);
        setFlags(StructureFlag.COLLIDABLE | StructureFlag.TICKABLE | StructureFlag.HEAVY);
        portableData.add("energy");
        hologramOffset = new float[] { 0, 1, 0 };
        hologramTranslation = new float[] { 1.2f, 0, 0 };
        registerProperty(new Property<>("energy_settings.production", PropertyType.INT, 50));
        registerProperty(new Property<>("energy_settings.capacity", PropertyType.INT, 2000));
        registerProperty(new Property<>("energy_settings.maximum_output", PropertyType.INT, 100));
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
        return new SolarGeneratorInstance(location, new StructureBodyDef(new StructurePartDef[]{
          new StructurePartDef("base", new Vector3f()),
          new StructurePartDef("out_energy", new Vector3f(), new StructureSocketDef(
            Socket.Medium.ENERGY, Socket.FlowDirection.OUT, direction.getOpposite().mask()
          ))
        }), direction);
    }

}
