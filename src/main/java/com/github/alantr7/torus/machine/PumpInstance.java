package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.world.Fluid;
import com.github.alantr7.torus.world.BlockLocation;
import com.github.alantr7.torus.math.Direction;
import com.github.alantr7.torus.structure.*;
import com.github.alantr7.torus.structure.builder.StructureBodyDef;
import com.github.alantr7.torus.structure.component.Connector;
import com.github.alantr7.torus.structure.data.Data;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Levelled;
import org.jetbrains.annotations.Nullable;

public class PumpInstance extends StructureInstance implements EnergyContainer, FluidContainer {

    protected Data<Integer> fluid = dataContainer.persist("fluid", Data.Type.INT, -1);

    protected Data<Integer> amount = dataContainer.persist("amount", Data.Type.INT, 0);

    @Getter
    protected Data<Integer> storedEnergy = dataContainer.persist("energy", Data.Type.INT, 0);

    protected Connector energyConnector;

    @Getter
    protected int energyCapacity = 500;

    PumpInstance(LoadContext context) {
        super(context);
    }

    public PumpInstance(BlockLocation location, StructureBodyDef bodyDef, Direction direction) {
        super(Structures.PUMP, location, bodyDef, direction);
    }

    @Override
    protected void setup() {
        energyConnector = getConnector("power_connector");
        energyConnector.maximumInput = 25;
    }

    @Override
    public void tick() {
        energyConnector.maintainEnergy(this);
        if (storedEnergy.get() < 50 || amount.get() >= 1000)
            return;

        Block block = location.getRelative(0, -1, 0).getBlock();
        Material liquid = block.getType();
        if (liquid != Material.WATER && liquid != Material.LAVA)
            return;

        Levelled levelled = (Levelled) block.getBlockData();
        if (levelled.getLevel() != 0)
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
