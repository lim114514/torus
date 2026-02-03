package com.github.alantr7.torus.model;

import com.github.alantr7.bukkitplugin.annotations.core.Invoke;
import com.github.alantr7.bukkitplugin.annotations.core.RequiresPlugin;
import com.github.alantr7.bukkitplugin.annotations.core.Singleton;
import com.github.alantr7.torus.api.resource.Resource;
import com.github.alantr7.torus.integration.modelengine.model_provider.ModelEngineRendererConfigLoader;
import com.github.alantr7.torus.log.Category;
import com.github.alantr7.torus.log.TorusLogger;
import com.github.alantr7.torus.model.de_provider.*;
import com.github.alantr7.torus.updater.UpdateUtils_0_5_2;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.joml.Vector3f;

import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;
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

    public ModelTemplate load(File file, Map<String, String> variables) {
        return load(file, YamlConfiguration.loadConfiguration(file), variables);
    }

    public ModelTemplate load(Resource resource, Map<String, String> variables) {
        try (Reader reader = new InputStreamReader(resource.stream)) {
            return load(resource.file, YamlConfiguration.loadConfiguration(reader), variables);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public ModelTemplate load(File file, FileConfiguration yaml, Map<String, String> variables) {
        ModelTemplate template = new ModelTemplate(yaml.getInt("model_version", 1));
        UpdateUtils_0_5_2.updateModelFileFormatFromV1ToV2(file, yaml);

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
                if (!offsetRaw.isEmpty()) {
                    TorusLogger.error(Category.MODELS, "Invalid offset: " + Arrays.toString(offsetRaw.toArray(Float[]::new)));
                }
                offset = rendererId.equals("display_entities") ? new Vector3f(.5f, 0, .5f) : new Vector3f();
            } else {
                offset = new Vector3f(offsetRaw.get(0), offsetRaw.get(1), offsetRaw.get(2));
            }

            PartModelTemplate partModelTemplate = loader.load(section, partName, offset, variables);
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
