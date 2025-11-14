package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.structure.inspection.InspectableData;
import com.github.alantr7.torus.world.BlockLocation;
import com.github.alantr7.torus.world.Direction;
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
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class BlockBreakerInstance extends StructureInstance implements EnergyContainer {

    @Getter
    protected int energyCapacity = 50;

    @Getter
    protected Data<Integer> storedEnergy = dataContainer.persist("energy", Data.Type.INT, 0);

    protected Connector powerConnector;

    protected Connector itemConnector;

    protected StructureInventory inventory;

    protected int breakingTicks;

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
    public InspectableData setupInspectableData() {
        return new InspectableData((byte) 1)
          .property("RF", InspectableData.TEMPLATE_RF.apply(this));
    }

    @Override
    public void tick() {
        powerConnector.maintainEnergy(this);
        itemConnector.attemptDirectItemExport();

        if (!hasSufficientEnergy(RF_COST) || inventory.getItems()[0] != null) {
            return;
        }

        Location blockLocation = location.getRelative(direction).toBukkit();
        Block block = blockLocation.getBlock();
        if (block.getType().isAir() || block.isLiquid())
            return;

        float blockDamage;
        if (breakingTicks++ == 3) {
            for (ItemStack drop : block.getDrops()) {
                inventory.addItem(drop.clone());
                break;
            }
            block.setType(Material.AIR);

            blockDamage = 0;
            breakingTicks = 0;
        } else {
            blockDamage = (float) breakingTicks / 3;
        }

        for (Player player : location.world.getBukkit().getPlayersSeeingChunk(location.x >> 4, location.z >> 4)) {
            player.sendBlockDamage(blockLocation, blockDamage);
        }
        consumeEnergy(RF_COST);
    }

}
