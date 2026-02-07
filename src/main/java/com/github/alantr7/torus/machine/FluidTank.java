package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.TorusPlugin;
import com.github.alantr7.torus.model.ModelTemplate;
import com.github.alantr7.torus.model.de_provider.DisplayEntitiesPartModelTemplate;
import com.github.alantr7.torus.model.de_provider.PartModelElementItemDisplayRenderer;
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

public class FluidTank extends Structure {

    public static int FLUID_CAPACITY = 96000;

    static ModelTemplate MODEL_FLUID = new ModelTemplate(1);
    static {
        DisplayEntitiesPartModelTemplate part = new DisplayEntitiesPartModelTemplate("fluid");
        part.add(new PartModelElementItemDisplayRenderer(
          Material.BLUE_CONCRETE,
          new Vector3f(0, 1.265f, 0),
          new Vector3f(2.15f, 0, 2.15f),
          0, 0
        ));

        MODEL_FLUID.add(part);
    }

    public FluidTank() {
        super(TorusPlugin.DEFAULT_ADDON, "fluid_tank", "Fluid Tank", FluidTankInstance.class);
        portableData.add("fluid");
        portableData.add("stored");
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
    }

    @Override
    protected StructureInstance instantiate(@NotNull BlockLocation location, Direction direction, Pitch pitch) {
        return new FluidTankInstance(location, new StructureBodyDef(
          new StructurePartDef[]{
            new StructurePartDef("base", new Vector3f()),
            new StructurePartDef("in_fluid", new Vector3f(1f, 1f, 0f), new StructureSocketDef(
              Socket.Medium.FLUID, Socket.FlowDirection.IN, direction.getRight().mask()
            )),
            new StructurePartDef("out_fluid", new Vector3f(-1f, 0f, 0f), new StructureSocketDef(
              Socket.Medium.FLUID, Socket.FlowDirection.OUT, direction.getLeft().mask()
            )),
            new StructurePartDef("liquid", new Vector3f(0f, 0f, -1f)),
          }
        ), direction);
    }

    @Override
    protected void loadConfig() {
        super.loadConfig();
        FLUID_CAPACITY = config.getInt("fluid_settings.capacity", FLUID_CAPACITY);
    }

}
