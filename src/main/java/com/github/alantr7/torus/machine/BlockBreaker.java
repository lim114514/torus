package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.TorusPlugin;
import com.github.alantr7.torus.structure.property.Property;
import com.github.alantr7.torus.structure.property.PropertyType;
import com.github.alantr7.torus.world.BlockLocation;
import com.github.alantr7.torus.world.Direction;
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

public class BlockBreaker extends Structure {

    public BlockBreaker() {
        super(TorusPlugin.DEFAULT_ADDON, "block_breaker", translatable("structure.block_breaker.name"), BlockBreakerInstance.class);
        isHeavy = false;
        isOmnidirectional = true;
        portableData.add("energy");
        hologramOffset = new float[] { 0, 0, 0 };
        hologramTranslation = new float[] { 1.5f, 0, 0 };
        registerProperty(new Property<>("energy_settings.capacity", PropertyType.INT, 50));
        registerProperty(new Property<>("energy_settings.maximum_input", PropertyType.INT, 100));
        registerProperty(new Property<>("energy_settings.consumption_on_mine", PropertyType.INT, 25));
    }

    @Override
    protected StructureInstance instantiate(@NotNull BlockLocation location, Direction direction, Pitch pitch) {
        return new BlockBreakerInstance(location, new StructureBodyDef(
          new StructurePartDef[]{
            new StructurePartDef("body", new Vector3f(0, 0, 0)),
            new StructurePartDef("power_connector", new Vector3f(0, 0, 0), new StructureSocketDef(
              Socket.Medium.ENERGY, Socket.FlowDirection.IN, direction.getOpposite().mask()
            )),
            new StructurePartDef("item_connector", new Vector3f(0, 0, 0), new StructureSocketDef(
              Socket.Medium.ITEM, Socket.FlowDirection.OUT, Direction.DOWN.mask()
            )) }
        ), direction, pitch);
    }

}
