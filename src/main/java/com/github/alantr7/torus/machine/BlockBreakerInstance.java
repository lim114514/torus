package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.world.BlockLocation;
import com.github.alantr7.torus.math.Direction;
import com.github.alantr7.torus.structure.LoadContext;
import com.github.alantr7.torus.structure.builder.StructureBodyDef;
import com.github.alantr7.torus.structure.data.Data;
import com.github.alantr7.torus.structure.EnergyContainer;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.structure.inventory.CustomStructureInventory;
import com.github.alantr7.torus.structure.inventory.StructureInventory;
import com.github.alantr7.torus.structure.Structures;
import com.github.alantr7.torus.structure.component.Connector;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class BlockBreakerInstance extends StructureInstance implements EnergyContainer {

    protected double rfCapacity = 50;

    protected Data<Integer> storedEnergy = dataContainer.persist("energy", Data.Type.INT, 0);

    protected Connector powerConnector;

    protected Connector itemConnector;

    protected StructureInventory inventory;

    public static final double RF_COST = 25;

    public BlockBreakerInstance(BlockLocation location, StructureBodyDef bodyDef, Direction direction) {
        super(Structures.BLOCK_BREAKER, location, bodyDef, direction);
    }

    BlockBreakerInstance(LoadContext context) {
        super(context);
    }

    @Override
    protected void setup() {
        powerConnector = getConnector("power_connector");
        itemConnector = getConnector("item_connector");
        inventory = new CustomStructureInventory(1);
        itemConnector.linkedInventory = inventory;
    }

    @Override
    public void tick() {
        if (storedEnergy.get() != rfCapacity) {
            // TODO: Perhaps optimize somehow?
            powerConnector.updateConnections();
            if (!powerConnector.connectedStructures.isEmpty()) {
                double taken = powerConnector.consumeEnergy(Math.min(powerConnector.getMaximumInput(), rfCapacity - storedEnergy.get()));
                supplyEnergy(taken);
            }
        }

        if (!hasSufficientEnergy(RF_COST) || inventory.getItems()[0] != null) {
            return;
        }

        if (!location.getRelative(direction).getBlock().getType().isAir()) {
            inventory.addItem(new ItemStack(location.getRelative(direction).getBlock().getType()));
            location.getRelative(direction).getBlock().setType(Material.AIR);
            consumeEnergy(RF_COST);
        }
    }

    @Override
    public double getEnergyCapacity() {
        return rfCapacity;
    }

    @Override
    public void setEnergyCapacity(double capacity) {
        rfCapacity = capacity;
    }

    @Override
    public double getStoredEnergy() {
        return (double) storedEnergy.get();
    }

    @Override
    public void setStoredEnergy(double energy) {
        storedEnergy.update((int) energy);
    }

}
