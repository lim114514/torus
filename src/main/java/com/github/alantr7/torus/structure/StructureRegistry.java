package com.github.alantr7.torus.structure;

import com.github.alantr7.bukkitplugin.annotations.core.Invoke;
import com.github.alantr7.bukkitplugin.annotations.core.InvokePeriodically;
import com.github.alantr7.bukkitplugin.annotations.core.Singleton;
import com.github.alantr7.bytils.buffer.ByteArrayReader;
import com.github.alantr7.bytils.buffer.ByteArrayWriter;
import com.github.alantr7.torus.TorusPlugin;
import com.github.alantr7.torus.api.addon.ConfigType;
import com.github.alantr7.torus.log.Category;
import com.github.alantr7.torus.log.TorusLogger;
import com.github.alantr7.torus.model.de_provider.ModelLoader;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

@Singleton
public class StructureRegistry {

    private int nextStructureId = 2;

    private final Map<String, Structure> loaded = new HashMap<>();

    private final Map<Integer, Structure> loadedByNumericIds = new LinkedHashMap<>();

    private final Map<String, Integer> structuresIds = new HashMap<>();

    private final Set<Structure> saveQuery = new LinkedHashSet<>();

    @Invoke(Invoke.Schedule.BEFORE_PLUGIN_ENABLE)
    private void init() {
        load();
    }

    private void load() {
        File file = new File(TorusPlugin.getInstance().getDataFolder(), "id_map.dat");
        if (!file.exists())
            return;

        try {
            ByteArrayReader reader = new ByteArrayReader(Files.readAllBytes(file.toPath()));
            while (reader.hasNext()) {
                int numericId = ByteArrayReader.toInt(reader.readBytes(2));
                String id = reader.readString();

                structuresIds.put(id, numericId);
                if (numericId >= nextStructureId) {
                    nextStructureId = numericId + 1;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @InvokePeriodically(interval = 20 * 60)
    @Invoke(Invoke.Schedule.AFTER_PLUGIN_DISABLE)
    public void save() {
        if (saveQuery.isEmpty())
            return;

        File file = new File(TorusPlugin.getInstance().getDataFolder(), "id_map.dat");
        file.getParentFile().mkdirs();

        try {
            RandomAccessFile raf = new RandomAccessFile(file, "rw");
            raf.seek(raf.length());

            ByteArrayWriter writer = new ByteArrayWriter();

            for (Structure str : saveQuery) {
                writer.writeU2(str.numericId);
                writer.writeString(str.namespacedId);
            }

            saveQuery.clear();

            raf.write(writer.getBuffer());
            raf.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void registerAndInitialize(Structure structure) {
        if (structure.numericId != -1) {
            throw new RuntimeException("Structure must not have numeric id already assigned when registering it");
        }

        if (structuresIds.containsKey(structure.namespacedId)) {
            structure.numericId = structuresIds.get(structure.namespacedId);
        } else {
            structure.numericId = nextStructureId++;
            structuresIds.put(structure.namespacedId, structure.numericId);
            saveQuery.add(structure);
        }

        if (structure.addon.allowsExternalConfig(ConfigType.STRUCTURE)) {
            File configFile = new File(structure.addon.configsDirectory, structure.id + ".config.yml");

            // Save default config if it exists
            if (!configFile.exists()) {
                try {
                    InputStream is = TorusPlugin.getInstance().getResource(structure.configResource);
                    if (is != null) {
                        is.close();
                        TorusPlugin.getInstance().saveResource(structure.configResource, false);
                    }
                } catch (Exception | Error e) {
                    TorusLogger.error(Category.GENERAL, "Could not save the default config for '" + structure.id + "'");
                    e.printStackTrace();
                }
            }

            if (configFile.exists()) {
                try {
                    structure.config = YamlConfiguration.loadConfiguration(configFile);
                    structure.loadConfig();
                } catch (Exception | Error e) {
                    TorusLogger.error(Category.MODELS, "Invalid configuration for structure '" + structure.id + "'");
                    e.printStackTrace();
                }
            }
        }

        if (structure.modelLocation != null) {
            InputStream modelResource = structure.modelLocation.getResource();
            if (modelResource != null) {
                try (Reader reader = new InputStreamReader(modelResource)) {
                    structure.setModel(ModelLoader.load(YamlConfiguration.loadConfiguration(reader)));
                } catch (Exception | Error e) {
                    TorusLogger.error(Category.MODELS, "Could not load model for " + structure.namespacedId);
                    e.printStackTrace();
                }
            }
        }

        loaded.put(structure.namespacedId, structure);
        loadedByNumericIds.put(structure.numericId, structure);
    }

    public Structure getStructure(String id) {
        return loaded.get(id);
    }

    public Structure getStructure(int id) {
        return loadedByNumericIds.get(id);
    }

    public Set<String> getStructuresIds() {
        return structuresIds.keySet();
    }

    public Set<Map.Entry<String, Integer>> getStructuresIdsMap() {
        return structuresIds.entrySet();
    }

}
