package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.exception.SetupException;
import com.github.alantr7.torus.model.de_provider.DisplayEntitiesDefaultAnimations;
import com.github.alantr7.torus.model.de_provider.DisplayEntitiesPartModel;
import com.github.alantr7.torus.structure.EnergyContainer;
import com.github.alantr7.torus.structure.LoadContext;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.structure.Structures;
import com.github.alantr7.torus.structure.builder.StructureBodyDef;
import com.github.alantr7.torus.structure.data.Data;
import com.github.alantr7.torus.structure.inspection.InspectableData;
import com.github.alantr7.torus.world.BlockLocation;
import com.github.alantr7.torus.world.Direction;
import lombok.Getter;

import static com.github.alantr7.torus.machine.Windmill.STATE_ACTIVE;

public class WindmillInstance extends StructureInstance implements EnergyContainer {

    @Getter
    float efficiency;

    @Getter
    public final Data<Integer> storedEnergy = dataContainer.persist("energy", Data.Type.INT, 0);

    WindmillInstance(LoadContext context) {
        super(context);
    }

    public WindmillInstance(BlockLocation location, StructureBodyDef bodyDef, Direction direction) {
        super(Structures.WINDMILL, location, bodyDef, direction);
    }


    @Override
    public void tick() {
        supplyEnergy((int) (efficiency * 75));
    }

    @Override
    protected void setup() throws SetupException {
        getSocket("out_energy").maximumOutput = Windmill.ENERGY_MAXIMUM_OUTPUT;
        efficiency = (float) Math.pow(Math.E, -8f/(location.y / 8f + 8f)) * 1.15505059f;
        state.set(STATE_ACTIVE, efficiency != 0, false);
    }

    @Override
    public InspectableData setupInspectableData() {
        return new InspectableData((byte) 2)
          .property("RF", InspectableData.TEMPLATE_RF.apply(this))
          .property("Eff", () -> (int) (efficiency * 100) + "%");
    }

    @Override
    public int getEnergyCapacity() {
        return Windmill.ENERGY_CAPACITY;
    }

}
