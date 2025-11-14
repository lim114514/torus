package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.model.ModelTemplate;
import com.github.alantr7.torus.world.BlockLocation;
import com.github.alantr7.torus.world.Direction;
import com.github.alantr7.torus.structure.Structure;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.structure.builder.StructureBodyDef;
import com.github.alantr7.torus.structure.builder.StructureComponentDef;
import com.github.alantr7.torus.structure.component.Connector;
import com.github.alantr7.torus.model.PartModelTemplate;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import static com.github.alantr7.torus.machine.EnergyCable.MODELS_FLUID;

public class FluidPipe extends Structure {

    static ModelTemplate INITIAL_MODEL = new ModelTemplate();
    static {
        PartModelTemplate part = new PartModelTemplate("base");
        part.add(MODELS_FLUID[0]);

        INITIAL_MODEL.add(part);
    }

    public FluidPipe() {
        super("torus:fluid_pipe", "Fluid Pipe", CableInstance.class);
        isHeavy = false;
    }

    @Override
    public ModelTemplate getInitialModel() {
        return INITIAL_MODEL;
    }

    @Override
    public StructureInstance place(BlockLocation location, Direction direction) {
        CableInstance instance = (CableInstance) super.place(location, direction);
        instance.updateConnections();

        return instance;
    }

    @Override
    protected StructureInstance instantiate(@NotNull BlockLocation location, Direction direction) {
        StructureComponentDef base = new StructureComponentDef("base", new Vector3f());
        return new CableInstance(location, new StructureBodyDef(new StructureComponentDef[]{base}), Connector.Matter.FLUID);
    }

}
