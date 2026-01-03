package com.github.alantr7.torus.model;

import com.github.alantr7.torus.model.animation.Animation;
import com.github.alantr7.torus.model.animation.AnimationProvider;
import com.github.alantr7.torus.world.Direction;
import org.bukkit.Location;
import org.joml.Vector3f;

import java.util.Map;

public abstract class PartModelTemplate {

    public final String name;

    public final Vector3f offset;

    public final Map<String, AnimationProvider<PartModel, Animation>> animationMap;

    public PartModelTemplate(String name, Vector3f offset, Map<String, AnimationProvider<PartModel, Animation>> animationMap) {
        this.name = name;
        this.offset = offset;
        this.animationMap = animationMap;
    }

    public abstract PartModel build(Location location, Direction direction);

    public abstract PartModel recycle(PartModel model, Location location, float rotH, float rotV);

}
