package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.math.BlockLocation;
import com.github.alantr7.torus.math.Direction;
import com.github.alantr7.torus.structure.Structure;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.structure.builder.StructureBodyDef;
import com.github.alantr7.torus.structure.builder.StructureComponentDef;
import com.github.alantr7.torus.structure.builder.StructureConnectorDef;
import com.github.alantr7.torus.structure.component.Connector;
import com.github.alantr7.torus.structure.display.ItemDisplayModelTemplate;
import com.github.alantr7.torus.structure.display.Model;
import com.github.alantr7.torus.structure.display.ModelTemplate;
import org.bukkit.Material;
import org.bukkit.entity.ItemDisplay;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class BlockBreaker extends Structure {

    static ModelTemplate BASE_MODEL = new ModelTemplate();
    static {
        BASE_MODEL.add(new ItemDisplayModelTemplate(Material.STICKY_PISTON, ItemDisplay.ItemDisplayTransform.NONE, 0, new Vector3f(0f, 0.5f, 0.0f), new Vector3f(1f, 0.75f, 1f), 180f, 90f));
        BASE_MODEL.add(new ItemDisplayModelTemplate(Material.DISPENSER, ItemDisplay.ItemDisplayTransform.NONE, 0, new Vector3f(0f, 0.5f, -0.5f + 0.0625f), new Vector3f(0.75f, 0.75f, 0.125f), 180f, 0f));
    }

    static ModelTemplate CONNECTOR_MODEL = new ModelTemplate();
    static {
        CONNECTOR_MODEL.add(new ItemDisplayModelTemplate(Material.GRAY_CONCRETE, ItemDisplay.ItemDisplayTransform.NONE, 0, new Vector3f(0f, 0.5f, 0.4375f), new Vector3f(0.625f, 0.625f, 0.125f), 0f, 0f));
    }

    @Override
    protected StructureInstance instantiate(@NotNull BlockLocation location, Direction direction) {
        Model bodyModel = BASE_MODEL.build(location.getBlock().getLocation().add(.5, 0, .5), direction);
        StructureComponentDef bodyComponent = new StructureComponentDef("body", new Vector3f(0, 0, 0), bodyModel);

        Model connectorModel = CONNECTOR_MODEL.build(location.getBlock().getLocation().add(.5, 0, .5), direction);
        StructureComponentDef powerConnectorComponent = new StructureComponentDef("power_connector", new Vector3f(0, 0, 0), connectorModel);

        StructureConnectorDef powerConnector = new StructureConnectorDef("power_connector", Connector.Matter.ENERGY, Connector.FlowDirection.IN, direction.getOpposite().mask());

        StructureComponentDef itemComponent = new StructureComponentDef("item_connector", new Vector3f(0, 0, 0), null);
        StructureConnectorDef itemConnector = new StructureConnectorDef("item_connector", Connector.Matter.ITEM, Connector.FlowDirection.OUT, Direction.DOWN.mask());

        StructureBodyDef body = new StructureBodyDef(
          new StructureComponentDef[]{ bodyComponent, powerConnectorComponent, itemComponent },
          new StructureConnectorDef[]{ powerConnector, itemConnector }
        );

        System.out.println();
        return new BlockBreakerInstance(location, body, direction);
    }

}
