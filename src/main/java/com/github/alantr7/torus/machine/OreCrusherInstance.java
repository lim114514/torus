package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.math.Direction;
import com.github.alantr7.torus.structure.*;
import com.github.alantr7.torus.structure.builder.StructureBodyDef;
import com.github.alantr7.torus.structure.component.Connector;
import com.github.alantr7.torus.structure.component.StructureComponent;
import com.github.alantr7.torus.structure.data.Data;
import com.github.alantr7.torus.structure.inventory.CustomStructureInventory;
import com.github.alantr7.torus.world.BlockLocation;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.List;

public class OreCrusherInstance extends StructureInstance implements Inspectable, EnergyContainer {

    protected StructureComponent leftWheel, rightWheel;
    protected Connector energyConnector, itemInConnector;

    protected CustomStructureInventory itemInBuffer = new CustomStructureInventory(1);
    protected CustomStructureInventory itemOutBuffer = new CustomStructureInventory(1);

    protected int processedTicks;

    protected Data<Integer> storedEnergy = dataContainer.persist("energy", Data.Type.INT, 0);

    public static final int PROCESS_DURATION = 5;

    OreCrusherInstance(LoadContext context) {
        super(context);
    }

    public OreCrusherInstance(BlockLocation location, StructureBodyDef bodyDef, Direction direction) {
        super(Structures.ORE_CRUSHER, location, bodyDef, direction);
    }

    @Override
    public void tick() {
        if (storedEnergy.get() < getEnergyCapacity()) {
            energyConnector.updateConnections();
            supplyEnergy(energyConnector.consumeEnergy(Math.min(getEnergyCapacity() - storedEnergy.get(), 500)));
        }
        if (itemInBuffer.getItems()[0] != null) {
            if (!hasSufficientEnergy(OreCrusher.ENERGY_CONSUMPTION_PER_TICK)) {
                return;
            }

            processedTicks++;
            updateModel();

            consumeEnergy(OreCrusher.ENERGY_CONSUMPTION_PER_TICK);
        } else {
            itemInConnector.updateConnections();

            List<ItemStack> items = itemInConnector.consumeItems(OreCrusher.INPUT_CRITERIA, 1, true);
            if (!items.isEmpty()) {
                itemInBuffer.addItem(items.getFirst());
            }
        }
        if (processedTicks >= PROCESS_DURATION) {
            itemOutBuffer.addItem(new ItemStack(Material.ANDESITE));
            itemInBuffer.getItems()[0] = null;
            processedTicks = 0;
        }
    }

    private void updateModel() {
        leftWheel.getModel().entities.forEach(display -> {
            Transformation transformation = display.getTransformation();
            display.setTransformation(new Transformation(new Vector3f(), new AxisAngle4f(location.world.getBukkit().getTime() * 0.9f, 0, 0, 1f), transformation.getScale(), new AxisAngle4f(transformation.getRightRotation())));
            display.setInterpolationDelay(0);
            display.setInterpolationDuration(20);
        });
        rightWheel.getModel().entities.forEach(display -> {
            Transformation transformation = display.getTransformation();
            display.setTransformation(new Transformation(new Vector3f(), new AxisAngle4f(location.world.getBukkit().getTime(), 0, 0, 1f), transformation.getScale(), new AxisAngle4f(transformation.getRightRotation())));
            display.setInterpolationDelay(0);
            display.setInterpolationDuration(20);
        });
    }

    @Override
    protected void setup() {
        leftWheel = getComponent("wheel_left");
        rightWheel = getComponent("wheel_right");

        (itemInConnector = getConnector("item_connector")).linkedInventory = itemInBuffer;
        energyConnector = getConnector("power_connector");
        getConnector("out_connector").linkedInventory = itemOutBuffer;
    }

    @Override
    public double getEnergyCapacity() {
        return 20_000;
    }

    @Override
    public double getStoredEnergy() {
        return storedEnergy.get();
    }

    @Override
    public void setStoredEnergy(double energy) {
        this.storedEnergy.update((int) energy);
    }

    @Override
    public String getInspectionText(BlockLocation location, Player player) {
        return "Ore Crusher " + String.format("[%d / %d RF] [Progress: %.2f]%%", (int) getStoredEnergy(), (int) getEnergyCapacity(), (float) processedTicks / PROCESS_DURATION * 100);
    }

}
