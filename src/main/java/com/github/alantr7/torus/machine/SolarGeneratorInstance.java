package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.math.BlockLocation;
import com.github.alantr7.torus.math.Direction;
import com.github.alantr7.torus.structure.LoadContext;
import com.github.alantr7.torus.structure.builder.StructureBodyDef;
import com.github.alantr7.torus.structure.EnergyContainer;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.structure.Structures;
import com.github.alantr7.torus.structure.data.Data;
import lombok.Getter;
import lombok.Setter;

public class SolarGeneratorInstance extends StructureInstance implements EnergyContainer {

    @Getter @Setter
    double energyCapacity = 2000;

    Data<Integer> storedEnergy;

    public SolarGeneratorInstance(BlockLocation location, StructureBodyDef bodyDef, Direction direction) {
        super(Structures.SOLAR_GENERATOR, location, bodyDef, direction);
    }

    @Override
    protected void setup() {
        storedEnergy = dataContainer.persist("capacity", Data.Type.INT, 0);
    }

    @Override
    public void tick() {
        if (storedEnergy.get() < energyCapacity) {
            storedEnergy.update(storedEnergy.get() + 35);
        }
    }

    public double getStoredEnergy() {
        return storedEnergy.get();
    }

    public void setStoredEnergy(double energy) {
        storedEnergy.update((int) energy);
    }

}
