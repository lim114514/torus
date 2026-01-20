package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.TorusPlugin;
import com.github.alantr7.torus.structure.state.State;
import com.github.alantr7.torus.structure.state.StateType;
import com.github.alantr7.torus.world.BlockLocation;
import com.github.alantr7.torus.world.Direction;
import com.github.alantr7.torus.utils.MathUtils;
import com.github.alantr7.torus.structure.Structure;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.structure.builder.StructureBodyDef;
import com.github.alantr7.torus.structure.builder.StructureComponentDef;
import com.github.alantr7.torus.structure.builder.StructureSocketDef;
import com.github.alantr7.torus.structure.component.Socket;
import com.github.alantr7.torus.world.Pitch;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class PhysicalConnector extends Structure {

    public static final State<Boolean> STATE_FRONT  = new State<>("front",  StateType.BOOLEAN, false);
    public static final State<Boolean> STATE_RIGHT  = new State<>("right",   StateType.BOOLEAN, false);
    public static final State<Boolean> STATE_BACK   = new State<>("back",  StateType.BOOLEAN, false);
    public static final State<Boolean> STATE_LEFT   = new State<>("left",   StateType.BOOLEAN, false);
    public static final State<Boolean> STATE_UP     = new State<>("up",     StateType.BOOLEAN, false);
    public static final State<Boolean> STATE_DOWN   = new State<>("down",   StateType.BOOLEAN, false);

    public PhysicalConnector() {
        super(TorusPlugin.DEFAULT_ADDON, "connector", "Connector", PhysicalConnectorInstance.class);
        isInteractable = true;
        isHeavy = false;
        isOmnidirectional = true;
        registerState(STATE_FRONT);
        registerState(STATE_RIGHT);
        registerState(STATE_BACK);
        registerState(STATE_LEFT);
        registerState(STATE_UP);
        registerState(STATE_DOWN);
        portableData.add("flow");
        portableData.add("filter");
    }

    @Override
    protected StructureInstance instantiate(@NotNull BlockLocation location, Direction direction, Pitch pitch) {
        StructureBodyDef body = new StructureBodyDef(
          new StructureComponentDef[]{
            new StructureComponentDef("connector", new Vector3f(), new StructureSocketDef(
              Socket.Matter.ITEM, Socket.FlowDirection.IN, MathUtils.setFlag(0b111111, direction.getOpposite().mask(), false)
            )),
            new StructureComponentDef("cable", new Vector3f()),
          }
        );

        return new PhysicalConnectorInstance(location, body, direction, pitch, Socket.FlowDirection.IN);
    }

    static State<Boolean> getStateFromDirection(Direction direction) {
        return switch (direction) {
            case NORTH -> STATE_FRONT;
            case EAST -> STATE_RIGHT;
            case SOUTH -> STATE_BACK;
            case WEST -> STATE_LEFT;
            case UP -> STATE_UP;
            case DOWN -> STATE_DOWN;
        };
    }

}
