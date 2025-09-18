package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.math.BlockLocation;
import com.github.alantr7.torus.math.Direction;
import com.github.alantr7.torus.structure.builder.StructureBodyDef;
import com.github.alantr7.torus.structure.EnergyContainer;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.structure.Structures;
import lombok.Getter;
import lombok.Setter;

public class SolarGeneratorInstance extends StructureInstance implements EnergyContainer {

    @Getter @Setter
    double energyCapacity = 2000;

    @Getter @Setter
    double storedEnergy;

    public SolarGeneratorInstance(BlockLocation location, StructureBodyDef bodyDef, Direction direction) {
        super(Structures.SOLAR_GENERATOR, location, bodyDef, direction);
    }

    @Override
    protected void setup() {

    }

    @Override
    public void tick() {
        if (storedEnergy < energyCapacity) {
            storedEnergy += 35;
        }
    }

}
