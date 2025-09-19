package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.math.BlockLocation;
import com.github.alantr7.torus.math.Direction;
import com.github.alantr7.torus.math.MathUtils;
import com.github.alantr7.torus.structure.Structure;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.structure.builder.StructureBodyDef;
import com.github.alantr7.torus.structure.builder.StructureComponentDef;
import com.github.alantr7.torus.structure.builder.StructureConnectorDef;
import com.github.alantr7.torus.structure.component.Connector;
import com.github.alantr7.torus.structure.display.ItemDisplayModelTemplate;
import com.github.alantr7.torus.structure.display.ModelTemplate;
import org.bukkit.Material;
import org.bukkit.entity.ItemDisplay;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class InventoryInterface extends Structure {

    static ModelTemplate CONNECTOR_MODEL = new ModelTemplate();
    static {
        CONNECTOR_MODEL.add(new ItemDisplayModelTemplate(Material.GRAY_CONCRETE, ItemDisplay.ItemDisplayTransform.NONE, 0, new Vector3f(0f, 0.5f, -0.5f + 0.0625f), new Vector3f(0.625f, 0.625f, 0.125f), 0f, 0f));
    }

    public InventoryInterface() {
        super("torus:inventory_interface", InventoryInterfaceInstance.class);
    }

    @Override
    public StructureInstance place(BlockLocation location, Direction direction) {
        InventoryInterfaceInstance instance = (InventoryInterfaceInstance) super.place(location, direction);
        instance.updateConnections();

        return instance;
    }

    @Override
    protected StructureInstance instantiate(@NotNull BlockLocation location, Direction direction) {
        StructureComponentDef connectorDef = new StructureComponentDef(
          "connector",
          new Vector3f(),
          CONNECTOR_MODEL.build(location.getBlock().getLocation().add(.5, 0, .5), direction)
        );

        StructureComponentDef cableDef = new StructureComponentDef(
          "cable",
          new Vector3f(),
          ModelTemplate.EMPTY.build(location.getBlock().getLocation().add(.5, 0, .5), direction)
        );

        StructureBodyDef body = new StructureBodyDef(
          new StructureComponentDef[]{connectorDef, cableDef},
          new StructureConnectorDef[]{
            new StructureConnectorDef("connector", Connector.Matter.ITEM, Connector.FlowDirection.IN, MathUtils.setFlag(0b111111, direction.mask(), false))
          }
        );

        return new InventoryInterfaceInstance(location, body, direction, Connector.FlowDirection.IN);
    }

}
