package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.math.ByteArrayBuilder;
import com.github.alantr7.torus.model.ModelTemplate;
import com.github.alantr7.torus.model.PartModelElementItemDisplayRenderer;
import com.github.alantr7.torus.model.PartModelTemplate;
import com.github.alantr7.torus.structure.Structure;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.structure.builder.StructureBodyDef;
import com.github.alantr7.torus.structure.builder.StructureComponentDef;
import com.github.alantr7.torus.structure.builder.StructureConnectorDef;
import com.github.alantr7.torus.structure.component.Connector;
import com.github.alantr7.torus.world.BlockLocation;
import com.github.alantr7.torus.world.Direction;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class Windmill extends Structure {

    public static final PartModelTemplate MODEL_BASE = new PartModelTemplate("base");
    static {
        MODEL_BASE.add(new PartModelElementItemDisplayRenderer(Material.GRAY_CONCRETE_POWDER, 0f, .0625f, 0f, 1f, 0.125f, 1f));

        MODEL_BASE.add(new PartModelElementItemDisplayRenderer(Material.LIGHT_GRAY_CONCRETE, 0f, .75f, 0f, .625f, 1.5f, .625f));
        MODEL_BASE.add(new PartModelElementItemDisplayRenderer(Material.LIGHT_GRAY_CONCRETE, 0f, 2.0625f, 0f, .4375f, 1.5f, .4375f));
        MODEL_BASE.add(new PartModelElementItemDisplayRenderer(Material.LIGHT_GRAY_CONCRETE, 0f, 3.3125f, 0f, .3125f, 1.8125f, .3125f));

        MODEL_BASE.add(new PartModelElementItemDisplayRenderer(Material.DISPENSER, 0f, 3.75f, 0f, 0.6875f, 0.6875f, 0.875f, 180, 0));
        MODEL_BASE.add(new PartModelElementItemDisplayRenderer(Material.END_ROD, 0f, 3.75f, -.5f, 1f, 1f, 1f, 0f, 90f));
    }

    public static final PartModelTemplate MODEL_BLADE_1 = new PartModelTemplate("blade_1");
    static {
        MODEL_BLADE_1.add(new PartModelElementItemDisplayRenderer(Material.IRON_BLOCK, 0f, 3.75f, -.6875f, 1.875f, .375f, .25f, 0f, 0f));
        MODEL_BLADE_1.add(new PartModelElementItemDisplayRenderer(Material.IRON_BLOCK, 0f, 3.75f, -.6875f, 4.6875f, .1875f, .1875f, 0f, 0f));
    }

    public static final PartModelTemplate MODEL_BLADE_2 = new PartModelTemplate("blade_2");
    static {
        MODEL_BLADE_2.add(new PartModelElementItemDisplayRenderer(Material.IRON_BLOCK, 0f, 3.75f, -.75f, .375f, 1.875f, .25f, 0f, 0f));
        MODEL_BLADE_2.add(new PartModelElementItemDisplayRenderer(Material.IRON_BLOCK, 0f, 3.75f, -.75f, .1875f, 4.6875f, .125f, 0f, 0f));
    }

    public static final PartModelTemplate MODEL_CONNECTOR = new PartModelTemplate("connector");
    static {
        MODEL_CONNECTOR.add(new PartModelElementItemDisplayRenderer(Material.GRAY_CONCRETE, 0f, .5f, .0625f - .5f, .625f, .625f, .125f));
    }

    public static final ModelTemplate MODEL = new ModelTemplate();
    static {
        MODEL.add(MODEL_BASE);
        MODEL.add(MODEL_BLADE_1);
        MODEL.add(MODEL_BLADE_2);
        MODEL.add(MODEL_CONNECTOR);
    }

    public static final float MAXIMUM_SPEED = 1.85f * (float) Math.PI / 3f;

    public Windmill() {
        super("torus:windmill", "Windmill", WindmillInstance.class);
    }

    @Override
    protected void createBounds(ByteArrayBuilder builder) {
        builder.add(0, 0, 0);
        builder.add(0, 1, 0);
        builder.add(0, 2, 0);
        builder.add(0, 3, 0);
    }

    @Override
    public ModelTemplate getInitialModel() {
        return MODEL;
    }

    @Override
    protected StructureInstance instantiate(@NotNull BlockLocation location, Direction direction) {
        return new WindmillInstance(location, new StructureBodyDef(new StructureComponentDef[]{
          new StructureComponentDef("base", new Vector3f()),
          new StructureComponentDef("in_power", new Vector3f(), new StructureConnectorDef(
            Connector.Matter.ENERGY, Connector.FlowDirection.OUT, direction.mask()
          ))
        }), direction);
    }

}
