package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.exception.SetupException;
import com.github.alantr7.torus.structure.inspection.InspectableDataContainer;
import com.github.alantr7.torus.structure.property.PropertyType;
import com.github.alantr7.torus.structure.socket.EnergySocket;
import com.github.alantr7.torus.structure.socket.ItemSocket;
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
import com.github.alantr7.torus.structure.socket.Socket;
import com.github.alantr7.torus.world.Pitch;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static com.github.alantr7.torus.lang.Localization.translate;

public class BlockBreakerInstance extends StructureInstance implements EnergyContainer {

    @Getter
    protected Data<Integer> storedEnergy = dataContainer.persist("energy", Data.Type.INT, 0);

    protected EnergySocket powerSocket;

    protected ItemSocket itemSocket;

    protected StructureInventory inventory;

    protected int breakingTicks;

    protected boolean isInventoryFull;

    public BlockBreakerInstance(BlockLocation location, StructureBodyDef bodyDef, Direction direction, Pitch pitch) {
        super(Structures.BLOCK_BREAKER, location, bodyDef, direction, pitch);
    }

    BlockBreakerInstance(LoadContext context) {
        super(context);
    }

    @Override
    protected void setup() throws SetupException {
        powerSocket = requireSocket("power_connector", EnergySocket.class);
        itemSocket = requireSocket("item_connector", ItemSocket.class);
        powerSocket.maximumInput = structure.getProperty("energy_settings.maximum_input", PropertyType.INT);
        inventory = new CustomStructureInventory(1);
        itemSocket.linkedInventory = inventory;
    }

    @Override
    public InspectableDataContainer setupInspectableData() {
        return new InspectableDataContainer((byte) 2)
          .property(translate("inspection.energy_unit"), InspectableDataContainer.TEMPLATE_RF.apply(this))
          .line(() -> isInventoryFull ? translate("inspection.block_breaker.inventory_full") : null);
    }

    @Override
    public int getEnergyCapacity() {
        return structure.getProperty("energy_settings.capacity", PropertyType.INT);
    }

    @Override
    public void tick(boolean isVirtual) {
        powerSocket.maintainEnergy(this);
        itemSocket.attemptDirectItemExport();

        if (!hasSufficientEnergy(structure.getProperty("energy_settings.consumption_on_mine", PropertyType.INT)))
            return;

        if ((isInventoryFull = (inventory.getItems()[0] != null)))
            return;

        if (location.world.getStructure(location.getRelative(facing)) != null)
            return;

        Location blockLocation = location.getRelative(facing).toBukkit();
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
        consumeEnergy(structure.getProperty("energy_settings.consumption_on_mine", PropertyType.INT));
    }

}
