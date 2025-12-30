package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.TorusPlugin;
import com.github.alantr7.torus.model.ModelTemplate;
import com.github.alantr7.torus.model.de_provider.DisplayEntitiesPartModelTemplate;
import com.github.alantr7.torus.world.BlockLocation;
import com.github.alantr7.torus.world.Direction;
import com.github.alantr7.torus.structure.Structure;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.structure.builder.StructureBodyDef;
import com.github.alantr7.torus.structure.builder.StructureComponentDef;
import com.github.alantr7.torus.structure.component.Socket;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import static com.github.alantr7.torus.machine.EnergyCable.*;

public class ItemCable extends Structure {

    static ModelTemplate INITIAL_MODEL = new ModelTemplate(1);
    static {
        DisplayEntitiesPartModelTemplate part = new DisplayEntitiesPartModelTemplate("base");
        part.add(MODELS_ITEM[0]);

        INITIAL_MODEL.add(part);
    }

    public ItemCable() {
        super(TorusPlugin.DEFAULT_ADDON, "item_cable", "Item Cable", CableInstance.class);
        isHeavy = false;
        allowedStates.add(STATE_NORTH);
        allowedStates.add(STATE_EAST);
        allowedStates.add(STATE_SOUTH);
        allowedStates.add(STATE_WEST);
        allowedStates.add(STATE_UP);
        allowedStates.add(STATE_DOWN);
    }

    @Override
    public ModelTemplate getModel() {
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
        return new CableInstance(location, new StructureBodyDef(new StructureComponentDef[]{
          new StructureComponentDef("base", new Vector3f())
        }), Socket.Matter.ITEM);
    }

}
