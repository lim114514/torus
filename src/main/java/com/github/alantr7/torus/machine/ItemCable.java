package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.world.BlockLocation;
import com.github.alantr7.torus.world.Direction;
import com.github.alantr7.torus.structure.Structure;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.structure.builder.StructureBodyDef;
import com.github.alantr7.torus.structure.builder.StructureComponentDef;
import com.github.alantr7.torus.structure.component.Connector;
import com.github.alantr7.torus.model.ModelTemplate;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import static com.github.alantr7.torus.machine.EnergyCable.MODELS_ITEM;

public class ItemCable extends Structure {

    public ItemCable() {
        super("torus:item_cable", CableInstance.class);
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
        modelDisconnected.add(MODELS_ITEM[6]);

        return new CableInstance(location, new StructureBodyDef(new StructureComponentDef[]{
          new StructureComponentDef("base", new Vector3f(), modelDisconnected)
        }), Connector.Matter.ITEM);
    }

}
