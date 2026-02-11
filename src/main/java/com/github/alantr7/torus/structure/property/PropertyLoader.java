package com.github.alantr7.torus.structure.property;

import com.github.alantr7.torus.log.Category;
import com.github.alantr7.torus.log.TorusLogger;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class PropertyLoader {

    private static final Map<PropertyType<?>, BiFunction<FileConfiguration, String, Object>> loaders = new HashMap<>();
    static {
        loaders.put(PropertyType.INT, MemorySection::getInt);
        loaders.put(PropertyType.FLOAT, MemorySection::getDouble);
        loaders.put(PropertyType.STRING, MemorySection::getString);
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

        Object value = loaderFunction.apply(config, property.name);
        property.value = (T) value;
    }

}
