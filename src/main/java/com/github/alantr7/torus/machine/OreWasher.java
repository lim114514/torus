package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.item.ItemCriteria;
import com.github.alantr7.torus.math.ByteArrayBuilder;
import com.github.alantr7.torus.model.ModelLocation;
import com.github.alantr7.torus.model.ModelTemplate;
import com.github.alantr7.torus.world.Direction;
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

public class OreWasher extends Structure {

    public static final int ENERGY_CONSUMPTION_PER_TICK = 300;

    public static ItemCriteria INPUT_CRITERIA = new ItemCriteria();

    public OreWasher() {
        super("torus:ore_washer", "Ore Washer", OreWasherInstance.class);
        itemDropDataWhitelist.add("energy");
        itemDropDataWhitelist.add("fluid");
        modelLocation = new ModelLocation("torus", "ore_washer");
    }

    @Override
    protected void createBounds(ByteArrayBuilder builder) {
        builder.add(0, 0, 0);
        builder.add(0, 0, 1);
        builder.add(0, 1, 0);
    }

    @Override
    protected StructureInstance instantiate(@NotNull BlockLocation location, Direction direction) {
        return new OreWasherInstance(location, new StructureBodyDef(
          new StructureComponentDef[] {
            new StructureComponentDef("body", new Vector3f()),
            new StructureComponentDef("item_connector", new Vector3f(0f, 1f, 0f), new StructureConnectorDef(
              Connector.Matter.ITEM, Connector.FlowDirection.IN, Direction.UP.mask()
            )),
            new StructureComponentDef("out_connector", new Vector3f(0f, 0, 0f), new StructureConnectorDef(
              Connector.Matter.ITEM, Connector.FlowDirection.OUT, direction.mask()
            )),
            new StructureComponentDef("power_connector", new Vector3f(0f, 0, 1f), new StructureConnectorDef(
              Connector.Matter.ENERGY, Connector.FlowDirection.IN, direction.getOpposite().mask()
            )),
            new StructureComponentDef("fluid_connector", new Vector3f(0f, 0, 1f), new StructureConnectorDef(
              Connector.Matter.FLUID, Connector.FlowDirection.IN, Direction.UP.mask()
            ))
          }
        ), direction);
    }
}
