package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.world.BlockLocation;
import com.github.alantr7.torus.math.Direction;
import com.github.alantr7.torus.structure.Structure;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.structure.builder.StructureBodyDef;
import com.github.alantr7.torus.structure.builder.StructureComponentDef;
import com.github.alantr7.torus.structure.builder.StructureConnectorDef;
import com.github.alantr7.torus.structure.component.Connector;
import com.github.alantr7.torus.structure.display.ModelPartItemDisplayRenderer;
import com.github.alantr7.torus.structure.display.Model;
import com.github.alantr7.torus.structure.display.ModelTemplate;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class BlockBreaker extends Structure {

    static ModelTemplate BASE_MODEL = new ModelTemplate();
    static {
        BASE_MODEL.add(new ModelPartItemDisplayRenderer(Material.STICKY_PISTON, new Vector3f(0f, 0.5f, 0.0f), new Vector3f(1f, 0.75f, 1f), 180f, 90f));
        BASE_MODEL.add(new ModelPartItemDisplayRenderer(Material.DISPENSER, new Vector3f(0f, 0.5f, -0.5f + 0.0625f), new Vector3f(0.75f, 0.75f, 0.125f), 180f, 0f));
    }

    static ModelTemplate CONNECTOR_MODEL = new ModelTemplate();
    static {
        CONNECTOR_MODEL.add(new ModelPartItemDisplayRenderer(Material.GRAY_CONCRETE, new Vector3f(0f, 0.5f, 0.4375f), new Vector3f(0.625f, 0.625f, 0.125f), 0f, 0f));
    }

    public BlockBreaker() {
        super("torus:block_breaker", BlockBreakerInstance.class);
    }

    @Override
    protected StructureInstance instantiate(@NotNull BlockLocation location, Direction direction) {
        return new BlockBreakerInstance(location, new StructureBodyDef(
          new StructureComponentDef[]{
            new StructureComponentDef("body", new Vector3f(0, 0, 0), BASE_MODEL),
            new StructureComponentDef("power_connector", new Vector3f(0, 0, 0), CONNECTOR_MODEL, new StructureConnectorDef(
              Connector.Matter.ENERGY, Connector.FlowDirection.IN, direction.getOpposite().mask()
            )),
            new StructureComponentDef("item_connector", new Vector3f(0, 0, 0), (Model) null, new StructureConnectorDef(
              Connector.Matter.ITEM, Connector.FlowDirection.OUT, Direction.DOWN.mask()
            )) }
        ), direction);
    }

}
