package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.item.HeadData;
import com.github.alantr7.torus.math.ByteArrayBuilder;
import com.github.alantr7.torus.math.Direction;
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

import java.util.HashSet;
import java.util.Set;

public class Quarry extends Structure {

    static ModelTemplate MODEL_BASE = new ModelTemplate();
    static {
        // Controller
        MODEL_BASE.add(new ModelPartItemDisplayRenderer(HeadData.create("http://textures.minecraft.net/texture/9318a300fda1733caf16ce78bc6dc82e7f0e1bec7e9e3cb24179e28580d3a2f8"), new float[] { 0f, .875f, -6f, 1.5f, 1.5f, 1.5f }));
        MODEL_BASE.add(new ModelPartItemDisplayRenderer(Material.GRAY_CONCRETE, new float[] { 0f, .0625f, -6f, 1f, .125f, 1f }));

        // Cables to/from controller
        MODEL_BASE.add(new ModelPartItemDisplayRenderer(Material.GRAY_CONCRETE, new Vector3f(-1.9375f - .5f, .6875f, -5.25f), new Vector3f(4.75f, .125f, .125f), 0f, 0f));
        MODEL_BASE.add(new ModelPartItemDisplayRenderer(Material.GRAY_CONCRETE, new Vector3f(0f, .6875f, -5.4375f), new Vector3f(.125f, .125f, .5f), 0f, 0f));
        MODEL_BASE.add(new ModelPartItemDisplayRenderer(Material.GRAY_CONCRETE, new Vector3f(-4.875f, 2.75f, -5.25f), new Vector3f(.125f, 4.25f, .125f), 0f, 0f));
        MODEL_BASE.add(new ModelPartItemDisplayRenderer(Material.GRAY_CONCRETE, new Vector3f(-4.875f, 4.8125f, -4.8125f), new Vector3f(.125f, .125f, 1f), 0f, 0f));
        MODEL_BASE.add(new ModelPartItemDisplayRenderer(Material.GRAY_CONCRETE, new Vector3f(-4.75f, 4.8125f, -5f), new Vector3f(.25f, .125f, .125f), 0f, 0f));

        MODEL_BASE.add(new ModelPartItemDisplayRenderer(Material.LIGHT_GRAY_TERRACOTTA, new Vector3f(0f, .5f, -5.4375f), new Vector3f(.125f, .125f, .5f), 0f, 0f));
        MODEL_BASE.add(new ModelPartItemDisplayRenderer(Material.LIGHT_GRAY_TERRACOTTA, new Vector3f(-2.5625f, .5f, -5.25f), new Vector3f(5.125f, .125f, .125f), 0f, 0f));
        MODEL_BASE.add(new ModelPartItemDisplayRenderer(Material.LIGHT_GRAY_TERRACOTTA, new Vector3f(-5.0625f, 2.5f, -5.25f), new Vector3f(.125f, 4.125f, .125f), 0f, 0f));
        MODEL_BASE.add(new ModelPartItemDisplayRenderer(Material.LIGHT_GRAY_TERRACOTTA, new Vector3f(-5.0625f, 4.5f, -4.8125f), new Vector3f(.125f, .125f, 1f), 0f, 0f));

        // Horizontal
        MODEL_BASE.add(new ModelPartItemDisplayRenderer(Material.BROWN_CONCRETE, new Vector3f(0f, 4.5f, -5f), new Vector3f(10f, 0.3125f, 0.3125f), 0f, 0f));
        MODEL_BASE.add(new ModelPartItemDisplayRenderer(Material.BROWN_CONCRETE, new Vector3f(0f, .5f, -5f), new Vector3f(10f, 0.3125f, 0.3125f), 0f, 0f));
        MODEL_BASE.add(new ModelPartItemDisplayRenderer(Material.BROWN_CONCRETE, new Vector3f(0f, .5f, 5f), new Vector3f(10f, 0.3125f, 0.3125f), 0f, 0f));
        MODEL_BASE.add(new ModelPartItemDisplayRenderer(Material.BROWN_CONCRETE, new Vector3f(0f, 4.5f, 5f), new Vector3f(10f, 0.3125f, 0.3125f), 0f, 0f));

        MODEL_BASE.add(new ModelPartItemDisplayRenderer(Material.BROWN_CONCRETE, new Vector3f(-5f, 4.5f, 0f), new Vector3f(0.3125f, 0.3125f, 10f), 0f, 0f));
        MODEL_BASE.add(new ModelPartItemDisplayRenderer(Material.BROWN_CONCRETE, new Vector3f(-5f, .5f, 0f), new Vector3f(0.3125f, 0.3125f, 10f), 0f, 0f));
        MODEL_BASE.add(new ModelPartItemDisplayRenderer(Material.BROWN_CONCRETE, new Vector3f(5f, .5f, 0f), new Vector3f(0.3125f, 0.3125f, 10f), 0f, 0f));
        MODEL_BASE.add(new ModelPartItemDisplayRenderer(Material.BROWN_CONCRETE, new Vector3f(5f, 4.5f, 0f), new Vector3f(0.3125f, 0.3125f, 10f), 0f, 0f));

        // Vertical
        MODEL_BASE.add(new ModelPartItemDisplayRenderer(Material.BROWN_CONCRETE, new Vector3f(5f, 2.3125f, -5f), new Vector3f(0.3125f, 4.6875f, 0.3125f), 0f, 0f));
        MODEL_BASE.add(new ModelPartItemDisplayRenderer(Material.BROWN_CONCRETE, new Vector3f(5f, 2.3125f, 5f), new Vector3f(0.3125f, 4.6875f, 0.3125f), 0f, 0f));
        MODEL_BASE.add(new ModelPartItemDisplayRenderer(Material.BROWN_CONCRETE, new Vector3f(-5f, 2.3125f, -5f), new Vector3f(0.3125f, 4.6875f, 0.3125f), 0f, 0f));
        MODEL_BASE.add(new ModelPartItemDisplayRenderer(Material.BROWN_CONCRETE, new Vector3f(-5f, 2.3125f, 5f), new Vector3f(0.3125f, 4.6875f, 0.3125f), 0f, 0f));

        // Motors
        MODEL_BASE.add(new ModelPartItemDisplayRenderer(Material.WEATHERED_COPPER, new Vector3f(-4.25f, 4.875f, -5f), new Vector3f(1f, .5f, .4375f), 0f, 0f));
        MODEL_BASE.add(new ModelPartItemDisplayRenderer(Material.WEATHERED_COPPER, new Vector3f(-5f, 4.875f, -4.25f), new Vector3f(.4375f, .5f, 1f), 0f, 0f));
    }

