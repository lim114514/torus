package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.TorusPlugin;
import com.github.alantr7.torus.math.ByteArrayBuilder;
import com.github.alantr7.torus.model.ModelLocation;
import com.github.alantr7.torus.world.Direction;
import com.github.alantr7.torus.structure.Structure;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.structure.builder.StructureBodyDef;
import com.github.alantr7.torus.structure.builder.StructureComponentDef;
import com.github.alantr7.torus.structure.builder.StructureSocketDef;
import com.github.alantr7.torus.structure.component.Socket;
import com.github.alantr7.torus.world.BlockLocation;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class BlastFurnace extends Structure {

    public BlastFurnace() {
        super(TorusPlugin.DEFAULT_ADDON, "blast_furnace", "Blast Furnace", BlastFurnaceInstance.class);
        isInteractable = true;
        modelLocation = new ModelLocation("torus", "blast_furnace");
    }

    @Override
    protected void createBounds(ByteArrayBuilder builder) {
        for (int x = -1; x <= 1; x++) {
            for (int z = 0; z <= 2; z++) {
                builder.add(x, 0, z);
            }
        }
        builder.add(0, 1, 1);
        builder.add(0, 2, 1);
        builder.add(0, 3, 1);
        builder.add(0, 4, 1);
    }

    @Override
    protected StructureInstance instantiate(@NotNull BlockLocation location, Direction direction) {
        return new BlastFurnaceInstance(location, new StructureBodyDef(new StructureComponentDef[]{
          new StructureComponentDef("base", new Vector3f(0f, 0f, 0f)),
          new StructureComponentDef("in_item", new Vector3f(0, 4, 1), new StructureSocketDef(
            Socket.Matter.ITEM, Socket.FlowDirection.IN, Direction.UP.mask()
          )),
          new StructureComponentDef("out_item", new Vector3f(1, 0, 0), new StructureSocketDef(
            Socket.Matter.ITEM, Socket.FlowDirection.OUT, direction.mask()
          )),
          new StructureComponentDef("out_slug", new Vector3f(-1, 0, 0), new StructureSocketDef(
            Socket.Matter.ITEM, Socket.FlowDirection.OUT, direction.mask()
          ))
        }), direction);
    }

}
