package com.github.alantr7.torus.machine;

import com.github.alantr7.torus.TorusPlugin;
import com.github.alantr7.torus.structure.Structure;
import com.github.alantr7.torus.structure.StructureFlag;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.structure.Structures;
import com.github.alantr7.torus.structure.builder.StructureBodyDef;
import com.github.alantr7.torus.structure.builder.StructurePartDef;
import com.github.alantr7.torus.world.BlockLocation;
import com.github.alantr7.torus.world.Direction;
import com.github.alantr7.torus.world.Pitch;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import static com.github.alantr7.torus.lang.Localization.translatable;

public class PressureVent extends Structure {

    public PressureVent() {
        super(TorusPlugin.DEFAULT_ADDON, "pressure_vent", translatable("structure.pressure_vent.name"), PressureVentInstance.class);
        setFlags(StructureFlag.COLLIDABLE | StructureFlag.HEAVY);
    }

    @Override
    protected StructureInstance instantiate(@NotNull BlockLocation location, Direction direction, Pitch pitch) {
        return new PressureVentInstance(Structures.PRESSURE_VENT, location, new StructureBodyDef(new StructurePartDef[]{
                new StructurePartDef("base", new Vector3f())
        }), direction);
    }

}
