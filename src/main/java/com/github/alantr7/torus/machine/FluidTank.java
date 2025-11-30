package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.model.ModelLocation;
import com.github.alantr7.torus.model.ModelTemplate;
import com.github.alantr7.torus.world.BlockLocation;
import com.github.alantr7.torus.world.Direction;
import com.github.alantr7.torus.math.ByteArrayBuilder;
import com.github.alantr7.torus.structure.Structure;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.structure.builder.StructureBodyDef;
import com.github.alantr7.torus.structure.builder.StructureComponentDef;
import com.github.alantr7.torus.structure.builder.StructureConnectorDef;
import com.github.alantr7.torus.structure.component.Connector;
import com.github.alantr7.torus.model.PartModelElementItemDisplayRenderer;
import com.github.alantr7.torus.model.PartModelTemplate;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class FluidTank extends Structure {

    public FluidTank() {
        super("torus:fluid_tank", "Fluid Tank", FluidTankInstance.class);
        itemDropDataWhitelist.add("fluid");
        itemDropDataWhitelist.add("stored");
        modelLocation = new ModelLocation("torus", "fluid_tank");
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
            new StructureComponentDef("input", new Vector3f(0f, 3f, 0f), new StructureConnectorDef(
              Connector.Matter.FLUID, Connector.FlowDirection.IN, Direction.UP.mask()
            )),
            new StructureComponentDef("output", new Vector3f(0f, 0f, -1f), new StructureConnectorDef(
              Connector.Matter.FLUID, Connector.FlowDirection.OUT, direction.mask()
            )),
            new StructureComponentDef("liquid", new Vector3f(0f, 0f, -1f)),
          }
        ), direction);
    }

}
