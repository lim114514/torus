package com.github.alantr7.torus.integration.modelengine.model_provider;

import com.github.alantr7.torus.model.animation.Animation;

public class ModelEngineAnimation extends Animation {

    private final ModelEnginePartModel part;

    public final String name;

    private boolean isPlaying;

    public ModelEngineAnimation(ModelEnginePartModel part, String name) {
        this.part = part;
        this.name = name;
    }

    @Override
    public void tick() {
        if (isPlaying)
            return;

        part.activeModel.getAnimationHandler().playAnimation(name, 1d, 1d, 1d, true);
        isPlaying = true;
    }

}
