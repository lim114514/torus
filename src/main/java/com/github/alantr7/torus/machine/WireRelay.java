package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.TorusPlugin;
import com.github.alantr7.torus.model.ModelLocation;
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

public class WireRelay extends Structure {

    public WireRelay() {
        super(TorusPlugin.DEFAULT_ADDON, "wire_relay", "Wire Relay", WireConnectorInstance.class);
        isInteractable = true;
        isHeavy = false;
        modelLocation = new ModelLocation("torus", "wire_relay");
    }

    @Override
    protected StructureInstance instantiate(@NotNull BlockLocation location, Direction direction) {
        return new WireConnectorInstance(WireConnectorInstance.Type.RELAY, location, new StructureBodyDef(new StructureComponentDef[]{
          new StructureComponentDef("base", new Vector3f(), new StructureSocketDef(Socket.Matter.ENERGY, Socket.FlowDirection.ALL, 0))
        }), direction);
    }

}
