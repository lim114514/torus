package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.Fluid;
import com.github.alantr7.torus.math.Direction;
import com.github.alantr7.torus.structure.*;
import com.github.alantr7.torus.structure.builder.StructureBodyDef;
import com.github.alantr7.torus.structure.component.Connector;
import com.github.alantr7.torus.structure.data.Data;
import com.github.alantr7.torus.structure.inventory.CustomStructureInventory;
import com.github.alantr7.torus.structure.inventory.StructureInventory;
import com.github.alantr7.torus.world.BlockLocation;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class OreWasherInstance extends StructureInstance implements EnergyContainer, FluidContainer, Inspectable {

    protected Data<Integer> water = dataContainer.persist("fluid", Data.Type.INT, 0);

    protected Data<Integer> energy = dataContainer.persist("energy", Data.Type.INT, 0);

    protected Connector powerConnector, itemInConnector, waterInConnector, itemOutConnector;

    protected StructureInventory itemInBuffer = new CustomStructureInventory(1);
    protected StructureInventory itemOutBuffer = new CustomStructureInventory(1);

    protected int processedTicks;

    public static final int PROCESS_DURATION = 3;

    public OreWasherInstance(LoadContext context) {
        super(context);
    }

    public OreWasherInstance(BlockLocation location, StructureBodyDef bodyDef, Direction direction) {
        super(Structures.ORE_WASHER, location, bodyDef, direction);
    }

    @Override
    public void tick() {
        if (energy.get() < getEnergyCapacity()) {
            powerConnector.updateConnections();
            if (!powerConnector.connectedStructures.isEmpty()) {
                supplyEnergy((int) powerConnector.consumeEnergy(Math.min(getEnergyCapacity() - energy.get(), 500)));
            }
        }

        if (water.get() < 1_000) {
            waterInConnector.updateConnections();
            if (!waterInConnector.connectedStructures.isEmpty()) {
                int consumed = waterInConnector.consumeFluid(Fluid.WATER, 1000 - water.get());
                supplyFluid(consumed);
            }
        }

        if (itemInBuffer.getItems()[0] != null) {
            if (!hasSufficientEnergy(OreWasher.ENERGY_CONSUMPTION_PER_TICK) || water.get() < 100) {
                return;
            }

            processedTicks++;
            consumeEnergy(OreWasher.ENERGY_CONSUMPTION_PER_TICK);
            consumeFluid(100);
        } else {
            itemInConnector.updateConnections();

            List<ItemStack> items = itemInConnector.consumeItems(null, 1, true);
            if (!items.isEmpty()) {
                itemInBuffer.addItem(items.getFirst());
            }
        }

        if (processedTicks >= PROCESS_DURATION) {
            itemOutBuffer.addItem(new ItemStack(Material.IRON_INGOT, 2));
            itemInBuffer.getItems()[0] = null;
            processedTicks = 0;
        }
    }

    @Override
    protected void setup() {
        powerConnector = getConnector("power_connector");
        itemInConnector = getConnector("item_connector");
        waterInConnector = getConnector("fluid_connector");
        itemOutConnector = getConnector("out_connector");
        itemOutConnector.linkedInventory = itemOutBuffer;
    }

    @Override
    public double getEnergyCapacity() {
        return 10_000;
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
    public String getInspectionText(BlockLocation location, Player player) {
        return String.format("Ore Washer [%d / 1000 mb] [%d / %d RF] [Progress: %.0f%%]", water.get(), (int) getStoredEnergy(), (int) getEnergyCapacity(), (float) processedTicks / PROCESS_DURATION * 100);
    }

    @Override
    public @Nullable Fluid getFluid() {
        return Fluid.WATER;
    }

    @Override
    public int getFluidCapacity() {
        return 1_000;
    }

    @Override
    public int getStoredFluid() {
        return water.get();
    }

    @Override
    public void setStoredFluid(int fluid) {
        water.update(fluid);
    }

}
