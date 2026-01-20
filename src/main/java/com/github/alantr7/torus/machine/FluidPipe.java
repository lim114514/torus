package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.TorusPlugin;
import com.github.alantr7.torus.structure.builder.StructureSocketDef;
import com.github.alantr7.torus.world.BlockLocation;
import com.github.alantr7.torus.world.Direction;
import com.github.alantr7.torus.structure.Structure;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.structure.builder.StructureBodyDef;
import com.github.alantr7.torus.structure.builder.StructureComponentDef;
import com.github.alantr7.torus.structure.component.Socket;
import com.github.alantr7.torus.world.Pitch;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import static com.github.alantr7.torus.machine.EnergyCable.*;
import static com.github.alantr7.torus.machine.EnergyCable.STATE_DOWN;
import static com.github.alantr7.torus.machine.EnergyCable.STATE_SOUTH;
import static com.github.alantr7.torus.machine.EnergyCable.STATE_UP;
import static com.github.alantr7.torus.machine.EnergyCable.STATE_WEST;

public class FluidPipe extends Structure {

    public FluidPipe() {
        super(TorusPlugin.DEFAULT_ADDON, "fluid_pipe", "Fluid Pipe", CableInstance.class);
        isHeavy = false;
        registerState(STATE_NORTH);
        registerState(STATE_EAST);
        registerState(STATE_SOUTH);
        registerState(STATE_WEST);
        registerState(STATE_UP);
        registerState(STATE_DOWN);
    }

    @Override
    protected StructureInstance instantiate(@NotNull BlockLocation location, Direction direction, Pitch pitch) {
        StructureComponentDef base = new StructureComponentDef("base", new Vector3f(), new StructureSocketDef(
          Socket.Matter.FLUID, Socket.FlowDirection.ALL, 0b111111
        ));
        return new CableInstance(location, new StructureBodyDef(new StructureComponentDef[]{base}), Socket.Matter.FLUID);
    }

}
