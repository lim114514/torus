package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.TorusPlugin;
import com.github.alantr7.torus.structure.Structure;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.structure.builder.StructureBodyDef;
import com.github.alantr7.torus.structure.builder.StructureComponentDef;
import com.github.alantr7.torus.structure.builder.StructureSocketDef;
import com.github.alantr7.torus.structure.component.Socket;
import com.github.alantr7.torus.structure.state.State;
import com.github.alantr7.torus.structure.state.StateType;
import com.github.alantr7.torus.world.BlockLocation;
import com.github.alantr7.torus.world.Direction;
import com.github.alantr7.torus.world.Pitch;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class LightBulb extends Structure {

    public static final State<Boolean> STATE_POWERED = new State<>("powered", StateType.BOOLEAN, false);

    public LightBulb() {
        super(TorusPlugin.DEFAULT_ADDON, "light_bulb", "Light Bulb", LightBulbInstance.class);
        hasCollision = false;
        isHeavy = false;
        isOmnidirectional = true;
        registerState(STATE_POWERED);
    }

    @Override
    protected StructureInstance instantiate(@NotNull BlockLocation location, Direction direction, Pitch pitch) {
        return new LightBulbInstance(this, location, new StructureBodyDef(new StructureComponentDef[] {
          new StructureComponentDef("base", new Vector3f(), new StructureSocketDef(
            Socket.Medium.ENERGY, Socket.FlowDirection.IN, direction.getOpposite().mask()
          ))
        }), direction, pitch);
    }

}
