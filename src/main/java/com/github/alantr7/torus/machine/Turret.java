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
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class Turret extends Structure {

    public Turret() {
        super("torus:turret", "Laser Turret", TurretInstance.class);
        itemDropDataWhitelist.add("energy");
        modelLocation = new ModelLocation("torus", "turret");
    }

    @Override
    protected void createBounds(ByteArrayBuilder builder) {
        builder.add(0, 0, 0);
        builder.add(0, 1, 0);
    }

    @Override
    protected StructureInstance instantiate(@NotNull BlockLocation location, Direction direction) {
        return new TurretInstance(location, new StructureBodyDef(new StructureComponentDef[]{
          new StructureComponentDef("base", new Vector3f()),
          new StructureComponentDef("head", new Vector3f()),
          new StructureComponentDef("in_item", new Vector3f(), new StructureConnectorDef(
            Connector.Matter.ITEM, Connector.FlowDirection.IN, direction.getOpposite().mask()
          )),
          new StructureComponentDef("in_energy", new Vector3f(), new StructureConnectorDef(
            Connector.Matter.ENERGY, Connector.FlowDirection.IN, Direction.DOWN.mask()
          ))
        }), direction);
    }
}