    static ModelTemplate MODEL_ENERGY_CONNECTOR = new ModelTemplate();
    static {
        MODEL_ENERGY_CONNECTOR.add(new ModelPartItemDisplayRenderer(Material.GRAY_CONCRETE, new Vector3f(.0625f -.5f, .5f, -6f), new Vector3f(.125f, .625f, .625f), 0f, 0f));
    }

    static ModelTemplate MODEL_ITEM_CONNECTOR = new ModelTemplate();
    static {
        MODEL_ITEM_CONNECTOR.add(new ModelPartItemDisplayRenderer(Material.GRAY_CONCRETE, new Vector3f(0f, .9375f, -6f), new Vector3f(.625f, .125f, .625f), 0f, 0f));
    }

    static ModelTemplate MODEL_DRILL_HOLDER = new ModelTemplate();
    static {
        MODEL_DRILL_HOLDER.add(new ModelPartItemDisplayRenderer(Material.ORANGE_TERRACOTTA, new Vector3f(0f, 4.4375f, 0f), new Vector3f(.3125f, 2.1875f, .3125f), 0f, 0f));

        // Item conduit
        MODEL_DRILL_HOLDER.add(new ModelPartItemDisplayRenderer(Material.LIGHT_GRAY_TERRACOTTA, new Vector3f(0f, 5.8125f, 0f), new Vector3f(.125f, .5625f, .125f), 0f, 0f));
        MODEL_DRILL_HOLDER.add(new ModelPartItemDisplayRenderer(Material.LIGHT_GRAY_TERRACOTTA, new Vector3f(-.5f, 5.3125f, 0f), new Vector3f(.125f, 1.5f, .125f), 0f, 0f));
        MODEL_DRILL_HOLDER.add(new ModelPartItemDisplayRenderer(Material.LIGHT_GRAY_TERRACOTTA, new Vector3f(-.25f, 6.125f, 0f), new Vector3f(.625f, .125f, .125f), 0f, 0f));

        // Energy cable
        MODEL_DRILL_HOLDER.add(new ModelPartItemDisplayRenderer(Material.GRAY_CONCRETE, new Vector3f(.25f - .5f, 4.875f, 0f), new Vector3f(.125f, .6875f, .125f), 0f, 0f));
        MODEL_DRILL_HOLDER.add(new ModelPartItemDisplayRenderer(Material.GRAY_CONCRETE, new Vector3f(.25f - .5f, 5.1875f, .375f - .5f), new Vector3f(.125f, .175f, .375f), 0f, 0f));

        // Motor
        MODEL_DRILL_HOLDER.add(new ModelPartItemDisplayRenderer(Material.WEATHERED_COPPER, new Vector3f(0f, 5.125f, .1875f - .5f), new Vector3f(.375f, .8125f, .3125f), 0f, 0f));
    }

