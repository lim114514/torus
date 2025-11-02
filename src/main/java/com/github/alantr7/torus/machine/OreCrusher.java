package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.item.ItemCriteria;
import com.github.alantr7.torus.world.Direction;
import com.github.alantr7.torus.math.ByteArrayBuilder;
import com.github.alantr7.torus.structure.Structure;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.structure.builder.StructureBodyDef;
import com.github.alantr7.torus.structure.builder.StructureComponentDef;
import com.github.alantr7.torus.structure.builder.StructureConnectorDef;
import com.github.alantr7.torus.structure.component.Connector;
import com.github.alantr7.torus.model.ModelPartItemDisplayRenderer;
import com.github.alantr7.torus.model.ModelTemplate;
import com.github.alantr7.torus.world.BlockLocation;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class OreCrusher extends Structure {

    static ModelTemplate MODEL_BASE = new ModelTemplate();
    static {
        MODEL_BASE.add(new ModelPartItemDisplayRenderer(Material.CYAN_TERRACOTTA, new Vector3f(0f, 1.5625f, 0f), new Vector3f(1f, 1.25f, 1f), 0f, 0f));
        MODEL_BASE.add(new ModelPartItemDisplayRenderer(Material.CYAN_TERRACOTTA, new Vector3f(0f, 0.8125f, 0f), new Vector3f(1.375f, .25f, 1.375f), 0f, 0f));
        MODEL_BASE.add(new ModelPartItemDisplayRenderer(Material.CYAN_TERRACOTTA, new Vector3f(1.0625f - .5f, .34f, -.0625f - .5f), new Vector3f(.25f, .75f, .25f), 0f, 0f));
        MODEL_BASE.add(new ModelPartItemDisplayRenderer(Material.CYAN_TERRACOTTA, new Vector3f(-.0625f - .5f, .34f, -.0625f - .5f), new Vector3f(.25f, .75f, .25f), 0f, 0f));
        MODEL_BASE.add(new ModelPartItemDisplayRenderer(Material.CYAN_TERRACOTTA, new Vector3f(-.0625f - .5f, .34f, 1.0625f - .5f), new Vector3f(.25f, .75f, .25f), 0f, 0f));
        MODEL_BASE.add(new ModelPartItemDisplayRenderer(Material.CYAN_TERRACOTTA, new Vector3f(1.0625f - .5f, .34f, 1.0625f - .5f), new Vector3f(.25f, .75f, .25f), 0f, 0f));

        // Hopper
        MODEL_BASE.add(new ModelPartItemDisplayRenderer(Material.SMOOTH_STONE, new Vector3f(0f, 2.75f, 0), new Vector3f(.8125f, .3125f, .8125f), 0f, 0f));
        MODEL_BASE.add(new ModelPartItemDisplayRenderer(Material.SMOOTH_STONE, new Vector3f(0f, 2.4375f, 0), new Vector3f(.4375f, .3125f, .4375f), 0f, 0f));
        MODEL_BASE.add(new ModelPartItemDisplayRenderer(Material.SMOOTH_STONE, new Vector3f(0f, 2.1875f, 0), new Vector3f(.25f, .3125f, .25f), 0f, 0f));

        MODEL_BASE.add(new ModelPartItemDisplayRenderer(Material.END_ROD, new Vector3f(0f, 1.5625f, -.125f - .5f), new Vector3f(1f, .75f, 1f), 0f, 90f));

        // Conveyor belt
        MODEL_BASE.add(new ModelPartItemDisplayRenderer(Material.CYAN_TERRACOTTA, new Vector3f(0f, .125f, .125f - .5f), new Vector3f(2.75f, .25f, .125f), 0f, 0f));
        MODEL_BASE.add(new ModelPartItemDisplayRenderer(Material.CYAN_TERRACOTTA, new Vector3f(0f, .125f, .875f - .5f), new Vector3f(2.75f, .25f, .125f), 0f, 0f));
        MODEL_BASE.add(new ModelPartItemDisplayRenderer(Material.GRAY_SHULKER_BOX, new Vector3f(0f, .125f, 0), new Vector3f(2.75f, .125f, .625f), 0f, 0f));

        // Motor belt
        MODEL_BASE.add(new ModelPartItemDisplayRenderer(Material.GRAY_CONCRETE, new Vector3f(-.475f - .5f, 1.941875f, -.375f - .5f), new Vector3f(.1875f, .0625f, 1.396f), 90f, 11.32f));
        MODEL_BASE.add(new ModelPartItemDisplayRenderer(Material.GRAY_CONCRETE, new Vector3f(-.4375f - .5f, 1.123125f, -.375f - .5f), new Vector3f(.1875f, .0625f, 1.38f), 90f, -6.01f));

        // Motor on the right
        MODEL_BASE.add(new ModelPartItemDisplayRenderer(Material.WEATHERED_COPPER, new Vector3f(-1.8125f, 1.5f, 0f), new Vector3f(.6875f, .6875f, 1.1875f), 0f, 0f));
        MODEL_BASE.add(new ModelPartItemDisplayRenderer(Material.CYAN_TERRACOTTA, new Vector3f(-1.8125f, .6875f, 0f), new Vector3f(.375f, .9375f, .4375f), 0f, 0f));
        MODEL_BASE.add(new ModelPartItemDisplayRenderer(Material.CYAN_TERRACOTTA, new Vector3f(-1.8125f, .125f, 0f), new Vector3f(1f, .3125f, 1.1875f), 0f, 0f));
        MODEL_BASE.add(new ModelPartItemDisplayRenderer(Material.END_ROD, new Vector3f(-1.3125f - .5f, 1.5f, -.125f - .5f), new Vector3f(.75f, .75f, .8125f), 0f, 90f));
        MODEL_BASE.add(new ModelPartItemDisplayRenderer(Material.POLISHED_TUFF, new Vector3f(-1.75f - .5f, 1.5f, 0f), new Vector3f(.25f, .375f, .4375f), 0f, 0f));
    }

    static ModelTemplate MODEL_ITEMS_IN = new ModelTemplate();
    static {
        MODEL_ITEMS_IN.add(new ModelPartItemDisplayRenderer(Material.GRAY_CONCRETE, new Vector3f(0f, 2.9375f, 0f), new Vector3f(0.625f, 0.125f, 0.625f), 0f, 0f));
    }

    static ModelTemplate MODEL_ITEMS_OUT = new ModelTemplate();
    static {
        MODEL_ITEMS_OUT.add(new ModelPartItemDisplayRenderer(Material.GRAY_CONCRETE, new Vector3f(1.4375f, .5f, 0f), new Vector3f(0.125f, 0.625f, 0.625f), 0f, 0f));
    }

    static ModelTemplate MODEL_ENERGY = new ModelTemplate();
    static {
        MODEL_ENERGY.add(new ModelPartItemDisplayRenderer(Material.GRAY_CONCRETE, new Vector3f(-1.9375f - 0.5f, 1.5f, 0f), new Vector3f(0.125f, 0.625f, 0.625f), 0f, 0f));
    }

    static ModelTemplate MODEL_WHEEL_LEFT = new ModelTemplate();
    static {
        MODEL_WHEEL_LEFT.add(new ModelPartItemDisplayRenderer(Material.STRUCTURE_VOID, new Vector3f(0f, 1.5625f, -.375f - .5f), new Vector3f(1.25f, 1.25f, 2.4375f), 0f, 0f));
    }

    static ModelTemplate MODEL_WHEEL_RIGHT = new ModelTemplate();
    static {
        MODEL_WHEEL_RIGHT.add(new ModelPartItemDisplayRenderer(Material.STRUCTURE_VOID, new Vector3f(-1.3125f - .5f, 1.5f, -.375f - .5f), new Vector3f(.75f, .75f, 2.4375f), 0f, 0f));
    }

    public static ItemCriteria INPUT_CRITERIA = new ItemCriteria();

    public static final int ENERGY_CONSUMPTION_PER_TICK = 300;

    public OreCrusher() {
        super("torus:ore_crusher", OreCrusherInstance.class);
        offset = new byte[]{ 0, 0, -1 };
    }

    @Override
    protected void createBounds(ByteArrayBuilder builder) {
        builder.add(0, 0, 0);
        builder.add(0, 1, 0);
        builder.add(0, 2, 0);
        builder.add(1, 0, 0);
        builder.add(-2, 0, 0);
        builder.add(-2, 1, 0);
        builder.add(-1, 0, 0);
        builder.add(0, 0, -1);
        builder.add(0, 1, -1);
    }

    @Override
    protected StructureInstance instantiate(@NotNull BlockLocation location, Direction direction) {
        direction = direction.getLeft();
        return new OreCrusherInstance(location, new StructureBodyDef(
          new StructureComponentDef[]{
            new StructureComponentDef("body", new Vector3f(), MODEL_BASE),
            new StructureComponentDef("power_connector", new Vector3f(-2, 1, 0), MODEL_ENERGY, new StructureConnectorDef(
              Connector.Matter.ENERGY, Connector.FlowDirection.IN, direction.getLeft().mask()
            )),
            new StructureComponentDef("item_connector", new Vector3f(0, 2, 0), MODEL_ITEMS_IN, new StructureConnectorDef(
              Connector.Matter.ITEM, Connector.FlowDirection.IN, Direction.UP.mask()
            )),
            new StructureComponentDef("out_connector", new Vector3f(1, 0, 0), MODEL_ITEMS_OUT, new StructureConnectorDef(
              Connector.Matter.ITEM, Connector.FlowDirection.OUT, direction.getRight().mask()
            )),
            new StructureComponentDef("wheel_left", new Vector3f(), MODEL_WHEEL_LEFT),
            new StructureComponentDef("wheel_right", new Vector3f(), MODEL_WHEEL_RIGHT)
          }
        ), direction);
    }
}
