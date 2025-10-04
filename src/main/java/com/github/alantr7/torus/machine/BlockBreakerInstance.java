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
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class BlockBreakerInstance extends StructureInstance implements EnergyContainer {

    @Getter
    protected int energyCapacity = 50;

    @Getter
    protected Data<Integer> storedEnergy = dataContainer.persist("energy", Data.Type.INT, 0);

    protected Connector powerConnector;

    protected Connector itemConnector;

    protected StructureInventory inventory;

    public static final int RF_COST = 25;

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
        powerConnector.maintainEnergy(this);
        if (!hasSufficientEnergy(RF_COST) || inventory.getItems()[0] != null) {
            return;
        }

        if (!location.getRelative(direction).getBlock().getType().isAir()) {
            inventory.addItem(new ItemStack(location.getRelative(direction).getBlock().getType()));
            location.getRelative(direction).getBlock().setType(Material.AIR);
            consumeEnergy(RF_COST);
        }
    }

}
