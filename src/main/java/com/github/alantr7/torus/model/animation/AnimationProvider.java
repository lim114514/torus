package com.github.alantr7.torus.model.animation;

import com.github.alantr7.torus.model.PartModel;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface AnimationProvider<K extends PartModel, V extends Animation> {

    @Nullable
    V get(K part);

}
