package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.TorusPlugin;
import com.github.alantr7.torus.structure.StructureFlag;
import com.github.alantr7.torus.structure.builder.StructureSocketDef;
import com.github.alantr7.torus.structure.state.State;
import com.github.alantr7.torus.structure.state.StateType;
import com.github.alantr7.torus.world.BlockLocation;
import com.github.alantr7.torus.world.Direction;
import com.github.alantr7.torus.structure.Structure;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.structure.builder.StructureBodyDef;
import com.github.alantr7.torus.structure.builder.StructurePartDef;
import com.github.alantr7.torus.structure.socket.Socket;
import com.github.alantr7.torus.world.Pitch;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import static com.github.alantr7.torus.lang.Localization.translatable;

public class EnergyCable extends Structure {

    public static final State<Boolean> STATE_NORTH  = new State<>("north",  StateType.BOOLEAN, false);
    public static final State<Boolean> STATE_EAST   = new State<>("east",   StateType.BOOLEAN, false);
    public static final State<Boolean> STATE_SOUTH  = new State<>("south",  StateType.BOOLEAN, false);
    public static final State<Boolean> STATE_WEST   = new State<>("west",   StateType.BOOLEAN, false);
    public static final State<Boolean> STATE_UP     = new State<>("up",     StateType.BOOLEAN, false);
    public static final State<Boolean> STATE_DOWN   = new State<>("down",   StateType.BOOLEAN, false);

    public EnergyCable() {
        super(TorusPlugin.DEFAULT_ADDON, "energy_cable", translatable("structure.energy_cable.name"), CableInstance.class);
        setFlags(StructureFlag.INTERACTABLE | StructureFlag.COLLIDABLE);
        registerState(STATE_NORTH);
        registerState(STATE_EAST);
        registerState(STATE_SOUTH);
        registerState(STATE_WEST);
        registerState(STATE_UP);
        registerState(STATE_DOWN);
    }

    @Override
    protected StructureInstance instantiate(@NotNull BlockLocation location, Direction direction, Pitch pitch) {
        StructurePartDef base = new StructurePartDef("base", new Vector3f(), new StructureSocketDef(
          Socket.Medium.ENERGY, Socket.FlowDirection.ALL, 0b111111
        ));
        return new CableInstance(location, new StructureBodyDef(new StructurePartDef[]{base}), Socket.Medium.ENERGY);
    }

    public static State<Boolean> getStateFromDirection(Direction direction) {
        return switch (direction) {
            case NORTH  -> STATE_NORTH;
            case EAST   -> STATE_EAST;
            case SOUTH  -> STATE_SOUTH;
            case WEST   -> STATE_WEST;
            case UP     -> STATE_UP;
            case DOWN   -> STATE_DOWN;
        };
    }

}
