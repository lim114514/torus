package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.TorusPlugin;
import com.github.alantr7.torus.model.ModelLocation;
import com.github.alantr7.torus.world.BlockLocation;
import com.github.alantr7.torus.world.Direction;
import com.github.alantr7.torus.math.MathUtils;
import com.github.alantr7.torus.structure.Structure;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.structure.builder.StructureBodyDef;
import com.github.alantr7.torus.structure.builder.StructureComponentDef;
import com.github.alantr7.torus.structure.builder.StructureConnectorDef;
import com.github.alantr7.torus.structure.component.Socket;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class PhysicalConnector extends Structure {

    public PhysicalConnector() {
        super(TorusPlugin.DEFAULT_PACK, "connector", "Connector", PhysicalConnectorInstance.class);
        isInteractable = true;
        isHeavy = false;
        portableData.add("flow");
        portableData.add("filter");
        modelLocation = new ModelLocation("torus", "connector");
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
              Socket.Matter.ITEM, Socket.FlowDirection.IN, MathUtils.setFlag(0b111111, direction.getOpposite().mask(), false)
            )),
            new StructureComponentDef("cable", new Vector3f()),
          }
        );

        return new PhysicalConnectorInstance(location, body, direction, Socket.FlowDirection.IN);
    }

}
