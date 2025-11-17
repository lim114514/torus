package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.model.ModelTemplate;
import com.github.alantr7.torus.world.BlockLocation;
import com.github.alantr7.torus.world.Direction;
import com.github.alantr7.torus.structure.Structure;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.structure.builder.StructureBodyDef;
import com.github.alantr7.torus.structure.builder.StructureComponentDef;
import com.github.alantr7.torus.structure.builder.StructureConnectorDef;
import com.github.alantr7.torus.structure.component.Connector;
import com.github.alantr7.torus.model.PartModelElementItemDisplayRenderer;
import com.github.alantr7.torus.model.PartModelTemplate;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class BlockBreaker extends Structure {

    static PartModelTemplate BASE_MODEL = new PartModelTemplate("base");
    static {
        BASE_MODEL.add(new PartModelElementItemDisplayRenderer(Material.PISTON, new Vector3f(0f, 0.5f, 0.0f), new Vector3f(.875f, 1f, .875f), 0f, -90f));
        BASE_MODEL.add(new PartModelElementItemDisplayRenderer(Material.STONECUTTER, new Vector3f(0f, 0.5f, 0.03625f - .5f), new Vector3f(.6875f, 1.875f, .6875f), new Vector3f(90f, 45f, -180f)));
        BASE_MODEL.add(new PartModelElementItemDisplayRenderer(Material.STONECUTTER, new Vector3f(0f, 0.5f, 0.03625f - .5f), new Vector3f(.6875f, 1.875f, .6875f), new Vector3f(-90f, 45f, 0f)));
    }

    static PartModelTemplate ENERGY_CONNECTOR_MODEL = new PartModelTemplate("in_energy");
    static {
        ENERGY_CONNECTOR_MODEL.add(new PartModelElementItemDisplayRenderer(Material.GRAY_CONCRETE, new Vector3f(0f, 0.5f, 0.5625f), new Vector3f(0.625f, 0.625f, 0.125f), 0f, 0f));
    }

    static PartModelTemplate ITEM_CONNECTOR_MODEL = new PartModelTemplate("out_item");
    static {
        ENERGY_CONNECTOR_MODEL.add(new PartModelElementItemDisplayRenderer(Material.GRAY_CONCRETE, new Vector3f(0f, -.0625f, 0f), new Vector3f(.625f, .125f, 0.625f), 0f, 0f));
    }

    public static final ModelTemplate INITIAL_MODEL = new ModelTemplate();
    static {
        INITIAL_MODEL.add(BASE_MODEL);
        INITIAL_MODEL.add(ENERGY_CONNECTOR_MODEL);
        INITIAL_MODEL.add(ITEM_CONNECTOR_MODEL);
    }

    public BlockBreaker() {
        super("torus:block_breaker", "Block Breaker", BlockBreakerInstance.class);
        isHeavy = false;
        itemDropDataWhitelist.add("energy");
    }

    @Override
    public ModelTemplate getInitialModel() {
        return INITIAL_MODEL;
    }

    @Override
    protected StructureInstance instantiate(@NotNull BlockLocation location, Direction direction) {
        return new BlockBreakerInstance(location, new StructureBodyDef(
          new StructureComponentDef[]{
            new StructureComponentDef("body", new Vector3f(0, 0, 0)),
            new StructureComponentDef("power_connector", new Vector3f(0, 0, 0), new StructureConnectorDef(
              Connector.Matter.ENERGY, Connector.FlowDirection.IN, direction.getOpposite().mask()
            )),
            new StructureComponentDef("item_connector", new Vector3f(0, 0, 0), new StructureConnectorDef(
              Connector.Matter.ITEM, Connector.FlowDirection.OUT, Direction.DOWN.mask()
            )) }
        ), direction);
    }

}
