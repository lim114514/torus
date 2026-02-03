package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.TorusPlugin;
import com.github.alantr7.torus.world.BlockLocation;
import com.github.alantr7.torus.world.Direction;
import com.github.alantr7.torus.structure.Structure;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.structure.builder.StructureBodyDef;
import com.github.alantr7.torus.structure.builder.StructureComponentDef;
import com.github.alantr7.torus.structure.builder.StructureSocketDef;
import com.github.alantr7.torus.structure.socket.Socket;
import com.github.alantr7.torus.world.Pitch;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class BlockBreaker extends Structure {

    public static int ENERGY_CAPACITY = 50;

    public static int ENERGY_MAXIMUM_INPUT = 100;

    public static int ENERGY_CONSUMPTION_ON_MINE = 25;

    public BlockBreaker() {
        super(TorusPlugin.DEFAULT_ADDON, "block_breaker", "Block Breaker", BlockBreakerInstance.class);
        isHeavy = false;
        isOmnidirectional = true;
        portableData.add("energy");
    }

    @Override
    protected StructureInstance instantiate(@NotNull BlockLocation location, Direction direction, Pitch pitch) {
        return new BlockBreakerInstance(location, new StructureBodyDef(
          new StructureComponentDef[]{
            new StructureComponentDef("body", new Vector3f(0, 0, 0)),
            new StructureComponentDef("power_connector", new Vector3f(0, 0, 0), new StructureSocketDef(
              Socket.Medium.ENERGY, Socket.FlowDirection.IN, direction.getOpposite().mask()
            )),
            new StructureComponentDef("item_connector", new Vector3f(0, 0, 0), new StructureSocketDef(
              Socket.Medium.ITEM, Socket.FlowDirection.OUT, Direction.DOWN.mask()
            )) }
        ), direction, pitch);
    }

    @Override
    protected void loadConfig() {
        super.loadConfig();
        ENERGY_CAPACITY = config.getInt("energy_settings.capacity", ENERGY_CAPACITY);
        ENERGY_CONSUMPTION_ON_MINE = config.getInt("energy_settings.consumption_on_mine", ENERGY_CONSUMPTION_ON_MINE);
    }

}
