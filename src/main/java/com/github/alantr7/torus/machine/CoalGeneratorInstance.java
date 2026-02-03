package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.TorusPlugin;
import com.github.alantr7.torus.exception.MissingDataException;
import com.github.alantr7.torus.structure.inspection.InspectableDataContainer;
import com.github.alantr7.torus.structure.socket.EnergySocket;
import com.github.alantr7.torus.structure.socket.ItemSocket;
import com.github.alantr7.torus.world.Direction;
import com.github.alantr7.torus.utils.MathUtils;
import com.github.alantr7.torus.structure.EnergyContainer;
import com.github.alantr7.torus.structure.LoadContext;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.structure.Structures;
import com.github.alantr7.torus.structure.builder.StructureBodyDef;
import com.github.alantr7.torus.structure.socket.Socket;
import com.github.alantr7.torus.structure.data.Data;
import com.github.alantr7.torus.world.BlockLocation;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class CoalGeneratorInstance extends StructureInstance implements EnergyContainer {

    @Getter
    protected Data<Integer> storedEnergy = dataContainer.persist("energy", Data.Type.INT, 0);

    protected ItemSocket inputSocket;
    protected EnergySocket outputSocket;

    protected int remainingBurnTicks;
    protected float[] chimneyPosition;

    public CoalGeneratorInstance(BlockLocation location, StructureBodyDef bodyDef, Direction direction) {
        super(Structures.COAL_GENERATOR, location, bodyDef, direction);
    }

    CoalGeneratorInstance(LoadContext context) {
        super(context);
    }

    @Override
    protected void setup() throws MissingDataException {
        inputSocket = requireSocket("item_connector", ItemSocket.class);
        outputSocket = requireSocket("power_connector", EnergySocket.class);
        outputSocket.maximumOutput = CoalGenerator.ENERGY_MAXIMUM_OUTPUT;

        chimneyPosition = MathUtils.rotateVectors(new float[] { 1.125f - .5f, 1.8f, 0.8125f - .5f }, direction.rotH, direction.rotV);
    }

    @Override
    public void tick(boolean isVirtual) {
        if (remainingBurnTicks == 0) {
            List<ItemStack> fuel = inputSocket.consumeItems(CoalGenerator.INPUT_CRITERIA, 1, true);
            if (fuel.isEmpty())
                return;

            remainingBurnTicks = 30;
        }

        if (storedEnergy.get() < CoalGenerator.ENERGY_CAPACITY) {
            supplyEnergy(CoalGenerator.ENERGY_PRODUCTION);
            remainingBurnTicks--;

            if (!isVirtual) {
                location.world.getBukkit().spawnParticle(Particle.CAMPFIRE_SIGNAL_SMOKE, location.getBlock().getLocation().add(chimneyPosition[0], chimneyPosition[1], chimneyPosition[2]).add(.5, 1, .5), 0, 0, .075f, 0);
                Bukkit.getScheduler().runTaskLater(TorusPlugin.getInstance(), () -> {
                    location.world.getBukkit().spawnParticle(Particle.CAMPFIRE_SIGNAL_SMOKE, location.getBlock().getLocation().add(chimneyPosition[0], chimneyPosition[1], chimneyPosition[2]).add(.5, 1, .5), 0, 0, .075f, 0);
                }, 10L);
            }
        }
    }

    @Override
    public int getEnergyCapacity() {
        return CoalGenerator.ENERGY_CAPACITY;
    }

    @Override
    public InspectableDataContainer setupInspectableData() {
        return new InspectableDataContainer((byte) 1)
          .property("RF", InspectableDataContainer.TEMPLATE_RF.apply(this));
    }

}
