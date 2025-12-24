package com.github.alantr7.torus.model;

import com.github.alantr7.bukkitplugin.annotations.core.Invoke;
import com.github.alantr7.bukkitplugin.annotations.core.RequiresPlugin;
import com.github.alantr7.bukkitplugin.annotations.core.Singleton;
import com.github.alantr7.torus.integration.modelengine.model_provider.ModelEngineRendererConfigLoader;
import com.github.alantr7.torus.log.Category;
import com.github.alantr7.torus.log.TorusLogger;
import com.github.alantr7.torus.model.de_provider.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.joml.Vector3f;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Singleton
public class ModelLoader {

    private final Map<String, RendererConfigLoader> renderers = new HashMap<>();

    {
        renderers.put("display_entities", new DisplayEntitiesRendererConfigLoader());
    }

    public ModelTemplate load(File file) {
        return load(YamlConfiguration.loadConfiguration(file));
    }

    public ModelTemplate load(FileConfiguration yaml) {
        ModelTemplate template = new ModelTemplate(yaml.getInt("model_version", 1));
        for (String partName : yaml.getKeys(false)) {
            ConfigurationSection section = yaml.getConfigurationSection(partName);
            if (section == null)
                continue;

            String rendererId = section.getString("renderer", "").toLowerCase();
            RendererConfigLoader loader = renderers.get(rendererId);

            if (loader == null) {
                TorusLogger.error(Category.MODELS, "Invalid renderer: " + rendererId);
                continue;
            }

            List<Float> offsetRaw = section.getFloatList("offset");
            Vector3f offset;
            if (offsetRaw.size() != 3) {
                TorusLogger.error(Category.MODELS, "Invalid offset: " + Arrays.toString(offsetRaw.toArray(Float[]::new)));
                offset = new Vector3f();
            } else {
                offset = new Vector3f(offsetRaw.get(0), offsetRaw.get(1), offsetRaw.get(2));
            }

            PartModelTemplate partModelTemplate = loader.load(section, partName, offset);
            if (partModelTemplate != null) {
                template.add(partModelTemplate);
            }
        }

        return template;
    }

    public void registerRendererConfigLoader(RendererConfigLoader configLoader) {
        renderers.put(configLoader.id, configLoader);
    }

    @RequiresPlugin("ModelEngine")
    @Invoke(Invoke.Schedule.AFTER_PLUGIN_ENABLE)
    public void registerModelEngineProvider() {
        registerRendererConfigLoader(new ModelEngineRendererConfigLoader());
        TorusLogger.info(Category.GENERAL, "Successful integration with ModelEngine.");
    }

}
