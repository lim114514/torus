package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.TorusPlugin;
import com.github.alantr7.torus.structure.StructureFlag;
import com.github.alantr7.torus.utils.ByteArrayBuilder;
import com.github.alantr7.torus.structure.Structure;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.structure.builder.StructureBodyDef;
import com.github.alantr7.torus.structure.builder.StructurePartDef;
import com.github.alantr7.torus.world.BlockLocation;
import com.github.alantr7.torus.world.Direction;
import com.github.alantr7.torus.world.Pitch;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import static com.github.alantr7.torus.lang.Localization.translatable;

public class PowerPole extends Structure {

    public PowerPole() {
        super(TorusPlugin.DEFAULT_ADDON, "power_pole", translatable("structure.power_pole.name"), PowerPoleInstance.class);
        setFlags(StructureFlag.COLLIDABLE);
    }

    @Override
    protected void createBounds(ByteArrayBuilder builder) {
        builder.add(0, 0, 0);
        builder.add(0, 1, 0);
        builder.add(0, 2, 0);
        builder.add(0, 3, 0);
        builder.add(1, 3, 0);
        builder.add(-1, 3, 0);
    }

    @Override
    protected StructureInstance instantiate(@NotNull BlockLocation location, Direction direction, Pitch pitch) {
        return new PowerPoleInstance(location, new StructureBodyDef(new StructurePartDef[]{
          new StructurePartDef("base", new Vector3f())
        }), direction);
    }

}
