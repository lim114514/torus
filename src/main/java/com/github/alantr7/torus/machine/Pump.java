package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.TorusPlugin;
import com.github.alantr7.torus.model.ModelTemplate;
import com.github.alantr7.torus.model.de_provider.DisplayEntitiesPartModelTemplate;
import com.github.alantr7.torus.model.de_provider.PartModelElementItemDisplayRenderer;
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
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import static com.github.alantr7.torus.lang.Localization.translatable;

public class Pump extends Structure {

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
        super(TorusPlugin.DEFAULT_ADDON, "pump", translatable("structure.pump.name"), PumpInstance.class);
        setFlags(StructureFlag.COLLIDABLE | StructureFlag.TICKABLE | StructureFlag.HEAVY);
        setPortableData("energy", "fluid", "amount");
        setHologramOffset(new Vector3f(0, 0.6f, 0));
        registerProperty(new Property<>("energy_settings.capacity", PropertyType.INT, 500));
        registerProperty(new Property<>("energy_settings.consumption", PropertyType.INT, 50));
        registerProperty(new Property<>("energy_settings.maximum_input", PropertyType.INT, 25));
        registerProperty(new Property<>("fluid_settings.capacity", PropertyType.INT, 1000));
        registerProperty(new Property<>("special_settings.maximum_pipe_length", PropertyType.INT, 32));
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
}
