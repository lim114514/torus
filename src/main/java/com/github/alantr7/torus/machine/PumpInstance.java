package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.math.BlockLocation;
import com.github.alantr7.torus.math.Direction;
import com.github.alantr7.torus.structure.LoadContext;
import com.github.alantr7.torus.structure.Structure;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.structure.Structures;
import com.github.alantr7.torus.structure.builder.StructureBodyDef;
import org.jetbrains.annotations.NotNull;

public class PumpInstance extends StructureInstance {

    PumpInstance(LoadContext context) {
        super(context);
    }

    public PumpInstance(BlockLocation location, StructureBodyDef bodyDef, Direction direction) {
        super(Structures.PUMP, location, bodyDef, direction);
    }

    @Override
    protected void setup() {

    }

    @Override
    public void tick() {

    }

}
