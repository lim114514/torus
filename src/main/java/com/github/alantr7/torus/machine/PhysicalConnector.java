package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.model.ModelTemplate;
import com.github.alantr7.torus.world.BlockLocation;
import com.github.alantr7.torus.world.Direction;
import com.github.alantr7.torus.math.MathUtils;
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

public class PhysicalConnector extends Structure {

    static PartModelTemplate CONNECTOR_MODEL = new PartModelTemplate("base");
    static {
        CONNECTOR_MODEL.add(new PartModelElementItemDisplayRenderer(Material.GRAY_CONCRETE, new Vector3f(0f, 0.5f, 0.4375f), new Vector3f(0.625f, 0.625f, 0.125f), 0f, 0f));
    }

    public static final ModelTemplate INITIAL_MODEL = new ModelTemplate();
    static {
        INITIAL_MODEL.add(CONNECTOR_MODEL);
        INITIAL_MODEL.add(new PartModelTemplate("cable"));
    }

    public PhysicalConnector() {
        super("torus:connector", PhysicalConnectorInstance.class);
        isHeavy = false;
    }

    @Override
    public ModelTemplate getInitialModel() {
        return INITIAL_MODEL;
    }

    @Override
    public StructureInstance place(BlockLocation location, Direction direction) {
        PhysicalConnectorInstance instance = (PhysicalConnectorInstance) super.place(location, direction);
        instance.updateConnections();

        return instance;
    }

    @Override
    protected StructureInstance instantiate(@NotNull BlockLocation location, Direction direction) {
        StructureBodyDef body = new StructureBodyDef(
          new StructureComponentDef[]{
            new StructureComponentDef("connector", new Vector3f(), new StructureConnectorDef(
              Connector.Matter.ITEM, Connector.FlowDirection.IN, MathUtils.setFlag(0b111111, direction.getOpposite().mask(), false)
            )),
            new StructureComponentDef("cable", new Vector3f()),
          }
        );

        return new PhysicalConnectorInstance(location, body, direction, Connector.FlowDirection.IN);
    }

}
