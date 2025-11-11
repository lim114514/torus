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

public class WireRelay extends Structure {

    public static final PartModelTemplate MODEL_BASE = new PartModelTemplate("base");
    static {
        MODEL_BASE.add(new PartModelElementItemDisplayRenderer(Material.LIGHT_GRAY_WOOL, new Vector3f(0, 0.4375f, 0), new Vector3f(0.25f, 0.3125f, 0.25f), new Vector3f()));
        MODEL_BASE.add(new PartModelElementItemDisplayRenderer(Material.STRIPPED_SPRUCE_LOG, new Vector3f(0, .1875f, 0), new Vector3f(0.125f, 0.375f, 0.125f), new Vector3f()));
        MODEL_BASE.add(new PartModelElementItemDisplayRenderer(Material.BROWN_CONCRETE, new Vector3f(0, .625f, 0), new Vector3f(0.375f, 0.125f, 0.375f), new Vector3f()));
        MODEL_BASE.add(new PartModelElementItemDisplayRenderer(Material.BROWN_CONCRETE, new Vector3f(0, .4375f, 0), new Vector3f(0.375f, 0.125f, 0.375f), new Vector3f()));
        MODEL_BASE.add(new PartModelElementItemDisplayRenderer(Material.BROWN_CONCRETE, new Vector3f(0, .25f, 0), new Vector3f(0.375f, 0.125f, 0.375f), new Vector3f()));
    }

    public static final ModelTemplate MODEL = new ModelTemplate();
    static {
        MODEL.add(MODEL_BASE);
    }

    public WireRelay() {
        super("torus:wire_relay", WireConnectorInstance.class);
        isInteractable = true;
        isHeavy = false;
    }

    @Override
    public ModelTemplate getInitialModel() {
        return MODEL;
    }

    @Override
    protected StructureInstance instantiate(@NotNull BlockLocation location, Direction direction) {
        return new WireConnectorInstance(WireConnectorInstance.Type.RELAY, location, new StructureBodyDef(new StructureComponentDef[]{
          new StructureComponentDef("base", new Vector3f())
        }), direction);
    }

}
