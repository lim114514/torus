package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.TorusPlugin;
import com.github.alantr7.torus.structure.Structure;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.structure.builder.StructureBodyDef;
import com.github.alantr7.torus.structure.builder.StructureComponentDef;
import com.github.alantr7.torus.structure.builder.StructureSocketDef;
import com.github.alantr7.torus.structure.component.Socket;
import com.github.alantr7.torus.world.BlockLocation;
import com.github.alantr7.torus.world.Direction;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class WireConnector extends Structure {

    public WireConnector() {
        super(TorusPlugin.DEFAULT_ADDON, "wire_connector", "Wire Connector", WireConnectorInstance.class);
        isInteractable = true;
        isHeavy = false;
        isOmnidirectional = true;
    }

    @Override
    protected StructureInstance instantiate(@NotNull BlockLocation location, Direction direction) {
        return new WireConnectorInstance(WireConnectorInstance.Type.CONNECTOR, location, new StructureBodyDef(new StructureComponentDef[]{
          new StructureComponentDef("base", new Vector3f(), new StructureSocketDef(Socket.Medium.ENERGY, Socket.FlowDirection.ALL, direction.getOpposite().mask()))
        }), direction);
    }

}
