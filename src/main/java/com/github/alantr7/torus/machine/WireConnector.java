package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.TorusPlugin;
import com.github.alantr7.torus.structure.Structure;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.structure.builder.StructureBodyDef;
import com.github.alantr7.torus.structure.builder.StructurePartDef;
import com.github.alantr7.torus.structure.builder.StructureSocketDef;
import com.github.alantr7.torus.structure.socket.Socket;
import com.github.alantr7.torus.world.BlockLocation;
import com.github.alantr7.torus.world.Direction;
import com.github.alantr7.torus.world.Pitch;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import static com.github.alantr7.torus.lang.Localization.translatable;

public class WireConnector extends Structure {

    public WireConnector() {
        super(TorusPlugin.DEFAULT_ADDON, "wire_connector", translatable("structure.wire_connector.name"), WireConnectorInstance.class);
        isTickable = false;
        isInteractable = true;
        isHeavy = false;
        isOmnidirectional = true;
    }

    @Override
    protected StructureInstance instantiate(@NotNull BlockLocation location, Direction direction, Pitch pitch) {
        return new WireConnectorInstance(WireConnectorInstance.Type.CONNECTOR, location, new StructureBodyDef(new StructurePartDef[]{
          new StructurePartDef("base", new Vector3f(), new StructureSocketDef(Socket.Medium.ENERGY, Socket.FlowDirection.ALL, direction.getOpposite().mask()))
        }), direction, pitch);
    }

}
