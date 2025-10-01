package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.world.BlockLocation;
import com.github.alantr7.torus.math.Direction;
import com.github.alantr7.torus.math.IntArrayBuilder;
import com.github.alantr7.torus.structure.Structure;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.structure.builder.StructureBodyDef;
import com.github.alantr7.torus.structure.builder.StructureComponentDef;
import com.github.alantr7.torus.structure.builder.StructureConnectorDef;
import com.github.alantr7.torus.structure.component.Connector;
import com.github.alantr7.torus.structure.display.ItemDisplayModelTemplate;
import com.github.alantr7.torus.structure.display.ModelTemplate;
import org.bukkit.Material;
import org.bukkit.entity.ItemDisplay;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class FluidTank extends Structure {

    static ModelTemplate MODEL_BASE = new ModelTemplate();
    static {
        MODEL_BASE.add(new ItemDisplayModelTemplate(Material.GRAY_CONCRETE_POWDER, ItemDisplay.ItemDisplayTransform.NONE, 0, new Vector3f(.875f, .5f, -.39f - .5f), new Vector3f(.25f, .9375f, .25f), 0f, 0f));
        MODEL_BASE.add(new ItemDisplayModelTemplate(Material.GRAY_CONCRETE_POWDER, ItemDisplay.ItemDisplayTransform.NONE, 0, new Vector3f(-0.375f - 0.5f, .5f, -.39f - .5f), new Vector3f(.25f, .9375f, .25f), 0f, 0f));
        MODEL_BASE.add(new ItemDisplayModelTemplate(Material.GRAY_CONCRETE_POWDER, ItemDisplay.ItemDisplayTransform.NONE, 0, new Vector3f(-0.39f - .5f, .5f, .875f), new Vector3f(.25f, .9375f, .25f), 0f, 0f));
        MODEL_BASE.add(new ItemDisplayModelTemplate(Material.GRAY_CONCRETE_POWDER, ItemDisplay.ItemDisplayTransform.NONE, 0, new Vector3f(.875f, .5f, 1.35f - 0.5f), new Vector3f(.25f, .9375f, .25f), 0f, 0f));

        MODEL_BASE.add(new ItemDisplayModelTemplate(Material.GRAY_CONCRETE_POWDER, ItemDisplay.ItemDisplayTransform.NONE, 0, new Vector3f(0f, 1.125f, 0f), new Vector3f(2.25f, .3125f, 2.25f), 0f, 0f));
        MODEL_BASE.add(new ItemDisplayModelTemplate(Material.GRAY_CONCRETE_POWDER, ItemDisplay.ItemDisplayTransform.NONE, 0, new Vector3f(0f, 3.4375f, 0f), new Vector3f(2.25f, .125f, 2.25f), 0f, 0f));

        MODEL_BASE.add(new ItemDisplayModelTemplate(Material.TINTED_GLASS, ItemDisplay.ItemDisplayTransform.NONE, 0, new Vector3f(0f, 2.3125f, -0.5f -.675f), new Vector3f(2.25f, 2.125f, 0.1f), 0f, 0f));
        MODEL_BASE.add(new ItemDisplayModelTemplate(Material.TINTED_GLASS, ItemDisplay.ItemDisplayTransform.NONE, 0, new Vector3f(0f, 2.3125f, -0.5f + 1.675f), new Vector3f(2.25f, 2.125f, 0.1f), 0f, 0f));
        MODEL_BASE.add(new ItemDisplayModelTemplate(Material.TINTED_GLASS, ItemDisplay.ItemDisplayTransform.NONE, 0, new Vector3f(1.175f, 2.3125f, 0f), new Vector3f(0.1f, 2.125f, 2.25f), 0f, 0f));
        MODEL_BASE.add(new ItemDisplayModelTemplate(Material.TINTED_GLASS, ItemDisplay.ItemDisplayTransform.NONE, 0, new Vector3f(-0.5f - 0.675f, 2.3125f, 0f), new Vector3f(0.1f, 2.125f, 2.25f), 0f, 0f));

        MODEL_BASE.add(new ItemDisplayModelTemplate(Material.POLISHED_TUFF_WALL, ItemDisplay.ItemDisplayTransform.NONE, 0, new Vector3f(0f, 3.6875f, 0f), new Vector3f(0.6875f, .375f, .75f), 90f, 0f));

        MODEL_BASE.add(new ItemDisplayModelTemplate(Material.LIGHT_BLUE_TERRACOTTA, ItemDisplay.ItemDisplayTransform.NONE, 0, new Vector3f(0f, .6875f, -.145f - .5f), new Vector3f(.1875f, .5625f, .1875f), 0f, 0f));
        MODEL_BASE.add(new ItemDisplayModelTemplate(Material.LIGHT_BLUE_TERRACOTTA, ItemDisplay.ItemDisplayTransform.NONE, 0, new Vector3f(0f, 0.5f, -.5625f - .5f), new Vector3f(.1875f, .1875f, .6875f), 0f, 0f));
    }

    static ModelTemplate MODEL_INPUT_CONNECTOR = new ModelTemplate();
    static {
        MODEL_INPUT_CONNECTOR.add(new ItemDisplayModelTemplate(Material.GRAY_CONCRETE, ItemDisplay.ItemDisplayTransform.NONE, 0, new Vector3f(0f, 3.9375f, 0f), new Vector3f(.625f, .125f, .625f), 0f, 0f));
    }

    static ModelTemplate MODEL_OUTPUT_CONNECTOR = new ModelTemplate();
    static {
        MODEL_OUTPUT_CONNECTOR.add(new ItemDisplayModelTemplate(Material.GRAY_CONCRETE, ItemDisplay.ItemDisplayTransform.NONE, 0, new Vector3f(0f, 0.5f, -0.9375f - 0.5f), new Vector3f(.625f, .625f, .125f), 0f, 0f));
    }

    static ModelTemplate MODEL_LIQUID = new ModelTemplate();
    static {
        MODEL_LIQUID.add(new ItemDisplayModelTemplate(Material.BLUE_CONCRETE, ItemDisplay.ItemDisplayTransform.NONE, 0, new Vector3f(0f, 1.265f, 0f), new Vector3f(2.15f, 0f, 2.15f), 0f, 0f));
    }


    public FluidTank() {
        super("torus:fluid_tank", FluidTankInstance.class);
    }

    @Override
    protected void createBounds(IntArrayBuilder builder) {
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
            new StructureComponentDef("base", new Vector3f(), MODEL_BASE.build(location.getBlock().getLocation().add(.5f, 0, .5f), direction)),
            new StructureComponentDef("input", new Vector3f(0f, 3f, 0f), MODEL_INPUT_CONNECTOR.build(location.getBlock().getLocation().add(.5f, 0, .5f), direction)),
            new StructureComponentDef("output", new Vector3f(0f, 0f, -1f), MODEL_OUTPUT_CONNECTOR.build(location.getBlock().getLocation().add(.5f, 0, .5f), direction)),
            new StructureComponentDef("liquid", new Vector3f(0f, 0f, -1f), MODEL_LIQUID.build(location.getBlock().getLocation().add(.5f, 0, .5f), direction)),
          },
          new StructureConnectorDef[]{
            new StructureConnectorDef("input", Connector.Matter.FLUID, Connector.FlowDirection.IN, Direction.UP.mask()),
            new StructureConnectorDef("output", Connector.Matter.FLUID, Connector.FlowDirection.OUT, direction.mask()),
          }
        ), direction);
    }

}
