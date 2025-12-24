package com.github.alantr7.torus.model;

import com.github.alantr7.torus.world.Direction;
import org.bukkit.Location;

public abstract class PartModelTemplate {

    public final String name;

    public PartModelTemplate(String name) {
        this.name = name;
    }

    public abstract PartModel build(Location location, Direction direction);

    public abstract PartModel recycle(PartModel model, Location location, float rotH, float rotV);

}
