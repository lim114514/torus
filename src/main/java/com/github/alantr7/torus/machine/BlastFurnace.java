package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.math.ByteArrayBuilder;
import com.github.alantr7.torus.world.Direction;
import com.github.alantr7.torus.structure.Structure;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.structure.builder.StructureBodyDef;
import com.github.alantr7.torus.structure.builder.StructureComponentDef;
import com.github.alantr7.torus.structure.builder.StructureConnectorDef;
import com.github.alantr7.torus.structure.component.Connector;
import com.github.alantr7.torus.structure.display.ModelPartItemDisplayRenderer;
import com.github.alantr7.torus.structure.display.ModelTemplate;
import com.github.alantr7.torus.world.BlockLocation;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class BlastFurnace extends Structure {

    static final ModelTemplate MODEL_BASE = new ModelTemplate();
    static {
        MODEL_BASE.add(new ModelPartItemDisplayRenderer(Material.NETHER_BRICKS, new Vector3f(0f, 0.9375f, 1f), new Vector3f(1.625f, 1.8125f, 1.625f), 0f, 0f));
        MODEL_BASE.add(new ModelPartItemDisplayRenderer(Material.NETHER_BRICKS, new Vector3f(0f, 2.6875f, 1.5f), new Vector3f(.9375f, 1.6875f, .25f), 0f, 0f));
        MODEL_BASE.add(new ModelPartItemDisplayRenderer(Material.NETHER_BRICKS, new Vector3f(0f, 3.375f, 1.375f - .5f), new Vector3f(.9375f, .3125f, 1f), 0f, 0f));
        MODEL_BASE.add(new ModelPartItemDisplayRenderer(Material.NETHER_BRICKS, new Vector3f(0f, 1.9375f, .9375f - .5f), new Vector3f(1f, .375f, .1875f), 0f, 0f));
        MODEL_BASE.add(new ModelPartItemDisplayRenderer(Material.CHISELED_NETHER_BRICKS, new Vector3f(-.0625f - .5f, 2.6875f, 1f), new Vector3f(.1875f, 1.6875f, 1.25f), 0f, 0f));
        MODEL_BASE.add(new ModelPartItemDisplayRenderer(Material.CHISELED_NETHER_BRICKS, new Vector3f(1.0625f - .5f, 2.6875f, 1f), new Vector3f(.1875f, 1.6875f, 1.25f), 0f, 0f));
        MODEL_BASE.add(new ModelPartItemDisplayRenderer(Material.TINTED_GLASS, new Vector3f(0f, 2.6875f, 1.0625f - .5f), new Vector3f(1f, 1.125f, .25f), 0f, 0f));

        // Outputs
        MODEL_BASE.add(new ModelPartItemDisplayRenderer(Material.NETHER_BRICKS, new Vector3f(0f, .5f, 1.375f - .5f), new Vector3f(1.875f, .1875f, .25f), 0f, 0f));
        MODEL_BASE.add(new ModelPartItemDisplayRenderer(Material.NETHER_BRICKS, new Vector3f(1f, .5f, .8125f - .5f), new Vector3f(.125f, .1875f, 1.375f), 0f, 0f));
        MODEL_BASE.add(new ModelPartItemDisplayRenderer(Material.NETHER_BRICKS, new Vector3f(-1f, .5f, .8125f - .5f), new Vector3f(.125f, .1875f, 1.375f), 0f, 0f));

        // Hopper
        MODEL_BASE.add(new ModelPartItemDisplayRenderer(Material.NETHER_BRICKS, new Vector3f(0f, 3.75f, 1f), new Vector3f(.625f, .4375f, .625f), 0f, 0f));
        MODEL_BASE.add(new ModelPartItemDisplayRenderer(Material.NETHER_BRICKS, new Vector3f(0f, 4.1875f, 1f), new Vector3f(.8125f, .4375f, .8125f), 0f, 0f));
        MODEL_BASE.add(new ModelPartItemDisplayRenderer(Material.NETHER_BRICKS, new Vector3f(0f, 4.625f, 1f), new Vector3f(1.25f, .4375f, 1.25f), 0f, 0f));

        // Pipe
        MODEL_BASE.add(new ModelPartItemDisplayRenderer(Material.END_ROD, new Vector3f(1.4375f - .5f, 3f, 1f), new Vector3f(1f, 3.125f, 1f), 0f, 0f));
        MODEL_BASE.add(new ModelPartItemDisplayRenderer(Material.END_ROD, new Vector3f(1.1875f - .5f, 4.625f, 1f), new Vector3f(1f, .625f, 1f), 270f, 90f));
    }

    static final ModelTemplate MODEL_IN_ITEM = new ModelTemplate();
    static {
        MODEL_IN_ITEM.add(new ModelPartItemDisplayRenderer(Material.CYAN_TERRACOTTA, new Vector3f(0f, 4.875f, 1f), new Vector3f(.625f, .25f, .625f), 0f, 0f));
    }

    static final ModelTemplate MODEL_OUT_ITEM = new ModelTemplate();
    static {
        MODEL_OUT_ITEM.add(new ModelPartItemDisplayRenderer(Material.CYAN_TERRACOTTA, new Vector3f(1f, .5f, .0625f - .5f), new Vector3f(.5f, .5f, .125f), 0f, 0f));
    }

    static final ModelTemplate MODEL_OUT_SLUG = new ModelTemplate();
    static {
        MODEL_OUT_SLUG.add(new ModelPartItemDisplayRenderer(Material.CYAN_TERRACOTTA, new Vector3f(-1f, .5f, .0625f - .5f), new Vector3f(.5f, .5f, .125f), 0f, 0f));
    }

    public BlastFurnace() {
        super("torus:blast_furnace", BlastFurnaceInstance.class);
    }

    @Override
    protected void createBounds(ByteArrayBuilder builder) {
        for (int x = -1; x <= 1; x++) {
            for (int z = 0; z <= 2; z++) {
                builder.add(x, 0, z);
            }
        }
        builder.add(0, 1, 1);
        builder.add(0, 2, 1);
        builder.add(0, 3, 1);
        builder.add(0, 4, 1);
    }

    @Override
    protected StructureInstance instantiate(@NotNull BlockLocation location, Direction direction) {
        return new BlastFurnaceInstance(location, new StructureBodyDef(new StructureComponentDef[]{
          new StructureComponentDef("base", new Vector3f(0f, 0f, 0f), MODEL_BASE),
          new StructureComponentDef("in_item", new Vector3f(0, 4, 1), MODEL_IN_ITEM, new StructureConnectorDef(
            Connector.Matter.ITEM, Connector.FlowDirection.IN, Direction.UP.mask()
          )),
          new StructureComponentDef("out_item", new Vector3f(1, 0, 0), MODEL_OUT_ITEM.build(location.toBukkit().add(.5, 0, .5), direction), new StructureConnectorDef(
            Connector.Matter.ITEM, Connector.FlowDirection.OUT, direction.mask()
          )),
          new StructureComponentDef("out_slug", new Vector3f(-1, 0, 0), MODEL_OUT_SLUG.build(location.toBukkit().add(.5, 0, .5), direction), new StructureConnectorDef(
            Connector.Matter.ITEM, Connector.FlowDirection.OUT, direction.mask()
          ))
        }), direction);
    }

}
