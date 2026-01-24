package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.structure.inspection.InspectableDataContainer;
import com.github.alantr7.torus.world.BlockLocation;
import com.github.alantr7.torus.world.Direction;
import com.github.alantr7.torus.structure.LoadContext;
import com.github.alantr7.torus.structure.builder.StructureBodyDef;
import com.github.alantr7.torus.structure.EnergyContainer;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.structure.Structures;
import com.github.alantr7.torus.structure.data.Data;
import lombok.Getter;

public class SolarGeneratorInstance extends StructureInstance implements EnergyContainer {

    @Getter
    protected Data<Integer> storedEnergy = dataContainer.persist("energy", Data.Type.INT, 0);

    public SolarGeneratorInstance(BlockLocation location, StructureBodyDef bodyDef, Direction direction) {
        super(Structures.SOLAR_GENERATOR, location, bodyDef, direction);
    }

    SolarGeneratorInstance(LoadContext context) {
        super(context);
    }

    @Override
    protected void setup() {
        getSocket("out_energy").maximumOutput = SolarGenerator.ENERGY_MAXIMUM_OUTPUT;
    }

    @Override
    public void tick(boolean isVirtual) {
        if (storedEnergy.get() < SolarGenerator.ENERGY_CAPACITY) {
            supplyEnergy((int) (SolarGenerator.ENERGY_PRODUCTION * calculateEfficiency()));
        }
    }

    @Override
    public int getEnergyCapacity() {
        return SolarGenerator.ENERGY_CAPACITY;
    }

    @Override
    public InspectableDataContainer setupInspectableData() {
        return new InspectableDataContainer((byte) 2)
          .property("RF", InspectableDataContainer.TEMPLATE_RF.apply(this))
          .property("Eff", () -> (int) (calculateEfficiency() * 100) + "%");
    }

    static float pi_10th = (float) Math.PI / 10f;
    static float pi_20th = (float) Math.PI / 20f;
    static float pi2 = (float) Math.PI * 2;
    static float m = 24000;
    static float l = m / 2;
    private float calculateEfficiency() {
        float maximumEfficiency = !location.getBlock().getWorld().isThundering() ? 1.0f : 0.2f;
        long time = location.world.getBukkit().getTime();

        float amp = (float) (Math.max(-pi_10th, Math.sin(Math.PI * time / l - pi2)) + pi_10th) / 2f * (1f/(1f / 2f + pi_20th));
        return amp * maximumEfficiency;
    }

}
