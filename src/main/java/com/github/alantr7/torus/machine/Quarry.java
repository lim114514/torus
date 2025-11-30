package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.math.ByteArrayBuilder;
import com.github.alantr7.torus.model.ModelLocation;
import com.github.alantr7.torus.world.Direction;
import com.github.alantr7.torus.structure.Structure;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.structure.builder.StructureBodyDef;
import com.github.alantr7.torus.structure.builder.StructureComponentDef;
import com.github.alantr7.torus.structure.builder.StructureConnectorDef;
import com.github.alantr7.torus.structure.component.Connector;
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

    public Quarry() {
        super("torus:quarry", "Quarry", QuarryInstance.class);
        itemDropDataWhitelist.add("energy");
        offset = new byte[] {0, 0, -6};
        modelLocation = new ModelLocation("torus", "quarry");
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
            new StructureComponentDef("in_energy", new Vector3f(0, 0, -6), new StructureConnectorDef(
              Connector.Matter.ENERGY, Connector.FlowDirection.IN, direction.getLeft().mask()
            )),
            new StructureComponentDef("out_item", new Vector3f(0, 0, -6), new StructureConnectorDef(
              Connector.Matter.ITEM, Connector.FlowDirection.OUT, Direction.UP.mask()
            )),
            new StructureComponentDef("drill_holder", new Vector3f()),
            new StructureComponentDef("drill", new Vector3f()),
            new StructureComponentDef("drill_tip", new Vector3f()),
            new StructureComponentDef("mover_x", new Vector3f()),
            new StructureComponentDef("mover_z", new Vector3f()),
          }
        ), direction);
    }

}
