package com.github.alantr7.torus.integration.modelengine.model_provider;

import com.github.alantr7.torus.log.Category;
import com.github.alantr7.torus.log.TorusLogger;
import com.github.alantr7.torus.model.PartModelTemplate;
import com.github.alantr7.torus.model.RendererConfigLoader;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

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

        return new ModelEnginePartModelTemplate(part, offset, blueprintId, section.getString("animation"));
    }

}
