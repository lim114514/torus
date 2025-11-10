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
        MODEL_BASE.add(new PartModelElementItemDisplayRenderer(Material.STRIPPED_DARK_OAK_WOOD, new Vector3f(0, .5f, 0), new Vector3f(.4375f, 1f, .4375f), new Vector3f()));
        MODEL_BASE.add(new PartModelElementItemDisplayRenderer(Material.STRIPPED_SPRUCE_LOG, new Vector3f(0, 2.5f, 0), new Vector3f(.3125f, 3.0625f, .3125f), new Vector3f()));
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
    }

    @Override
    protected StructureInstance instantiate(@NotNull BlockLocation location, Direction direction) {
        return new PowerPoleInstance(location, new StructureBodyDef(new StructureComponentDef[]{
          new StructureComponentDef("base", new Vector3f())
        }), direction);
    }
}
