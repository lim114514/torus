package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.Fluid;
import com.github.alantr7.torus.world.BlockLocation;
import com.github.alantr7.torus.math.Direction;
import com.github.alantr7.torus.structure.*;
import com.github.alantr7.torus.structure.builder.StructureBodyDef;
import com.github.alantr7.torus.structure.component.Connector;
import com.github.alantr7.torus.structure.data.Data;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

public class PumpInstance extends StructureInstance implements EnergyContainer, FluidContainer {

    protected Data<Integer> fluid = dataContainer.persist("fluid", Data.Type.INT, -1);

    protected Data<Integer> amount = dataContainer.persist("amount", Data.Type.INT, 0);

    protected Data<Integer> energy = dataContainer.persist("energy", Data.Type.INT, 0);

    protected Connector energyConnector;

    @Getter @Setter
    protected double energyCapacity = 500;

    PumpInstance(LoadContext context) {
        super(context);
    }

    public PumpInstance(BlockLocation location, StructureBodyDef bodyDef, Direction direction) {
        super(Structures.PUMP, location, bodyDef, direction);
    }

    @Override
    protected void setup() {
        energyConnector = getConnector("power_connector");
    }

    @Override
    public void tick() {
        if (energy.get() < energyCapacity) {
            energyConnector.updateConnections();
            supplyEnergy(energyConnector.consumeEnergy(Math.min(energyCapacity - energy.get(), 25)));
        }

        if (energy.get() < 50 || amount.get() >= 1000)
            return;

        Material liquid = location.getRelative(0, -1, 0).getBlock().getType();
        if (liquid != Material.WATER && liquid != Material.LAVA)
            return;

        Fluid fluid = liquid == Material.WATER ? Fluid.WATER : Fluid.LAVA;
        if (getStoredFluid() == 0 || this.fluid.get() == -1) {
            this.fluid.update(fluid.ordinal());
        }

        if (this.fluid.get() != fluid.ordinal())
            return;

        consumeEnergy(50);

        location.getRelative(0, -1, 0).getBlock().setType(Material.AIR);
        amount.update(amount.get() + 1000);
    }

    @Override
    public double getStoredEnergy() {
        return energy.get();
    }

    @Override
    public void setStoredEnergy(double energy) {
        this.energy.update((int) energy);
    }

    @Override
    public @Nullable Fluid getFluid() {
        return fluid.get() == -1 ? null : Fluid.values()[fluid.get()];
    }

    @Override
    public int getFluidCapacity() {
        return 1000;
    }

    @Override
    public int getStoredFluid() {
        return amount.get();
    }

    @Override
    public void setStoredFluid(int fluid) {
        amount.update(fluid);
    }

}