    static ModelTemplate MODEL_DRILL = new ModelTemplate();
    static {
        MODEL_DRILL.add(new ModelPartItemDisplayRenderer(Material.ORANGE_TERRACOTTA, new Vector3f(0f, 2.125f, 0f), new Vector3f(.1875f, 3.25f, .1875f), 0f, 0f));
    }

    static ModelTemplate MODEL_DRILL_TIP = new ModelTemplate();
    static {
        MODEL_DRILL_TIP.add(new ModelPartItemDisplayRenderer(Material.QUARTZ_BLOCK, new Vector3f(0f, .375f, 0f), new Vector3f(.1875f, .25f, .1875f), 0f, 0f));
    }

    static ModelTemplate MODEL_MOVER_X = new ModelTemplate();
    static {
        MODEL_MOVER_X.add(new ModelPartItemDisplayRenderer(Material.CYAN_TERRACOTTA, new Vector3f(0f, 4.5f, 0f), new Vector3f(10f, .25f, .25f), 0f, 0f));
    }

    static ModelTemplate MODEL_MOVER_Z = new ModelTemplate();
    static {
        MODEL_MOVER_Z.add(new ModelPartItemDisplayRenderer(Material.CYAN_TERRACOTTA, new Vector3f(0f, 4.5f, 0f), new Vector3f(.25f, .25f, 10f), 0f, 0f));
    }

    public static final Set<Material> BLOCK_BLACKLIST = new HashSet<>();
    static {
        BLOCK_BLACKLIST.add(Material.BEDROCK);
    }

    public Quarry() {
        super("torus:quarry", QuarryInstance.class);
        offset = new byte[] {0, 0, -6};
    }

    @Override
    protected void createBounds(ByteArrayBuilder builder) {
        int length = 5;
        for (int x = 0; x <= length; x++) {
            builder.add(x, 0, length);
            builder.add(x, 0, -length);
            builder.add(-x, 0, length);
            builder.add(-x, 0, -length);

            builder.add(length, 0, x);
            builder.add(-length, 0, x);
            builder.add(length, 0, -x);
            builder.add(-length, 0, -x);

            builder.add(x, 4, length);
            builder.add(x, 4, -length);
            builder.add(-x, 4, length);
            builder.add(-x, 4, -length);

            builder.add(length, 4, x);
            builder.add(-length, 4, x);
            builder.add(length, 4, -x);
            builder.add(-length, 4, -x);
        }

        for (int i = 1; i <= 3; i++) {
            builder.add(length, i, length);
            builder.add(length, i, -length);
            builder.add(-length, i, -length);
            builder.add(-length, i, length);
        }

        // Controller
        builder.add(0, 0, -length - 1);
    }

    @Override
    protected StructureInstance instantiate(@NotNull BlockLocation location, Direction direction) {
        return new QuarryInstance(this, location, new StructureBodyDef(
          new StructureComponentDef[]{
            new StructureComponentDef("base", new Vector3f(), MODEL_BASE.build(location.toBukkit().add(.5f, 0, .5f), direction)),
            new StructureComponentDef("in_energy", new Vector3f(0, 0, -6), MODEL_ENERGY_CONNECTOR.build(location.toBukkit().add(.5f, 0, .5f), direction)),
            new StructureComponentDef("out_item", new Vector3f(0, 0, -6), MODEL_ITEM_CONNECTOR.build(location.toBukkit().add(.5f, 0, .5f), direction)),
            new StructureComponentDef("drill_holder", new Vector3f(), MODEL_DRILL_HOLDER.build(location.toBukkit().add(.5f, 0, .5f), direction)),
            new StructureComponentDef("drill", new Vector3f(), MODEL_DRILL.build(location.toBukkit().add(.5f, 0, .5f), direction)),
            new StructureComponentDef("drill_tip", new Vector3f(), MODEL_DRILL_TIP.build(location.toBukkit().add(.5f, 0, .5f), direction)),
            new StructureComponentDef("mover_x", new Vector3f(), MODEL_MOVER_X.build(location.toBukkit().add(.5f, 0, .5f), direction)),
            new StructureComponentDef("mover_z", new Vector3f(), MODEL_MOVER_Z.build(location.toBukkit().add(.5f, 0, .5f), direction)),
          },
          new StructureConnectorDef[]{
            new StructureConnectorDef("in_energy", Connector.Matter.ENERGY, Connector.FlowDirection.OUT, direction.getLeft().mask()),
            new StructureConnectorDef("out_item", Connector.Matter.ITEM, Connector.FlowDirection.OUT, Direction.UP.mask())
          }
        ), direction);
    }

}
