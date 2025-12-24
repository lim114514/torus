package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.model.de_provider.DisplayEntitiesPartModel;
import com.github.alantr7.torus.world.Fluid;
import com.github.alantr7.torus.world.BlockLocation;
import com.github.alantr7.torus.world.Direction;
import com.github.alantr7.torus.structure.*;
import com.github.alantr7.torus.structure.builder.StructureBodyDef;
import com.github.alantr7.torus.structure.component.Socket;
import com.github.alantr7.torus.structure.data.Data;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Levelled;
import org.bukkit.entity.Display;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.Nullable;

public class PumpInstance extends StructureInstance implements EnergyContainer, FluidContainer {

    protected Data<Integer> fluid = dataContainer.persist("fluid", Data.Type.INT, -1);

    protected Data<Integer> amount = dataContainer.persist("amount", Data.Type.INT, 0);

    protected Data<Integer> length = dataContainer.persist("length", Data.Type.INT, 0);

    @Getter
    protected Data<Integer> storedEnergy = dataContainer.persist("energy", Data.Type.INT, 0);

    protected Socket energySocket;

    PumpInstance(LoadContext context) {
        super(context);
    }

    public PumpInstance(BlockLocation location, StructureBodyDef bodyDef, Direction direction) {
        super(Structures.PUMP, location, bodyDef, direction);
    }

    @Override
    protected void setup() {
        energySocket = getSocket("power_connector");
        energySocket.maximumInput = Pump.ENERGY_MAXIMUM_INPUT;
    }

    @Override
    public void handleModelInit() {
        // TODO: Abstraction
        Display pipe = ((DisplayEntitiesPartModel) model.getPart("pipe")).entityReferences.getFirst().getEntity();
        pipe.setTeleportDuration(20);
        updateModel();
    }

    private void updateModel() {
        // TODO: Abstraction
        Display pipe = ((DisplayEntitiesPartModel) model.getPart("pipe")).entityReferences.getFirst().getEntity();
        if (pipe != null) {
            Transformation transformation = pipe.getTransformation();
            transformation.getScale().set(.1875f, length.get(), .1875f);
            pipe.setTransformation(transformation);
            pipe.setInterpolationDelay(0);
            pipe.setInterpolationDuration(20);
            pipe.teleport(location.toBukkitCentered().add(0, .2f - length.get() / 2f, 0));
        }
    }

    private int airTicks = 0;

    @Override
    public void tick() {
        energySocket.maintainEnergy(this);
        if (storedEnergy.get() < Pump.ENERGY_CONSUMPTION || amount.get() >= Pump.FLUID_CAPACITY)
            return;

        Block block = location.getRelative(0, -1 - length.get(), 0).getBlock();
        Material liquid = block.getType();

        if (liquid.isAir()) {
            if (airTicks++ == 3) {
                airTicks = 0;
                if (length.get() < Pump.MAXIMUM_PIPE_LENGTH) {
                    length.update(length.get() + 1);
                    updateModel();

                    return;
                }
            }
            return;
        }

        airTicks = 0;

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

        consumeEnergy(Pump.ENERGY_CONSUMPTION);

        block.setType(Material.AIR);
        amount.update(amount.get() + 1000);
    }

    @Override
    public int getEnergyCapacity() {
        return Pump.ENERGY_CAPACITY;
    }

    @Override
    public @Nullable Fluid getFluid() {
        return fluid.get() == -1 ? null : Fluid.values()[fluid.get()];
    }

    @Override
    public int getFluidCapacity() {
        return Pump.FLUID_CAPACITY;
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
