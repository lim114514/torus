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
import com.github.alantr7.torus.structure.component.Socket;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class BlockBreakerInstance extends StructureInstance implements EnergyContainer {

    @Getter
    protected Data<Integer> storedEnergy = dataContainer.persist("energy", Data.Type.INT, 0);

    protected Socket powerSocket;

    protected Socket itemSocket;

    protected StructureInventory inventory;

    protected int breakingTicks;

    public BlockBreakerInstance(BlockLocation location, StructureBodyDef bodyDef, Direction direction) {
        super(Structures.BLOCK_BREAKER, location, bodyDef, direction);
    }

    BlockBreakerInstance(LoadContext context) {
        super(context);
    }

    @Override
    protected void setup() {
        powerSocket = getSocket("power_connector");
        itemSocket = getSocket("item_connector");
        powerSocket.maximumInput = BlockBreaker.ENERGY_MAXIMUM_INPUT;
        inventory = new CustomStructureInventory(1);
        itemSocket.linkedInventory = inventory;
    }

    @Override
    public InspectableData setupInspectableData() {
        return new InspectableData((byte) 1)
          .property("RF", InspectableData.TEMPLATE_RF.apply(this));
    }

    @Override
    public int getEnergyCapacity() {
        return BlockBreaker.ENERGY_CAPACITY;
    }

    @Override
    public void tick() {
        powerSocket.maintainEnergy(this);
        itemSocket.attemptDirectItemExport();

        if (!hasSufficientEnergy(BlockBreaker.ENERGY_CONSUMPTION_ON_MINE) || inventory.getItems()[0] != null)
            return;

        if (location.world.getStructure(location.getRelative(direction)) != null)
            return;

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

            blockDamage = 0;
            breakingTicks = 0;
            blockLocation.getWorld().playSound(blockLocation, block.getBlockSoundGroup().getBreakSound(), 0.75f, 1f);

            block.setType(Material.AIR);
        } else {
            blockDamage = (float) breakingTicks / 3;
            blockLocation.getWorld().playSound(blockLocation, block.getBlockSoundGroup().getHitSound(), 0.75f, 1f);
        }

        for (Player player : location.world.getBukkit().getPlayersSeeingChunk(location.x >> 4, location.z >> 4)) {
            player.sendBlockDamage(blockLocation, blockDamage);
        }
        consumeEnergy(BlockBreaker.ENERGY_CONSUMPTION_ON_MINE);
    }

}
