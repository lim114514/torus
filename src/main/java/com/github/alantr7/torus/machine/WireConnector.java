package com.github.alantr7.torus.machine;

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

public class WireConnector extends Structure {

    public static final PartModelTemplate MODEL_BASE = new PartModelTemplate("base");
    static {
        MODEL_BASE.add(new PartModelElementItemDisplayRenderer(Material.GRAY_WOOL, new Vector3f(0, .5f, .25f), new Vector3f(.1875f, .1875f, .5f), new Vector3f()));
        MODEL_BASE.add(new PartModelElementItemDisplayRenderer(Material.BROWN_TERRACOTTA, new Vector3f(0, .5f, .3125f), new Vector3f(.375f, .375f,  .125f), new Vector3f()));
        MODEL_BASE.add(new PartModelElementItemDisplayRenderer(Material.BROWN_TERRACOTTA, new Vector3f(0, .5f, .125f), new Vector3f(.375f, .375f,  .125f), new Vector3f()));
        MODEL_BASE.add(new PartModelElementItemDisplayRenderer(Material.BROWN_TERRACOTTA, new Vector3f(0, .5f, .4375f - .5f), new Vector3f(.375f, .375f,  .125f), new Vector3f()));
    }

    public static final ModelTemplate MODEL = new ModelTemplate();
    static {
        MODEL.add(MODEL_BASE);
    }

    public WireConnector() {
        super("torus:wire_connector", WireConnectorInstance.class);
        isInteractable = true;
        isHeavy = false;
    }

    @Override
    public ModelTemplate getInitialModel() {
        return MODEL;
    }

    @Override
    protected StructureInstance instantiate(@NotNull BlockLocation location, Direction direction) {
        return new WireConnectorInstance(WireConnectorInstance.Type.CONNECTOR, location, new StructureBodyDef(new StructureComponentDef[]{
          new StructureComponentDef("base", new Vector3f())
        }), direction);
    }

}
