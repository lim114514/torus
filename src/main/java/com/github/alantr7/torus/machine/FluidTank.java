package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.TorusPlugin;
import com.github.alantr7.torus.api.resource.ResourceLocation;
import com.github.alantr7.torus.world.BlockLocation;
import com.github.alantr7.torus.world.Direction;
import com.github.alantr7.torus.math.ByteArrayBuilder;
import com.github.alantr7.torus.structure.Structure;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.structure.builder.StructureBodyDef;
import com.github.alantr7.torus.structure.builder.StructureComponentDef;
import com.github.alantr7.torus.structure.builder.StructureSocketDef;
import com.github.alantr7.torus.structure.component.Socket;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class FluidTank extends Structure {

    public static int FLUID_CAPACITY = 96000;

    public FluidTank() {
        super(TorusPlugin.DEFAULT_ADDON, "fluid_tank", "Fluid Tank", FluidTankInstance.class);
        portableData.add("fluid");
        portableData.add("stored");
        modelLocation = new ResourceLocation(
          addon.externalContainer, "models/fluid_tank.model.yml",
          addon.classpathContainer, "configs/torus/models/fluid_tank.model.yml"
        );
    }

    @Override
    protected void createBounds(ByteArrayBuilder builder) {
        for (int i = 0; i < 3; i++) {
            for (int j = -1; j <= 1; j++) {
                for (int k = -1; k <= 1; k++) {
                    builder.add(j, i, k);
                }
            }
        }
        builder.add(0, 3, 0);
    }

    @Override
    protected StructureInstance instantiate(@NotNull BlockLocation location, Direction direction) {
        return new FluidTankInstance(location, new StructureBodyDef(
          new StructureComponentDef[]{
            new StructureComponentDef("base", new Vector3f()),
            new StructureComponentDef("in_fluid", new Vector3f(0f, 3f, 0f), new StructureSocketDef(
              Socket.Matter.FLUID, Socket.FlowDirection.IN, Direction.UP.mask()
            )),
            new StructureComponentDef("out_fluid", new Vector3f(0f, 0f, -1f), new StructureSocketDef(
              Socket.Matter.FLUID, Socket.FlowDirection.OUT, direction.mask()
            )),
            new StructureComponentDef("liquid", new Vector3f(0f, 0f, -1f)),
          }
        ), direction);
    }

    @Override
    protected void loadConfig() {
        super.loadConfig();
        FLUID_CAPACITY = config.getInt("fluid_settings.capacity", FLUID_CAPACITY);
    }

}
