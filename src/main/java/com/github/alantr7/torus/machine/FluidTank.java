package com.github.alantr7.torus.machine;

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

    static PartModelTemplate MODEL_BASE = new PartModelTemplate("base");
    static {
        MODEL_BASE.add(new PartModelElementItemDisplayRenderer(Material.GRAY_CONCRETE_POWDER, new Vector3f(.875f, .5f, -.39f - .5f), new Vector3f(.25f, .9375f, .25f), 0f, 0f));
        MODEL_BASE.add(new PartModelElementItemDisplayRenderer(Material.GRAY_CONCRETE_POWDER, new Vector3f(-0.375f - 0.5f, .5f, -.39f - .5f), new Vector3f(.25f, .9375f, .25f), 0f, 0f));
        MODEL_BASE.add(new PartModelElementItemDisplayRenderer(Material.GRAY_CONCRETE_POWDER, new Vector3f(-0.39f - .5f, .5f, .875f), new Vector3f(.25f, .9375f, .25f), 0f, 0f));
        MODEL_BASE.add(new PartModelElementItemDisplayRenderer(Material.GRAY_CONCRETE_POWDER, new Vector3f(.875f, .5f, 1.35f - 0.5f), new Vector3f(.25f, .9375f, .25f), 0f, 0f));

        MODEL_BASE.add(new PartModelElementItemDisplayRenderer(Material.GRAY_CONCRETE_POWDER, new Vector3f(0f, 1.125f, 0f), new Vector3f(2.25f, .3125f, 2.25f), 0f, 0f));
        MODEL_BASE.add(new PartModelElementItemDisplayRenderer(Material.GRAY_CONCRETE_POWDER, new Vector3f(0f, 3.4375f, 0f), new Vector3f(2.25f, .125f, 2.25f), 0f, 0f));

        MODEL_BASE.add(new PartModelElementItemDisplayRenderer(Material.TINTED_GLASS, new Vector3f(0f, 2.3125f, -0.5f -.675f), new Vector3f(2.25f, 2.125f, 0.1f), 0f, 0f));
        MODEL_BASE.add(new PartModelElementItemDisplayRenderer(Material.TINTED_GLASS, new Vector3f(0f, 2.3125f, -0.5f + 1.675f), new Vector3f(2.25f, 2.125f, 0.1f), 0f, 0f));
        MODEL_BASE.add(new PartModelElementItemDisplayRenderer(Material.TINTED_GLASS, new Vector3f(1.175f, 2.3125f, 0f), new Vector3f(0.1f, 2.125f, 2.25f), 0f, 0f));
        MODEL_BASE.add(new PartModelElementItemDisplayRenderer(Material.TINTED_GLASS, new Vector3f(-0.5f - 0.675f, 2.3125f, 0f), new Vector3f(0.1f, 2.125f, 2.25f), 0f, 0f));

        MODEL_BASE.add(new PartModelElementItemDisplayRenderer(Material.POLISHED_TUFF_WALL, new Vector3f(0f, 3.6875f, 0f), new Vector3f(0.6875f, .375f, .75f), 90f, 0f));

        MODEL_BASE.add(new PartModelElementItemDisplayRenderer(Material.LIGHT_BLUE_TERRACOTTA, new Vector3f(0f, .6875f, -.145f - .5f), new Vector3f(.1875f, .5625f, .1875f), 0f, 0f));
        MODEL_BASE.add(new PartModelElementItemDisplayRenderer(Material.LIGHT_BLUE_TERRACOTTA, new Vector3f(0f, 0.5f, -.5625f - .5f), new Vector3f(.1875f, .1875f, .6875f), 0f, 0f));
    }

    static PartModelTemplate MODEL_INPUT_CONNECTOR = new PartModelTemplate("in_fluid");
    static {
        MODEL_INPUT_CONNECTOR.add(new PartModelElementItemDisplayRenderer(Material.GRAY_CONCRETE, new Vector3f(0f, 3.9375f, 0f), new Vector3f(.625f, .125f, .625f), 0f, 0f));
    }

    static PartModelTemplate MODEL_OUTPUT_CONNECTOR = new PartModelTemplate("out_fluid");
    static {
        MODEL_OUTPUT_CONNECTOR.add(new PartModelElementItemDisplayRenderer(Material.GRAY_CONCRETE, new Vector3f(0f, 0.5f, -0.9375f - 0.5f), new Vector3f(.625f, .625f, .125f), 0f, 0f));
    }

    static PartModelTemplate MODEL_LIQUID = new PartModelTemplate("liquid");
    static {
        MODEL_LIQUID.add(new PartModelElementItemDisplayRenderer(Material.BLUE_CONCRETE, new Vector3f(0f, 1.265f, 0f), new Vector3f(2.15f, 0f, 2.15f), 0f, 0f));
    }

    public static final ModelTemplate INITIAL_MODEL = new ModelTemplate();
    static {
        INITIAL_MODEL.add(MODEL_BASE);
        INITIAL_MODEL.add(MODEL_INPUT_CONNECTOR);
        INITIAL_MODEL.add(MODEL_OUTPUT_CONNECTOR);
        INITIAL_MODEL.add(MODEL_LIQUID);
    }

    public FluidTank() {
        super("torus:fluid_tank", FluidTankInstance.class);
    }

    @Override
    public ModelTemplate getInitialModel() {
        return INITIAL_MODEL;
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
