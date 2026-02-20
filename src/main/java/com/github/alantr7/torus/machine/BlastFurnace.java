package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.TorusPlugin;
import com.github.alantr7.torus.structure.StructureFlag;
import com.github.alantr7.torus.utils.ByteArrayBuilder;
import com.github.alantr7.torus.world.Direction;
import com.github.alantr7.torus.structure.Structure;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.structure.builder.StructureBodyDef;
import com.github.alantr7.torus.structure.builder.StructurePartDef;
import com.github.alantr7.torus.structure.builder.StructureSocketDef;
import com.github.alantr7.torus.structure.socket.Socket;
import com.github.alantr7.torus.world.BlockLocation;
import com.github.alantr7.torus.world.Pitch;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import static com.github.alantr7.torus.lang.Localization.translatable;

public class BlastFurnace extends Structure {

    public BlastFurnace() {
        super(TorusPlugin.DEFAULT_ADDON, "blast_furnace", translatable("structure.blast_furnace.name"), BlastFurnaceInstance.class);
        setFlags(StructureFlag.COLLIDABLE | StructureFlag.INTERACTABLE | StructureFlag.HEAVY | StructureFlag.TICKABLE);
        setHologramTranslation(new Vector3f(1.2f, 0, 0));
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
    protected StructureInstance instantiate(@NotNull BlockLocation location, Direction direction, Pitch pitch) {
        return new BlastFurnaceInstance(location, new StructureBodyDef(new StructurePartDef[]{
          new StructurePartDef("base", new Vector3f(0f, 0f, 0f)),
          new StructurePartDef("in_item", new Vector3f(0, 4, 1), new StructureSocketDef(
            Socket.Medium.ITEM, Socket.FlowDirection.IN, Direction.UP.mask()
          )),
          new StructurePartDef("out_item", new Vector3f(1, 0, 0), new StructureSocketDef(
            Socket.Medium.ITEM, Socket.FlowDirection.OUT, direction.mask()
          )),
          new StructurePartDef("out_slug", new Vector3f(-1, 0, 0), new StructureSocketDef(
            Socket.Medium.ITEM, Socket.FlowDirection.OUT, direction.mask()
          ))
        }), direction);
    }

}
