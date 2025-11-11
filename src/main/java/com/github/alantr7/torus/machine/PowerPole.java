package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.math.ByteArrayBuilder;
import com.github.alantr7.torus.model.ModelTemplate;
import com.github.alantr7.torus.model.PartModelElementItemDisplayRenderer;
import com.github.alantr7.torus.model.PartModelTemplate;
import com.github.alantr7.torus.structure.Structure;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.structure.builder.StructureBodyDef;
import com.github.alantr7.torus.structure.builder.StructureComponentDef;
import com.github.alantr7.torus.world.BlockLocation;
import com.github.alantr7.torus.world.Direction;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class PowerPole extends Structure {

    public static final PartModelTemplate MODEL_BASE = new PartModelTemplate("base");
    static {
        MODEL_BASE.add(new PartModelElementItemDisplayRenderer(Material.STRIPPED_SPRUCE_WOOD, new Vector3f(0, .5f, 0), new Vector3f(.3125f, 1f, .3125f), new Vector3f()));
        MODEL_BASE.add(new PartModelElementItemDisplayRenderer(Material.STRIPPED_SPRUCE_WOOD, new Vector3f(0, 2.375f, 0), new Vector3f(.25f, 2.9375f, .25f), new Vector3f()));
        MODEL_BASE.add(new PartModelElementItemDisplayRenderer(Material.STRIPPED_SPRUCE_WOOD, new Vector3f(0, 3.9375f, 0), new Vector3f(.1875f, 2.25f, .25f), new Vector3f(0, 0, 90f)));
        MODEL_BASE.add(new PartModelElementItemDisplayRenderer(Material.STRIPPED_SPRUCE_WOOD, new Vector3f(.3125f, 3.625f, 0), new Vector3f(.1875f, .875f, .125f), new Vector3f(0, 0, -45f)));
        MODEL_BASE.add(new PartModelElementItemDisplayRenderer(Material.STRIPPED_SPRUCE_WOOD, new Vector3f(.1875f - .5f, 3.625f, 0), new Vector3f(.1875f, .875f, .125f), new Vector3f(0, 0, 45f)));
    }

    public static final ModelTemplate MODEL = new ModelTemplate();
    static {
        MODEL.add(MODEL_BASE);
    }

    public PowerPole() {
        super("torus:power_pole", PowerPoleInstance.class);
        isHeavy = false;
    }

    @Override
    public ModelTemplate getInitialModel() {
        return MODEL;
    }

    @Override
    protected void createBounds(ByteArrayBuilder builder) {
        builder.add(0, 0, 0);
        builder.add(0, 1, 0);
        builder.add(0, 2, 0);
        builder.add(0, 3, 0);
        builder.add(1, 3, 0);
        builder.add(-1, 3, 0);
    }

    @Override
    protected StructureInstance instantiate(@NotNull BlockLocation location, Direction direction) {
        return new PowerPoleInstance(location, new StructureBodyDef(new StructureComponentDef[]{
          new StructureComponentDef("base", new Vector3f())
        }), direction);
    }
}
