package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.TorusPlugin;
import com.github.alantr7.torus.structure.Structure;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.structure.builder.StructureBodyDef;
import com.github.alantr7.torus.structure.builder.StructureComponentDef;
import com.github.alantr7.torus.structure.builder.StructureSocketDef;
import com.github.alantr7.torus.structure.socket.Socket;
import com.github.alantr7.torus.structure.state.State;
import com.github.alantr7.torus.structure.state.StateType;
import com.github.alantr7.torus.utils.MathUtils;
import com.github.alantr7.torus.world.BlockLocation;
import com.github.alantr7.torus.world.Direction;
import com.github.alantr7.torus.world.Pitch;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class JunctionBox extends Structure {

    public static final State<Boolean> STATE_RIGHT  = new State<>("right",  StateType.BOOLEAN, false);
    public static final State<Boolean> STATE_BACK   = new State<>("back",   StateType.BOOLEAN, false);
    public static final State<Boolean> STATE_LEFT   = new State<>("left",   StateType.BOOLEAN, false);
    public static final State<Boolean> STATE_UP     = new State<>("up",     StateType.BOOLEAN, false);
    public static final State<Boolean> STATE_DOWN   = new State<>("down",   StateType.BOOLEAN, false);

    public JunctionBox() {
        super(TorusPlugin.DEFAULT_ADDON, "junction_box", "Junction Box", JunctionBoxInstance.class);
        isHeavy = false;
        registerState(STATE_RIGHT);
        registerState(STATE_BACK);
        registerState(STATE_LEFT);
        registerState(STATE_UP);
        registerState(STATE_DOWN);
    }

    @Override
    protected StructureInstance instantiate(@NotNull BlockLocation location, Direction direction, Pitch pitch) {
        return new JunctionBoxInstance(location, new StructureBodyDef(new StructureComponentDef[]{
          new StructureComponentDef("base", new Vector3f(), new StructureSocketDef(
            Socket.Medium.ENERGY, Socket.FlowDirection.ALL, MathUtils.setFlag(0b111111, direction.mask(), false)
          ))
        }), direction, pitch);
    }

}
