package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.exception.SetupException;
import com.github.alantr7.torus.structure.LoadContext;
import com.github.alantr7.torus.structure.Structure;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.structure.builder.StructureBodyDef;
import com.github.alantr7.torus.structure.component.Socket;
import com.github.alantr7.torus.world.BlockLocation;
import com.github.alantr7.torus.world.Direction;
import com.github.alantr7.torus.world.Fluid;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class FaucetInstance extends StructureInstance {

    protected Socket socket;

    public FaucetInstance(Structure structure, BlockLocation location, StructureBodyDef bodyDef, Direction direction) {
        super(structure, location, bodyDef, direction);
    }

    FaucetInstance(LoadContext context) {
        super(context);
    }

    @Override
    protected void setup() throws SetupException {
        socket = requireSocket("base");
    }

    @Override
    public void handlePlayerInteraction(PlayerInteractEvent event, BlockLocation location) {
        ItemStack stack = event.getPlayer().getInventory().getItemInMainHand();
        if (stack.getType() != Material.BUCKET)
            return;

        int consumed = socket.consumeFluid(Fluid.WATER, 1000);
        if (consumed == 1000) {
            fillBucket(event.getPlayer(), stack, Fluid.WATER);
            return;
        }

        consumed = socket.consumeFluid(Fluid.LAVA, 1000);
        if (consumed == 1000) {
            fillBucket(event.getPlayer(), stack, Fluid.LAVA);
            return;
        }
    }

    private void fillBucket(Player player, ItemStack bucket, Fluid fluid) {
        if (bucket.getAmount() == 1) {
            bucket.setType(fluid == Fluid.WATER ? Material.WATER_BUCKET : Material.LAVA_BUCKET);
        } else {
            bucket.setAmount(bucket.getAmount() - 1);
            player.getInventory().addItem(new ItemStack(fluid == Fluid.WATER ? Material.WATER_BUCKET : Material.LAVA_BUCKET));
        }
    }

}
