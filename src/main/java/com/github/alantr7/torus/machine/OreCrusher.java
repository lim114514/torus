package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.item.ItemCriteria;
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
import com.github.alantr7.torus.world.BlockLocation;
import org.bukkit.Material;
import org.bukkit.entity.ItemDisplay;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class OreCrusher extends Structure {

    static ModelTemplate MODEL_BASE = new ModelTemplate();
    static {
        MODEL_BASE.add(new ItemDisplayModelTemplate(Material.CYAN_TERRACOTTA, ItemDisplay.ItemDisplayTransform.NONE, 0, new Vector3f(0f, 1.5625f, 0f), new Vector3f(1f, 1.25f, 1f), 0f, 0f));
        MODEL_BASE.add(new ItemDisplayModelTemplate(Material.CYAN_TERRACOTTA, ItemDisplay.ItemDisplayTransform.NONE, 0, new Vector3f(0f, 0.8125f, 0f), new Vector3f(1.375f, .25f, 1.375f), 0f, 0f));
        MODEL_BASE.add(new ItemDisplayModelTemplate(Material.CYAN_TERRACOTTA, ItemDisplay.ItemDisplayTransform.NONE, 0, new Vector3f(1.0625f - .5f, .34f, -.0625f - .5f), new Vector3f(.25f, .75f, .25f), 0f, 0f));
        MODEL_BASE.add(new ItemDisplayModelTemplate(Material.CYAN_TERRACOTTA, ItemDisplay.ItemDisplayTransform.NONE, 0, new Vector3f(-.0625f - .5f, .34f, -.0625f - .5f), new Vector3f(.25f, .75f, .25f), 0f, 0f));
        MODEL_BASE.add(new ItemDisplayModelTemplate(Material.CYAN_TERRACOTTA, ItemDisplay.ItemDisplayTransform.NONE, 0, new Vector3f(-.0625f - .5f, .34f, 1.0625f - .5f), new Vector3f(.25f, .75f, .25f), 0f, 0f));
        MODEL_BASE.add(new ItemDisplayModelTemplate(Material.CYAN_TERRACOTTA, ItemDisplay.ItemDisplayTransform.NONE, 0, new Vector3f(1.0625f - .5f, .34f, 1.0625f - .5f), new Vector3f(.25f, .75f, .25f), 0f, 0f));

        // Hopper
        MODEL_BASE.add(new ItemDisplayModelTemplate(Material.SMOOTH_STONE, ItemDisplay.ItemDisplayTransform.NONE, 0, new Vector3f(0f, 2.75f, 0), new Vector3f(.8125f, .3125f, .8125f), 0f, 0f));
        MODEL_BASE.add(new ItemDisplayModelTemplate(Material.SMOOTH_STONE, ItemDisplay.ItemDisplayTransform.NONE, 0, new Vector3f(0f, 2.4375f, 0), new Vector3f(.4375f, .3125f, .4375f), 0f, 0f));
        MODEL_BASE.add(new ItemDisplayModelTemplate(Material.SMOOTH_STONE, ItemDisplay.ItemDisplayTransform.NONE, 0, new Vector3f(0f, 2.1875f, 0), new Vector3f(.25f, .3125f, .25f), 0f, 0f));

        MODEL_BASE.add(new ItemDisplayModelTemplate(Material.END_ROD, ItemDisplay.ItemDisplayTransform.NONE, 0, new Vector3f(0f, 1.5625f, -.125f - .5f), new Vector3f(1f, .75f, 1f), 0f, 90f));

        // Conveyor belt
        MODEL_BASE.add(new ItemDisplayModelTemplate(Material.CYAN_TERRACOTTA, ItemDisplay.ItemDisplayTransform.NONE, 0, new Vector3f(0f, .125f, .125f - .5f), new Vector3f(2.75f, .25f, .125f), 0f, 0f));
        MODEL_BASE.add(new ItemDisplayModelTemplate(Material.CYAN_TERRACOTTA, ItemDisplay.ItemDisplayTransform.NONE, 0, new Vector3f(0f, .125f, .875f - .5f), new Vector3f(2.75f, .25f, .125f), 0f, 0f));
        MODEL_BASE.add(new ItemDisplayModelTemplate(Material.GRAY_SHULKER_BOX, ItemDisplay.ItemDisplayTransform.NONE, 0, new Vector3f(0f, .125f, 0), new Vector3f(2.75f, .125f, .625f), 0f, 0f));

        // Motor belt
        MODEL_BASE.add(new ItemDisplayModelTemplate(Material.GRAY_CONCRETE, ItemDisplay.ItemDisplayTransform.NONE, 0, new Vector3f(-.475f - .5f, 1.941875f, -.375f - .5f), new Vector3f(.1875f, .0625f, 1.396f), 90f, 11.32f));
        MODEL_BASE.add(new ItemDisplayModelTemplate(Material.GRAY_CONCRETE, ItemDisplay.ItemDisplayTransform.NONE, 0, new Vector3f(-.4375f - .5f, 1.123125f, -.375f - .5f), new Vector3f(.1875f, .0625f, 1.38f), 90f, -6.01f));

        // Motor on the right
        MODEL_BASE.add(new ItemDisplayModelTemplate(Material.WEATHERED_COPPER, ItemDisplay.ItemDisplayTransform.NONE, 0, new Vector3f(-1.8125f, 1.5f, 0f), new Vector3f(.6875f, .6875f, 1.1875f), 0f, 0f));
        MODEL_BASE.add(new ItemDisplayModelTemplate(Material.CYAN_TERRACOTTA, ItemDisplay.ItemDisplayTransform.NONE, 0, new Vector3f(-1.8125f, .6875f, 0f), new Vector3f(.375f, .9375f, .4375f), 0f, 0f));
        MODEL_BASE.add(new ItemDisplayModelTemplate(Material.CYAN_TERRACOTTA, ItemDisplay.ItemDisplayTransform.NONE, 0, new Vector3f(-1.8125f, .125f, 0f), new Vector3f(1f, .3125f, 1.1875f), 0f, 0f));
        MODEL_BASE.add(new ItemDisplayModelTemplate(Material.END_ROD, ItemDisplay.ItemDisplayTransform.NONE, 0, new Vector3f(-1.3125f - .5f, 1.5f, -.125f - .5f), new Vector3f(.75f, .75f, .8125f), 0f, 90f));
        MODEL_BASE.add(new ItemDisplayModelTemplate(Material.POLISHED_TUFF, ItemDisplay.ItemDisplayTransform.NONE, 0, new Vector3f(-1.75f - .5f, 1.5f, 0f), new Vector3f(.25f, .375f, .4375f), 0f, 0f));
    }

    static ModelTemplate MODEL_ITEMS_IN = new ModelTemplate();
    static {
        MODEL_ITEMS_IN.add(new ItemDisplayModelTemplate(Material.GRAY_CONCRETE, ItemDisplay.ItemDisplayTransform.NONE, 0, new Vector3f(0f, 2.9375f, 0f), new Vector3f(0.625f, 0.125f, 0.625f), 0f, 0f));
    }

    static ModelTemplate MODEL_ITEMS_OUT = new ModelTemplate();
    static {
        MODEL_ITEMS_OUT.add(new ItemDisplayModelTemplate(Material.GRAY_CONCRETE, ItemDisplay.ItemDisplayTransform.NONE, 0, new Vector3f(1.4375f, .5f, 0f), new Vector3f(0.125f, 0.625f, 0.625f), 0f, 0f));
    }

    static ModelTemplate MODEL_ENERGY = new ModelTemplate();
    static {
        MODEL_ENERGY.add(new ItemDisplayModelTemplate(Material.GRAY_CONCRETE, ItemDisplay.ItemDisplayTransform.NONE, 0, new Vector3f(-1.9375f - 0.5f, 1.5f, 0f), new Vector3f(0.125f, 0.625f, 0.625f), 0f, 0f));
    }

    static ModelTemplate MODEL_WHEEL_LEFT = new ModelTemplate();
    static {
        MODEL_WHEEL_LEFT.add(new ItemDisplayModelTemplate(Material.STRUCTURE_VOID, ItemDisplay.ItemDisplayTransform.NONE, 0, new Vector3f(0f, 1.5625f, -.375f - .5f), new Vector3f(1.25f, 1.25f, 2.4375f), 0f, 0f));
    }

    static ModelTemplate MODEL_WHEEL_RIGHT = new ModelTemplate();
    static {
        MODEL_WHEEL_RIGHT.add(new ItemDisplayModelTemplate(Material.STRUCTURE_VOID, ItemDisplay.ItemDisplayTransform.NONE, 0, new Vector3f(-1.3125f - .5f, 1.5f, -.375f - .5f), new Vector3f(.75f, .75f, 2.4375f), 0f, 0f));
    }

    public static final ItemCriteria INPUT_CRITERIA = new ItemCriteria();
    static {
        INPUT_CRITERIA.materials.add(Material.COBBLESTONE);
        INPUT_CRITERIA.materials.add(Material.IRON_ORE);
    }

    public static final int ENERGY_CONSUMPTION_PER_TICK = 2_500;

    public OreCrusher() {
        super("torus:ore_crusher", OreCrusherInstance.class);
    }

    @Override
    protected void createBounds(IntArrayBuilder builder) {
        builder.add(0, 0, 0);
        builder.add(0, 1, 0);
        builder.add(0, 2, 0);
        builder.add(1, 0, 0);
        builder.add(-1, 0, 0);
        builder.add(-2, 0, 0);
        builder.add(-2, 1, 0);
    }

    @Override
    protected StructureInstance instantiate(@NotNull BlockLocation location, Direction direction) {
        StructureComponentDef body = new StructureComponentDef("body", new Vector3f(), MODEL_BASE.build(location.getBlock().getLocation().add(.5, 0, .5), direction));
        StructureComponentDef energyIn = new StructureComponentDef("power_connector", new Vector3f(-2, 1, 0), MODEL_ENERGY.build(location.getBlock().getLocation().add(.5, 0, .5), direction));
        StructureComponentDef itemsIn = new StructureComponentDef("item_connector", new Vector3f(0, 2, 0), MODEL_ITEMS_IN.build(location.getBlock().getLocation().add(.5, 0, .5), direction));
        StructureComponentDef itemsOut = new StructureComponentDef("out_connector", new Vector3f(1, 0, 0), MODEL_ITEMS_OUT.build(location.getBlock().getLocation().add(.5, 0, .5), direction));
        StructureComponentDef wheelLeft = new StructureComponentDef("wheel_left", new Vector3f(), MODEL_WHEEL_LEFT.build(location.getBlock().getLocation().add(.5, 0, .5), direction));
        StructureComponentDef wheelRight = new StructureComponentDef("wheel_right", new Vector3f(), MODEL_WHEEL_RIGHT.build(location.getBlock().getLocation().add(.5, 0, .5), direction));

        return new OreCrusherInstance(location, new StructureBodyDef(
          new StructureComponentDef[]{ body, energyIn, itemsIn, itemsOut, wheelLeft, wheelRight },
          new StructureConnectorDef[]{
            new StructureConnectorDef("power_connector", Connector.Matter.ENERGY, Connector.FlowDirection.IN, direction.getLeft().mask()),
            new StructureConnectorDef("item_connector", Connector.Matter.ITEM, Connector.FlowDirection.IN, Direction.UP.mask()),
            new StructureConnectorDef("out_connector", Connector.Matter.ITEM, Connector.FlowDirection.OUT, direction.getRight().mask()),
          }
        ), direction);
    }
}
