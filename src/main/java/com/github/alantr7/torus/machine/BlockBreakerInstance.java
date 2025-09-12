package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.math.BlockLocation;
import com.github.alantr7.torus.math.ConnectorLocation;
import com.github.alantr7.torus.math.Direction;
import com.github.alantr7.torus.model.engine.display.ItemDisplayModelTemplate;
import com.github.alantr7.torus.model.engine.display.Model;
import com.github.alantr7.torus.model.engine.display.ModelTemplate;
import com.github.alantr7.torus.structure.EnergyCapacitor;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.structure.Structures;
import com.github.alantr7.torus.structure.component.Connector;
import com.github.alantr7.torus.structure.component.StructureComponent;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.ItemDisplay;
import org.joml.Vector3f;

public class BlockBreakerInstance extends StructureInstance implements EnergyCapacitor {

    protected double rfCapacity = 50;

    @Getter
    protected double storedEnergy;

    protected Connector powerConnector;

    protected Connector itemConnector;

    public static final double RF_COST = 25;

    static ModelTemplate BASE_MODEL = new ModelTemplate();
    static {
        BASE_MODEL.add(new ItemDisplayModelTemplate(Material.STICKY_PISTON, ItemDisplay.ItemDisplayTransform.NONE, 0, new Vector3f(0f, 0.5f, 0.0625f), new Vector3f(1f, 0.625f, 1f), 0f, 90f));
        BASE_MODEL.add(new ItemDisplayModelTemplate(Material.DISPENSER, ItemDisplay.ItemDisplayTransform.NONE, 0, new Vector3f(0f, 0.5f, -0.5f + 0.125f), new Vector3f(0.8125f, 0.8125f, 0.25f), 0f, 0f));
    }

    static ModelTemplate CONNECTOR_MODEL = new ModelTemplate();
    static {
        CONNECTOR_MODEL.add(new ItemDisplayModelTemplate(Material.GRAY_CONCRETE, ItemDisplay.ItemDisplayTransform.NONE, 0, new Vector3f(0f, 0.5f, 0.4375f), new Vector3f(0.625f, 0.625f, 0.125f), 0f, 0f));
    }

    public BlockBreakerInstance(BlockLocation location, Direction direction) {
        super(Structures.BLOCK_BREAKER, location, direction);
    }

    @Override
    public void create() {
        Model bodyModel = BASE_MODEL.build(location.getBlock().getLocation().add(.5, 0, .5), direction);
        components.put("body", new StructureComponent(this, new BlockLocation(location.world, 0, 0, 0), direction, bodyModel));

        Model connectorModel = CONNECTOR_MODEL.build(location.getBlock().getLocation().add(.5, 0, .5), direction);
        components.put("power_connector", new StructureComponent(this, new BlockLocation(location.world, 0, 0, 0), direction, connectorModel));

        powerConnector = new Connector(components.get("power_connector"), direction.getOpposite().mask(), Connector.FlowDirection.IN, Connector.Matter.ENERGY);
        connectors.put(new ConnectorLocation(location.getRelative(0, 0, 0), Connector.Matter.ENERGY), powerConnector);

        components.put("item_connector", new StructureComponent(this, new BlockLocation(location.world, 0, 0, 0), direction, null));

        itemConnector = new Connector(components.get("item_connector"), Direction.DOWN.mask(), Connector.FlowDirection.OUT, Connector.Matter.ITEM);
        connectors.put(new ConnectorLocation(location, Connector.Matter.ITEM), itemConnector);
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
