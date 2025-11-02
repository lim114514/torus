package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.item.ItemCriteria;
import com.github.alantr7.torus.math.ByteArrayBuilder;
import com.github.alantr7.torus.model.ModelTemplate;
import com.github.alantr7.torus.world.Direction;
import com.github.alantr7.torus.structure.Structure;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.structure.builder.StructureBodyDef;
import com.github.alantr7.torus.structure.builder.StructureComponentDef;
import com.github.alantr7.torus.structure.builder.StructureConnectorDef;
import com.github.alantr7.torus.structure.component.Connector;
import com.github.alantr7.torus.model.PartModelElementItemDisplayRenderer;
import com.github.alantr7.torus.model.PartModelTemplate;
import com.github.alantr7.torus.world.BlockLocation;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class OreWasher extends Structure {

    static PartModelTemplate MODEL = new PartModelTemplate("base");
    static {
        MODEL.add(new PartModelElementItemDisplayRenderer(Material.CYAN_TERRACOTTA, new Vector3f(0, 0.5f, .0625f - 0.5f), new Vector3f(1, 1, 0.125f), 0, 0));
        MODEL.add(new PartModelElementItemDisplayRenderer(Material.CYAN_TERRACOTTA, new Vector3f(0, 0.5f, .9375f - 0.5f), new Vector3f(1, 1, 0.125f), 0, 0));
        MODEL.add(new PartModelElementItemDisplayRenderer(Material.CYAN_TERRACOTTA, new Vector3f(.4375f, 0.5f, 0f), new Vector3f(.125f, 1, 0.75f), 0, 0));
        MODEL.add(new PartModelElementItemDisplayRenderer(Material.CYAN_TERRACOTTA, new Vector3f(.0625f - .5f, 0.5f, 0f), new Vector3f(.125f, 1, 0.75f), 0, 0));
        MODEL.add(new PartModelElementItemDisplayRenderer(Material.CYAN_TERRACOTTA, new Vector3f(0f, 0.125f, .5f), new Vector3f(.75f, .25f, 1.75f), 0, 0));
        MODEL.add(new PartModelElementItemDisplayRenderer(Material.WEATHERED_COPPER, new Vector3f(.375f - .5f, 0.4375f, .875f), new Vector3f(.375f, .375f, .75f), 0, 0));

        // Hopper
        MODEL.add(new PartModelElementItemDisplayRenderer(Material.SMOOTH_STONE, new Vector3f(0f, 1.75f, 0f), new Vector3f(1f, .1875f, 1f), 0, 0));
        MODEL.add(new PartModelElementItemDisplayRenderer(Material.SMOOTH_STONE, new Vector3f(0f, 1.5625f, 0f), new Vector3f(.5625f, .1875f, .5625f), 0, 0));
        MODEL.add(new PartModelElementItemDisplayRenderer(Material.SMOOTH_STONE, new Vector3f(0f, 1.375f, 0f), new Vector3f(.3125f, .1875f, .3125f), 0, 0));

        // Saws
        MODEL.add(new PartModelElementItemDisplayRenderer(Material.STONECUTTER, new Vector3f(0f, .625f, .25f - .5f), new Vector3f(.8125f, .875f, .25f), 0, 0));
        MODEL.add(new PartModelElementItemDisplayRenderer(Material.STONECUTTER, new Vector3f(0f, .625f, .40875f - .5f), new Vector3f(.8125f, .875f, .25f), 0, 0));
        MODEL.add(new PartModelElementItemDisplayRenderer(Material.STONECUTTER, new Vector3f(0f, .625f, .06625f), new Vector3f(.8125f, .875f, .25f), 0, 0));
        MODEL.add(new PartModelElementItemDisplayRenderer(Material.STONECUTTER, new Vector3f(0f, .625f, .25f), new Vector3f(.8125f, .875f, .25f), 0, 0));
    }

    static PartModelTemplate MODEL_CONNECTOR_ITEMS_IN = new PartModelTemplate("in_item");
    static {
        MODEL_CONNECTOR_ITEMS_IN.add(new PartModelElementItemDisplayRenderer(Material.GRAY_CONCRETE, new Vector3f(0f, 1.875f, 0f), new Vector3f(0.625f, 0.25f, 0.625f), 0, 0));
    }

    static PartModelTemplate MODEL_CONNECTOR_POWER = new PartModelTemplate("in_energy");
    static {
        MODEL_CONNECTOR_POWER.add(new PartModelElementItemDisplayRenderer(Material.GRAY_WOOL, new Vector3f(.375f - .5f, 0.5f, 1.3125f), new Vector3f(.125f, .125f, .1875f), 0, 0));
        MODEL_CONNECTOR_POWER.add(new PartModelElementItemDisplayRenderer(Material.GRAY_CONCRETE, new Vector3f(0, 0.5f, 1.4375f), new Vector3f(0.625f, 0.625f, 0.125f), 0, 0));
    }

    static PartModelTemplate MODEL_WATER_IN = new PartModelTemplate("in_fluid");
    static {
        MODEL_WATER_IN.add(new PartModelElementItemDisplayRenderer(Material.GRAY_CONCRETE, new Vector3f(0, 0.9375f, 1f), new Vector3f(0.625f, .125f, .625f), 0, 0));
        MODEL_WATER_IN.add(new PartModelElementItemDisplayRenderer(Material.LIGHT_BLUE_TERRACOTTA, new Vector3f(.1875f, .5f, 1.1875f - .5f), new Vector3f(.125f, 0.125f, 0.5f), 0, 0));
        MODEL_WATER_IN.add(new PartModelElementItemDisplayRenderer(Material.LIGHT_BLUE_TERRACOTTA, new Vector3f(.1875f, .6875f, 1f), new Vector3f(0.125f, .5f, .125f), 0, 0));
    }


    static PartModelTemplate MODEL_CONNECTOR_ITEMS_OUT = new PartModelTemplate("out_item");
    static {
        MODEL_CONNECTOR_ITEMS_OUT.add(new PartModelElementItemDisplayRenderer(Material.GRAY_CONCRETE, new Vector3f(0, 0.5f, -0.0625f - 0.5f), new Vector3f(0.625f, 0.625f, 0.125f), 0, 0));
    }

    public static final ModelTemplate INITIAL_MODEL = new ModelTemplate();
    static {
        INITIAL_MODEL.add(MODEL);
        INITIAL_MODEL.add(MODEL_CONNECTOR_ITEMS_IN);
        INITIAL_MODEL.add(MODEL_CONNECTOR_ITEMS_OUT);
        INITIAL_MODEL.add(MODEL_CONNECTOR_POWER);
        INITIAL_MODEL.add(MODEL_WATER_IN);
    }

    public static final int ENERGY_CONSUMPTION_PER_TICK = 300;

    public static ItemCriteria INPUT_CRITERIA = new ItemCriteria();

    public OreWasher() {
        super("torus:ore_washer", OreWasherInstance.class);
    }

    @Override
    public ModelTemplate getInitialModel() {
        return INITIAL_MODEL;
    }

    @Override
    protected void createBounds(ByteArrayBuilder builder) {
        builder.add(0, 0, 0);
        builder.add(0, 0, 1);
        builder.add(0, 1, 0);
    }

    @Override
    protected StructureInstance instantiate(@NotNull BlockLocation location, Direction direction) {
        return new OreWasherInstance(location, new StructureBodyDef(
          new StructureComponentDef[] {
            new StructureComponentDef("body", new Vector3f(), MODEL),
            new StructureComponentDef("item_connector", new Vector3f(0f, 1f, 0f), MODEL_CONNECTOR_ITEMS_IN, new StructureConnectorDef(
              Connector.Matter.ITEM, Connector.FlowDirection.IN, Direction.UP.mask()
            )),
            new StructureComponentDef("out_connector", new Vector3f(0f, 0, 0f), MODEL_CONNECTOR_ITEMS_OUT, new StructureConnectorDef(
              Connector.Matter.ITEM, Connector.FlowDirection.OUT, direction.mask()
            )),
            new StructureComponentDef("power_connector", new Vector3f(0f, 0, 1f), MODEL_CONNECTOR_POWER, new StructureConnectorDef(
              Connector.Matter.ENERGY, Connector.FlowDirection.IN, direction.getOpposite().mask()
            )),
            new StructureComponentDef("fluid_connector", new Vector3f(0f, 0, 1f), MODEL_WATER_IN, new StructureConnectorDef(
              Connector.Matter.FLUID, Connector.FlowDirection.IN, Direction.UP.mask()
            ))
          }
        ), direction);
    }
}
