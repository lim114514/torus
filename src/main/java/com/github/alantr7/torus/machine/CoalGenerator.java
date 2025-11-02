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

public class CoalGenerator extends Structure {

    static PartModelTemplate MODEL = new PartModelTemplate("base");
    static {
        MODEL.add(new PartModelElementItemDisplayRenderer(Material.CYAN_TERRACOTTA, new Vector3f(0, .0625f, 1f), new Vector3f(1.75f, .125f, 2.6875f), 0f, 0f));
        MODEL.add(new PartModelElementItemDisplayRenderer(Material.CYAN_TERRACOTTA, new Vector3f(0, 1.1875f, 2.125f - .5f), new Vector3f(.625f, .125f, .625f), 0f, 0f));
        MODEL.add(new PartModelElementItemDisplayRenderer(Material.CYAN_TERRACOTTA, new Vector3f(0, .9375f, .9375f - .5f), new Vector3f(.375f, .1875f, 1.1875f), 0f, 0f));
        MODEL.add(new PartModelElementItemDisplayRenderer(Material.CYAN_TERRACOTTA, new Vector3f(0, .5f, 1f), new Vector3f(.25f, .25f, 2.75f), 0f, 0f));
        MODEL.add(new PartModelElementItemDisplayRenderer(Material.CYAN_TERRACOTTA, new Vector3f(0, .5f, .375f - .5f), new Vector3f(.6875f, .8125f, .125f), 0f, 0f));
        MODEL.add(new PartModelElementItemDisplayRenderer(Material.CYAN_TERRACOTTA, new Vector3f(0, .5f, .75f - .5f), new Vector3f(.6875f, .8125f, .125f), 0f, 0f));
        MODEL.add(new PartModelElementItemDisplayRenderer(Material.CYAN_TERRACOTTA, new Vector3f(0, .5f, 1.125f - .5f), new Vector3f(.6875f, .8125f, .125f), 0f, 0f));
        MODEL.add(new PartModelElementItemDisplayRenderer(Material.CYAN_TERRACOTTA, new Vector3f(0, .5f, 1.5f - .5f), new Vector3f(.6875f, .8125f, .125f), 0f, 0f));
        MODEL.add(new PartModelElementItemDisplayRenderer(Material.BLAST_FURNACE, new Vector3f(0, .625f, 2.125f - .5f), new Vector3f(1f, 1f, 1f), 0f, 0f));
        MODEL.add(new PartModelElementItemDisplayRenderer(Material.GRAY_CONCRETE, new Vector3f(0, .4375f, .9375f - .5f), new Vector3f(.625f, .8125f, 1.375f), 0f, 0f));
        MODEL.add(new PartModelElementItemDisplayRenderer(Material.LIGHTNING_ROD, new Vector3f(0, 1.0625f, .0625f), new Vector3f(.5f, .25f, .5f), 0f, 0f));
        MODEL.add(new PartModelElementItemDisplayRenderer(Material.LIGHTNING_ROD, new Vector3f(0, 1.0625f, .4375f), new Vector3f(.5f, .25f, .5f), 0f, 0f));
        MODEL.add(new PartModelElementItemDisplayRenderer(Material.LIGHTNING_ROD, new Vector3f(0, 1.0625f, .8125f), new Vector3f(.5f, .25f, .5f), 0f, 0f));

        MODEL.add(new PartModelElementItemDisplayRenderer(Material.END_ROD, new Vector3f(-.125f - .5f, .375f, .0625f), new Vector3f(1f, .5f, 1f), 0f, 0f));
        MODEL.add(new PartModelElementItemDisplayRenderer(Material.END_ROD, new Vector3f(.125f - .5f, .625f, .0625f), new Vector3f(1f, .5f, 1f), 0f, 90f));

        MODEL.add(new PartModelElementItemDisplayRenderer(Material.END_ROD, new Vector3f(-.125f - .5f, .375f, .4375f), new Vector3f(1f, .5f, 1f), 0f, 0f));
        MODEL.add(new PartModelElementItemDisplayRenderer(Material.END_ROD, new Vector3f(.125f - .5f, .625f, .4375f), new Vector3f(1f, .5f, 1f), 0f, 90f));

        MODEL.add(new PartModelElementItemDisplayRenderer(Material.END_ROD, new Vector3f(-.125f - .5f, .375f, .8125f), new Vector3f(1f, .5f, 1f), 0f, 0f));
        MODEL.add(new PartModelElementItemDisplayRenderer(Material.END_ROD, new Vector3f(.125f - .5f, .625f, .8125f), new Vector3f(1f, .5f, 1f), 0f, 90f));

        // Chimney
        MODEL.add(new PartModelElementItemDisplayRenderer(Material.CYAN_TERRACOTTA, new Vector3f(1.125f - .5f, .875f, .8125f - .5f), new Vector3f(.3125f, 1.5f, .3125f), 0f, 0f));
        MODEL.add(new PartModelElementItemDisplayRenderer(Material.CYAN_TERRACOTTA, new Vector3f(1.125f - .5f, 2.25f, .8125f - .5f), new Vector3f(.25f, 1.25f, .25f), 0f, 0f));
    }

