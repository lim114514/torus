package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.model.ModelTemplate;
import com.github.alantr7.torus.world.Direction;
import com.github.alantr7.torus.math.ByteArrayBuilder;
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

public class PowerBank extends Structure {

    static PartModelTemplate MODEL = new PartModelTemplate("base");
    static {
        MODEL.add(new PartModelElementItemDisplayRenderer(Material.CYAN_TERRACOTTA, new Vector3f(0, 0.75f, 0), new Vector3f(0.75f, 1.5f, 0.75f), 0f, 0f));
        MODEL.add(new PartModelElementItemDisplayRenderer(Material.WHITE_STAINED_GLASS, new Vector3f(0, 0.72125f, 0.121875f - 0.5f), new Vector3f(0.1875f, 1.1875f, 0.0625f), 0f, 0f));
        MODEL.add(new PartModelElementItemDisplayRenderer(Material.GRAY_CONCRETE, new Vector3f(0, 0.72125f, 0.11625f - 0.5f), new Vector3f(0.125f, 1.12f, 0.0625f), 0f, 0f));
    }

    static PartModelTemplate PROGRESS_MODEL = new PartModelTemplate("progress");
    static {
        PROGRESS_MODEL.add(new PartModelElementItemDisplayRenderer(Material.CYAN_CONCRETE, new Vector3f(0, 0.1675f, 0.11525f - 0.5f), new Vector3f(0.12f, 0.0011f, 0.0625f), 0f, 0f));
    }

    static PartModelTemplate CONNECTOR_MODEL = new PartModelTemplate("connector");
    static {
        CONNECTOR_MODEL.add(new PartModelElementItemDisplayRenderer(Material.GRAY_WOOL, new Vector3f(0f, 1.75f, 0), new Vector3f(.2f, 0.5f, .2f), 0f, 0f));
        CONNECTOR_MODEL.add(new PartModelElementItemDisplayRenderer(Material.WAXED_CUT_COPPER_SLAB, new Vector3f(0f, 1.625f, 0), new Vector3f(0.375f, 0.1875f, 0.375f), 0f, 0f));
        CONNECTOR_MODEL.add(new PartModelElementItemDisplayRenderer(Material.WAXED_CUT_COPPER_SLAB, new Vector3f(0f, 1.78375f, 0), new Vector3f(0.375f, 0.1875f, 0.375f), 0f, 0f));
        CONNECTOR_MODEL.add(new PartModelElementItemDisplayRenderer(Material.WAXED_CUT_COPPER_SLAB, new Vector3f(0f, 1.9375f, 0), new Vector3f(0.375f, 0.1875f, 0.375f), 0f, 0f));
    }

    public static final ModelTemplate INITIAL_MODEL = new ModelTemplate();
    static {
        INITIAL_MODEL.add(MODEL);
        INITIAL_MODEL.add(PROGRESS_MODEL);
        INITIAL_MODEL.add(CONNECTOR_MODEL);
    }

    public PowerBank() {
        super("torus:power_bank", "Power Bank", PowerBankInstance.class);
        itemDropDataWhitelist.add("energy");
    }

    @Override
    public ModelTemplate getInitialModel() {
        return INITIAL_MODEL;
    }

    @Override
    public void createBounds(ByteArrayBuilder builder) {
        builder.add(0, 0, 0);
        builder.add(0, 1, 0);
    }

    @Override
    protected StructureInstance instantiate(@NotNull BlockLocation location, Direction direction) {
        return new PowerBankInstance(location, new StructureBodyDef(new StructureComponentDef[]{
          new StructureComponentDef("base", new Vector3f()),
          new StructureComponentDef(
            "power_connector", new Vector3f(0, 1, 0), new StructureConnectorDef(
            Connector.Matter.ENERGY, Connector.FlowDirection.ALL, Direction.UP.mask()
          )),
          new StructureComponentDef("charge", new Vector3f())
        }), direction);
    }

}
