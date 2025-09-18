package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.math.BlockLocation;
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

import static com.github.alantr7.torus.machine.EnergyCable.MODELS_ITEM;

public class ItemCable extends Structure {

    public ItemCable() {
        super("torus:item_cable");
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

        StructureComponentDef base = new StructureComponentDef("base", new Vector3f(), modelDisconnected.build(location.getBlock().getLocation().add(.5, 0, .5), direction));
        return new CableInstance(location, new StructureBodyDef(new StructureComponentDef[]{base}, new StructureConnectorDef[0]), Connector.Matter.ITEM);
    }

}
