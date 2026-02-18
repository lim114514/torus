package com.github.alantr7.torus.structure.property;

import com.github.alantr7.torus.log.Category;
import com.github.alantr7.torus.log.TorusLogger;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public class PropertyLoader {

    private static final Map<PropertyType<?>, BiFunction<FileConfiguration, String, Object>> loaders = new HashMap<>();
    static {
        loaders.put(PropertyType.BOOLEAN, MemorySection::getBoolean);
        loaders.put(PropertyType.INT, MemorySection::getInt);
        loaders.put(PropertyType.FLOAT, (section, key) -> (float) section.getDouble(key));
        loaders.put(PropertyType.STRING, MemorySection::getString);
        loaders.put(PropertyType.STRING_LIST, MemorySection::getStringList);
        loaders.put(PropertyType.VECTOR3I, (section, key) -> {
            List<Integer> list = section.getIntegerList(key);
            return list.size() != 3 ? new Vector3i() : new Vector3i(list.get(0), list.get(1), list.get(2));
        });
        loaders.put(PropertyType.VECTOR3F, (section, key) -> {
            List<Float> list = section.getFloatList(key);
            return list.size() != 3 ? new Vector3f() : new Vector3f(list.get(0), list.get(1), list.get(2));
        });
    }

    private final FileConfiguration config;

    public PropertyLoader(FileConfiguration config) {
        this.config = config;
    }

    public <T> void load(Property<T> property) {
        var loaderFunction = loaders.get(property.type);
        if (loaderFunction == null) {
            TorusLogger.error(Category.STRUCTURES, "There is no property loader for type: " + property.type.name);
            return;
        }

        if (config.isSet(property.name)) {
            Object value = loaderFunction.apply(config, property.name);
            property.value = (T) value;
        }
    }

}
