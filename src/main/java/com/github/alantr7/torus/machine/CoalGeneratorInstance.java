package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.TorusPlugin;
import com.github.alantr7.torus.world.Direction;
import com.github.alantr7.torus.math.MathUtils;
import com.github.alantr7.torus.structure.EnergyContainer;
import com.github.alantr7.torus.structure.LoadContext;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.structure.Structures;
import com.github.alantr7.torus.structure.builder.StructureBodyDef;
import com.github.alantr7.torus.structure.component.Connector;
import com.github.alantr7.torus.structure.data.Data;
import com.github.alantr7.torus.world.BlockLocation;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class CoalGeneratorInstance extends StructureInstance implements EnergyContainer {

    @Getter
    protected int energyCapacity = 18_000;

    @Getter
    protected Data<Integer> storedEnergy = dataContainer.persist("energy", Data.Type.INT, 0);

    protected Connector inputConnector;
    protected Connector outputConnector;

    protected int remainingBurnTicks;
    protected float[] chimneyPosition;

    public CoalGeneratorInstance(BlockLocation location, StructureBodyDef bodyDef, Direction direction) {
        super(Structures.COAL_GENERATOR, location, bodyDef, direction);
    }

    CoalGeneratorInstance(LoadContext context) {
        super(context);
    }

    @Override
    protected void setup() {
        inputConnector = getConnector("item_connector");
        outputConnector = getConnector("power_connector");
        outputConnector.maximumOutput = 500;

        chimneyPosition = MathUtils.rotateVectors(new float[] { 1.125f - .5f, 1.8f, 0.8125f - .5f }, direction.rotH, direction.rotV);
    }

    @Override
    public void tick() {
        if (remainingBurnTicks == 0) {
            inputConnector.updateNetwork();
            List<ItemStack> fuel = inputConnector.consumeItems(CoalGenerator.INPUT_CRITERIA, 1, true);
            if (fuel.isEmpty())
                return;

            remainingBurnTicks = 30;
        }

        if (storedEnergy.get() < energyCapacity) {
            storedEnergy.update(Math.min(storedEnergy.get() + 300, energyCapacity));
            remainingBurnTicks--;

            location.world.getBukkit().spawnParticle(Particle.CAMPFIRE_SIGNAL_SMOKE, location.getBlock().getLocation().add(chimneyPosition[0], chimneyPosition[1], chimneyPosition[2]).add(.5, 1, .5), 0, 0, .075f, 0);
            Bukkit.getScheduler().runTaskLater(TorusPlugin.getInstance(), () -> {
                location.world.getBukkit().spawnParticle(Particle.CAMPFIRE_SIGNAL_SMOKE, location.getBlock().getLocation().add(chimneyPosition[0], chimneyPosition[1], chimneyPosition[2]).add(.5, 1, .5), 0, 0, .075f, 0);
            }, 10L);
        }
    }

}