    static PartModelTemplate MODEL_INPUT_CONNECTOR = new PartModelTemplate("in_item");
    static {
        MODEL_INPUT_CONNECTOR.add(new PartModelElementItemDisplayRenderer(Material.GRAY_CONCRETE, new Vector3f(0f, .5f, .0625f - .5f), new Vector3f(.625f, .625f, .125f), 0f, 0f));
    }

    static PartModelTemplate MODEL_OUTPUT_CONNECTOR = new PartModelTemplate("out_energy");
    static {
        MODEL_OUTPUT_CONNECTOR.add(new PartModelElementItemDisplayRenderer(Material.GRAY_CONCRETE, new Vector3f(0f, .5f, 2.4375f), new Vector3f(.625f, .625f, .125f), 0f, 0f));
    }

    public static final ItemCriteria INPUT_CRITERIA = new ItemCriteria();
    static {
        INPUT_CRITERIA.materials.add(Material.COAL);
        INPUT_CRITERIA.materials.add(Material.CHARCOAL);
    }

    public static final ModelTemplate INITIAL_MODEL = new ModelTemplate();
    static {
        INITIAL_MODEL.add(MODEL);
        INITIAL_MODEL.add(MODEL_INPUT_CONNECTOR);
        INITIAL_MODEL.add(MODEL_OUTPUT_CONNECTOR);
    }

    public CoalGenerator() {
        super("torus:coal_generator", CoalGeneratorInstance.class);
    }

    @Override
    public ModelTemplate getInitialModel() {
        return INITIAL_MODEL;
    }

    @Override
    public void createBounds(ByteArrayBuilder builder) {
        for (int z = 0; z <= 2; z++) {
            builder.add(0, 0, z);
        }
        builder.add(1, 0, 0);
        builder.add(1, 1, 0);
        builder.add(1, 2, 0);
    }

    @Override
    protected StructureInstance instantiate(@NotNull BlockLocation location, Direction direction) {
        return new CoalGeneratorInstance(location, new StructureBodyDef(new StructureComponentDef[]{
          new StructureComponentDef("base", new Vector3f(), MODEL),
          new StructureComponentDef(
            "item_connector", new Vector3f(0, 0, 2), MODEL_INPUT_CONNECTOR, new StructureConnectorDef(Connector.Matter.ITEM, Connector.FlowDirection.IN, direction.getOpposite().mask())
          ),
          new StructureComponentDef(
            "power_connector", new Vector3f(0, 0, 0), MODEL_OUTPUT_CONNECTOR, new StructureConnectorDef(Connector.Matter.ENERGY, Connector.FlowDirection.OUT, direction.mask())
          )
        }), direction);
    }

}
