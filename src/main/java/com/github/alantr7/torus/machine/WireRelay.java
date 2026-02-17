package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.TorusPlugin;
import com.github.alantr7.torus.structure.Structure;
import com.github.alantr7.torus.structure.StructureFlag;
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

public class WireRelay extends Structure {

    public WireRelay() {
        super(TorusPlugin.DEFAULT_ADDON, "wire_relay", translatable("structure.wire_relay.name"), WireConnectorInstance.class);
        setFlags(StructureFlag.COLLIDABLE | StructureFlag.INTERACTABLE);
    }

    @Override
    protected StructureInstance instantiate(@NotNull BlockLocation location, Direction direction, Pitch pitch) {
        return new WireConnectorInstance(WireConnectorInstance.Type.RELAY, location, new StructureBodyDef(new StructurePartDef[]{
          new StructurePartDef("base", new Vector3f(), new StructureSocketDef(Socket.Medium.ENERGY, Socket.FlowDirection.ALL, 0))
        }), direction, pitch);
    }

}
