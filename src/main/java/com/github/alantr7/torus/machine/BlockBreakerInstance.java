package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.math.BlockLocation;
import com.github.alantr7.torus.math.Direction;
import com.github.alantr7.torus.structure.LoadContext;
import com.github.alantr7.torus.structure.builder.StructureBodyDef;
import com.github.alantr7.torus.structure.display.ItemDisplayModelTemplate;
import com.github.alantr7.torus.structure.display.ModelTemplate;
import com.github.alantr7.torus.structure.EnergyContainer;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.structure.inventory.CustomStructureInventory;
import com.github.alantr7.torus.structure.inventory.StructureInventory;
import com.github.alantr7.torus.structure.Structures;
import com.github.alantr7.torus.structure.component.Connector;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.joml.Vector3f;

public class BlockBreakerInstance extends StructureInstance implements EnergyContainer {

    protected double rfCapacity = 50;

    @Getter
    protected double storedEnergy;

    protected Connector powerConnector;

    protected Connector itemConnector;

    protected StructureInventory inventory;

    public static final double RF_COST = 25;

    static ModelTemplate BASE_MODEL = new ModelTemplate();
    static {
        BASE_MODEL.add(new ItemDisplayModelTemplate(Material.STICKY_PISTON, ItemDisplay.ItemDisplayTransform.NONE, 0, new Vector3f(0f, 0.5f, 0.0f), new Vector3f(1f, 0.75f, 1f), 180f, 90f));
        BASE_MODEL.add(new ItemDisplayModelTemplate(Material.DISPENSER, ItemDisplay.ItemDisplayTransform.NONE, 0, new Vector3f(0f, 0.5f, -0.5f + 0.0625f), new Vector3f(0.75f, 0.75f, 0.125f), 180f, 0f));
    }

    static ModelTemplate CONNECTOR_MODEL = new ModelTemplate();
    static {
        CONNECTOR_MODEL.add(new ItemDisplayModelTemplate(Material.GRAY_CONCRETE, ItemDisplay.ItemDisplayTransform.NONE, 0, new Vector3f(0f, 0.5f, 0.4375f), new Vector3f(0.625f, 0.625f, 0.125f), 0f, 0f));
    }

    public BlockBreakerInstance(BlockLocation location, StructureBodyDef bodyDef, Direction direction) {
        super(Structures.BLOCK_BREAKER, location, bodyDef, direction);
    }

    BlockBreakerInstance(LoadContext context) {
        super(context);
    }

    @Override
    protected void setup() {
        powerConnector = getConnector("power_connector");
        itemConnector = getConnector("item_container");
        inventory = new CustomStructureInventory(1);
    }

    @Override
    public void tick() {
        if (storedEnergy != rfCapacity) {
            // TODO: Perhaps optimize somehow?
            powerConnector.updateConnections();
            if (!powerConnector.connectedStructures.isEmpty()) {
                double taken = powerConnector.consumeEnergy(Math.min(powerConnector.getMaximumInput(), rfCapacity - storedEnergy));
                supplyEnergy(taken);
            }
        }

        if (!hasSufficientEnergy(RF_COST)) {
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
    public void setStoredEnergy(double energy) {
        storedEnergy = energy;
    }

}
