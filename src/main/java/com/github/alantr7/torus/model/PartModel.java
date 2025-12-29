package com.github.alantr7.torus.model;

import com.github.alantr7.torus.model.animation.Animation;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

public abstract class PartModel {

    @Getter @Setter
    private @Nullable Animation animation;

    public abstract void teleport(Location location);

    public abstract void remove();

}