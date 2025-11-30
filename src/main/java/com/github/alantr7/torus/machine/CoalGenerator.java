package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.item.ItemCriteria;
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

public class CoalGenerator extends Structure {

    public static final ItemCriteria INPUT_CRITERIA = new ItemCriteria();
    static {
        INPUT_CRITERIA.materials.add(Material.COAL);
        INPUT_CRITERIA.materials.add(Material.CHARCOAL);
    }

    public CoalGenerator() {
        super("torus:coal_generator", "Coal Generator", CoalGeneratorInstance.class);
        itemDropDataWhitelist.add("energy");
        modelLocation = new ModelLocation("torus", "coal_generator");
    }

    @Override
    public void createBounds(ByteArrayBuilder builder) {
        for (int z = 0; z <= 2; z++) {
            builder.add(0, 0, z);
        }
        builder.add(1, 0, 0);
        builder.add(1, 1, 0);
        builder.add(1, 2, 0);
    }

    @Override
    protected StructureInstance instantiate(@NotNull BlockLocation location, Direction direction) {
        return new CoalGeneratorInstance(location, new StructureBodyDef(new StructureComponentDef[]{
          new StructureComponentDef("base", new Vector3f()),
          new StructureComponentDef(
            "item_connector", new Vector3f(0, 0, 2), new StructureConnectorDef(Connector.Matter.ITEM, Connector.FlowDirection.IN, direction.getOpposite().mask())
          ),
          new StructureComponentDef(
            "power_connector", new Vector3f(0, 0, 0), new StructureConnectorDef(Connector.Matter.ENERGY, Connector.FlowDirection.OUT, direction.mask())
          )
        }), direction);
    }

}
