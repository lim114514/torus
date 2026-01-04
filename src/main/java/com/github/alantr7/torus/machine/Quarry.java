package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.TorusPlugin;
import com.github.alantr7.torus.api.resource.ResourceLocation;
import com.github.alantr7.torus.math.ByteArrayBuilder;
import com.github.alantr7.torus.world.Direction;
import com.github.alantr7.torus.structure.Structure;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.structure.builder.StructureBodyDef;
import com.github.alantr7.torus.structure.builder.StructureComponentDef;
import com.github.alantr7.torus.structure.builder.StructureSocketDef;
import com.github.alantr7.torus.structure.component.Socket;
import com.github.alantr7.torus.world.BlockLocation;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.HashSet;
import java.util.Set;

public class Quarry extends Structure {

    public static final Set<Material> BLOCK_BLACKLIST = new HashSet<>();
    static {
        BLOCK_BLACKLIST.add(Material.BEDROCK);
    }

    public static int ENERGY_CONSUMPTION_ON_MOVE = 50;

    public static int ENERGY_CONSUMPTION_ON_MINE = 150;

    public static int ENERGY_MAXIMUM_INPUT = 350;

    public static int MAXIMUM_DEPTH = 64;

    public Quarry() {
        super(TorusPlugin.DEFAULT_ADDON, "quarry", "Quarry", QuarryInstance.class);
        portableData.add("energy");
        offset = new byte[] {0, 0, -6};
    }

    @Override
    protected void createBounds(ByteArrayBuilder builder) {
        int length = 5;
        for (int x = 0; x <= length; x++) {
            builder.add(x, 0, length);
            builder.add(x, 0, -length);
            builder.add(-x, 0, length);
            builder.add(-x, 0, -length);

            builder.add(length, 0, x);
            builder.add(-length, 0, x);
            builder.add(length, 0, -x);
            builder.add(-length, 0, -x);

            builder.add(x, 4, length);
            builder.add(x, 4, -length);
            builder.add(-x, 4, length);
            builder.add(-x, 4, -length);

            builder.add(length, 4, x);
            builder.add(-length, 4, x);
            builder.add(length, 4, -x);
            builder.add(-length, 4, -x);
        }

        for (int i = 1; i <= 3; i++) {
            builder.add(length, i, length);
            builder.add(length, i, -length);
            builder.add(-length, i, -length);
            builder.add(-length, i, length);
        }

        // Controller
        builder.add(0, 0, -length - 1);
    }

    @Override
    protected StructureInstance instantiate(@NotNull BlockLocation location, Direction direction) {
        return new QuarryInstance(this, location, new StructureBodyDef(
          new StructureComponentDef[]{
            new StructureComponentDef("base", new Vector3f()),
            new StructureComponentDef("in_energy", new Vector3f(0, 0, -6), new StructureSocketDef(
              Socket.Matter.ENERGY, Socket.FlowDirection.IN, direction.getLeft().mask()
            )),
            new StructureComponentDef("out_item", new Vector3f(0, 0, -6), new StructureSocketDef(
              Socket.Matter.ITEM, Socket.FlowDirection.OUT, Direction.UP.mask()
            )),
            new StructureComponentDef("head", new Vector3f()),
            new StructureComponentDef("feed", new Vector3f()),
            new StructureComponentDef("drill_bit", new Vector3f()),
            new StructureComponentDef("gantry_x", new Vector3f()),
            new StructureComponentDef("gantry_z", new Vector3f()),
          }
        ), direction);
    }

    @Override
    protected void loadConfig() {
        super.loadConfig();
        ENERGY_CONSUMPTION_ON_MINE = config.getInt("energy_settings.consumption_on_mine", ENERGY_CONSUMPTION_ON_MINE);
        ENERGY_CONSUMPTION_ON_MOVE = config.getInt("energy_settings.consumption_on_mine", ENERGY_CONSUMPTION_ON_MOVE);
        ENERGY_MAXIMUM_INPUT = config.getInt("energy_settings.maximum_input", ENERGY_MAXIMUM_INPUT);
        MAXIMUM_DEPTH = config.getInt("special_settings.maximum_depth", MAXIMUM_DEPTH);
    }

}
