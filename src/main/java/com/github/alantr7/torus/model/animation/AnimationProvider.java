package com.github.alantr7.torus.model.animation;

import com.github.alantr7.torus.model.PartModel;
import com.github.alantr7.torus.structure.StructureInstance;

@FunctionalInterface
public interface AnimationProvider<T> {

    T create(StructureInstance structure, PartModel model);

}
