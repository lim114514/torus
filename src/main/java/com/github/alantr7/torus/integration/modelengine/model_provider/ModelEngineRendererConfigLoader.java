package com.github.alantr7.torus.integration.modelengine.model_provider;

import com.github.alantr7.torus.log.Category;
import com.github.alantr7.torus.log.TorusLogger;
import com.github.alantr7.torus.model.PartModel;
import com.github.alantr7.torus.model.PartModelTemplate;
import com.github.alantr7.torus.model.RendererConfigLoader;
import com.github.alantr7.torus.model.animation.Animation;
import com.github.alantr7.torus.model.animation.AnimationProvider;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ModelEngineRendererConfigLoader extends RendererConfigLoader {

    public ModelEngineRendererConfigLoader() {
        super("modelengine");
    }

    @Override
    public @Nullable PartModelTemplate load(ConfigurationSection section, String part, Vector3f offset) {
        String blueprintId = section.getString("blueprint");
        if (blueprintId == null) {
            TorusLogger.error(Category.MODELS, "Blueprint is not set.");
            return null;
        }

        Map<String, AnimationProvider<PartModel, Animation>> animationMap;
        ConfigurationSection animationMapSection = section.getConfigurationSection("animation_map");
        if (animationMapSection != null) {
            animationMap = new HashMap<>();
            for (String modelAnimation : animationMapSection.getKeys(false)) {
                String blueprintAnimation = animationMapSection.getString(modelAnimation);
                if (blueprintAnimation == null) {
                    continue;
                }

                animationMap.put(modelAnimation, part1 -> new ModelEngineAnimation((ModelEnginePartModel) part1, blueprintAnimation));
            }
        } else {
            animationMap = Collections.emptyMap();
        }

        return new ModelEnginePartModelTemplate(part, offset, blueprintId, animationMap);
    }

}
