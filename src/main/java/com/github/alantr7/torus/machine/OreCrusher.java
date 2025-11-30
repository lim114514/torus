package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.item.ItemCriteria;
import com.github.alantr7.torus.model.ModelLocation;
import com.github.alantr7.torus.model.ModelTemplate;
import com.github.alantr7.torus.world.Direction;
import com.github.alantr7.torus.math.ByteArrayBuilder;
import com.github.alantr7.torus.structure.Structure;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.structure.builder.StructureBodyDef;
import com.github.alantr7.torus.structure.builder.StructureComponentDef;
import com.github.alantr7.torus.structure.builder.StructureConnectorDef;
import com.github.alantr7.torus.structure.component.Connector;
import com.github.alantr7.torus.model.PartModelElementItemDisplayRenderer;
import com.github.alantr7.torus.model.PartModelTemplate;
import com.github.alantr7.torus.world.BlockLocation;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class OreCrusher extends Structure {

    public static ItemCriteria INPUT_CRITERIA = new ItemCriteria();

    public static final int ENERGY_CONSUMPTION_PER_TICK = 300;

    public OreCrusher() {
        super("torus:ore_crusher", "Ore Crusher", OreCrusherInstance.class);
        offset = new byte[]{ 0, 0, -1 };
        itemDropDataWhitelist.add("energy");
        modelLocation = new ModelLocation("torus", "ore_crusher");
    }

    @Override
    protected void createBounds(ByteArrayBuilder builder) {
        builder.add(0, 0, 0);
        builder.add(0, 1, 0);
        builder.add(0, 2, 0);
        builder.add(1, 0, 0);
        builder.add(-2, 0, 0);
        builder.add(-2, 1, 0);
        builder.add(-1, 0, 0);
        builder.add(0, 0, -1);
        builder.add(0, 1, -1);
    }

    @Override
    protected StructureInstance instantiate(@NotNull BlockLocation location, Direction direction) {
        return new OreCrusherInstance(location, new StructureBodyDef(
          new StructureComponentDef[]{
            new StructureComponentDef("body", new Vector3f()),
            new StructureComponentDef("power_connector", new Vector3f(-2, 1, 0), new StructureConnectorDef(
              Connector.Matter.ENERGY, Connector.FlowDirection.IN, direction.getLeft().mask()
            )),
            new StructureComponentDef("item_connector", new Vector3f(0, 2, 0), new StructureConnectorDef(
              Connector.Matter.ITEM, Connector.FlowDirection.IN, Direction.UP.mask()
            )),
            new StructureComponentDef("out_connector", new Vector3f(1, 0, 0), new StructureConnectorDef(
              Connector.Matter.ITEM, Connector.FlowDirection.OUT, direction.getRight().mask()
            )),
            new StructureComponentDef("wheel_left", new Vector3f()),
            new StructureComponentDef("wheel_right", new Vector3f())
          }
        ), direction);
    }
}
