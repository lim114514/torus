package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.world.BlockLocation;
import com.github.alantr7.torus.math.Direction;
import com.github.alantr7.torus.structure.Structure;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.structure.builder.StructureBodyDef;
import com.github.alantr7.torus.structure.builder.StructureComponentDef;
import com.github.alantr7.torus.structure.builder.StructureConnectorDef;
import com.github.alantr7.torus.structure.component.Connector;
import com.github.alantr7.torus.structure.display.ModelTemplate;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import static com.github.alantr7.torus.machine.EnergyCable.MODELS_FLUID;

public class FluidPipe extends Structure {

    public FluidPipe() {
        super("torus:fluid_pipe", CableInstance.class);
        isHeavy = false;
    }

    @Override
    public StructureInstance place(BlockLocation location, Direction direction) {
        CableInstance instance = (CableInstance) super.place(location, direction);
        instance.updateConnections();

        return instance;
    }

    @Override
    protected StructureInstance instantiate(@NotNull BlockLocation location, Direction direction) {
        ModelTemplate modelDisconnected = new ModelTemplate();
        modelDisconnected.add(MODELS_FLUID[6]);

        StructureComponentDef base = new StructureComponentDef("base", new Vector3f(), modelDisconnected.build(location.getBlock().getLocation().add(.5, 0, .5), direction));
        return new CableInstance(location, new StructureBodyDef(new StructureComponentDef[]{base}), Connector.Matter.FLUID);
    }

}
