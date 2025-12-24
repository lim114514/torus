package com.github.alantr7.torus.model;

import com.github.alantr7.torus.world.Direction;
import org.bukkit.Location;
import org.joml.Vector3f;

public abstract class PartModelTemplate {

    public final String name;

    public final Vector3f offset;

    public PartModelTemplate(String name, Vector3f offset) {
        this.name = name;
        this.offset = offset;
    }

    public abstract PartModel build(Location location, Direction direction);

    public abstract PartModel recycle(PartModel model, Location location, float rotH, float rotV);

}
