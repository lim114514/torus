package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.exception.SetupException;
import com.github.alantr7.torus.structure.LoadContext;
import com.github.alantr7.torus.structure.Structure;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.structure.builder.StructureBodyDef;
import com.github.alantr7.torus.structure.component.Socket;
import com.github.alantr7.torus.world.BlockLocation;
import com.github.alantr7.torus.world.Direction;
import com.github.alantr7.torus.world.Pitch;
import org.bukkit.Material;

import static com.github.alantr7.torus.machine.LightBulb.STATE_POWERED;

public class LightBulbInstance extends StructureInstance {

    protected Socket socket;

    protected boolean wasPowered;

    LightBulbInstance(LoadContext context) {
        super(context);
    }

    public LightBulbInstance(Structure structure, BlockLocation location, StructureBodyDef bodyDef, Direction direction, Pitch pitch) {
        super(structure, location, bodyDef, direction, pitch);
    }

    @Override
    protected void setup() throws SetupException {
        socket = requireSocket("base");
    }

    @Override
    public void onModelSpawn() {
        location.getBlock().setType(Material.AIR);
    }

    @Override
    public void tick(boolean isVirtual) {
        int consumed = socket.consumeEnergy(30);
        boolean isPowered = consumed == 30;

        if (isPowered) {
            state.set(STATE_POWERED, true);
        } else {
            state.set(STATE_POWERED, false);
        }

        if (isPowered != wasPowered) {
            location.getBlock().setType(isPowered ? Material.LIGHT : Material.AIR);
        }

        wasPowered = isPowered;
    }

}
