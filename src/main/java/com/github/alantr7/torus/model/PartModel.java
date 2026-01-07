package com.github.alantr7.torus.model;

import com.github.alantr7.torus.model.animation.Animation;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class PartModel {

    public final PartModelTemplate template;

    public final String name;

    @Getter @Setter
    private @Nullable Animation animation;

    public PartModel(PartModelTemplate template) {
        this.template = template;
        this.name = template.name;
    }

    public abstract void setLocation(@NotNull Location location);

    public abstract void setRotation(float horizontal, float vertical);

    public abstract void remove();

}