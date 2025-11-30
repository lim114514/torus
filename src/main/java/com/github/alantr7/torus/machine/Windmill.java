package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.math.ByteArrayBuilder;
import com.github.alantr7.torus.model.ModelLocation;
import com.github.alantr7.torus.model.ModelTemplate;
import com.github.alantr7.torus.model.PartModelElementItemDisplayRenderer;
import com.github.alantr7.torus.model.PartModelTemplate;
import com.github.alantr7.torus.structure.Structure;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.structure.builder.StructureBodyDef;
import com.github.alantr7.torus.structure.builder.StructureComponentDef;
import com.github.alantr7.torus.structure.builder.StructureConnectorDef;
import com.github.alantr7.torus.structure.component.Connector;
import com.github.alantr7.torus.world.BlockLocation;
import com.github.alantr7.torus.world.Direction;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class Windmill extends Structure {

    public static final float MAXIMUM_SPEED = 1.85f * (float) Math.PI / 3f;

    public Windmill() {
        super("torus:windmill", "Windmill", WindmillInstance.class);
        itemDropDataWhitelist.add("energy");
        modelLocation = new ModelLocation("torus", "windmill");
    }

    @Override
    protected void createBounds(ByteArrayBuilder builder) {
        builder.add(0, 0, 0);
        builder.add(0, 1, 0);
        builder.add(0, 2, 0);
        builder.add(0, 3, 0);
    }

    @Override
    protected StructureInstance instantiate(@NotNull BlockLocation location, Direction direction) {
        return new WindmillInstance(location, new StructureBodyDef(new StructureComponentDef[]{
          new StructureComponentDef("base", new Vector3f()),
          new StructureComponentDef("in_power", new Vector3f(), new StructureConnectorDef(
            Connector.Matter.ENERGY, Connector.FlowDirection.OUT, direction.mask()
          ))
        }), direction);
    }

}
