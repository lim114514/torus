package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.exception.SetupException;
import com.github.alantr7.torus.structure.LoadContext;
import com.github.alantr7.torus.structure.Structure;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.structure.Structures;
import com.github.alantr7.torus.structure.builder.StructureBodyDef;
import com.github.alantr7.torus.world.BlockLocation;
import com.github.alantr7.torus.world.Direction;

public class PowerPoleInstance extends StructureInstance {

    PowerPoleInstance(LoadContext context) {
        super(context);
    }

    public PowerPoleInstance(BlockLocation location, StructureBodyDef bodyDef, Direction direction) {
        super(Structures.POWER_POLE, location, bodyDef, direction);
    }

    @Override
    public void tick() {

    }

    @Override
    protected void setup() throws SetupException {

    }
}
