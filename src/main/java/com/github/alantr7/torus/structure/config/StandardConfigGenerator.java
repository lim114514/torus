package com.github.alantr7.torus.structure.config;

import com.github.alantr7.torus.structure.Structure;
import com.github.alantr7.torus.structure.StructureFlag;
import com.github.alantr7.torus.structure.property.Property;
import com.github.alantr7.torus.structure.property.PropertyType;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.*;

public class StandardConfigGenerator {

    public static final StandardConfigGenerator INSTANCE = new StandardConfigGenerator();

    private static final Map<String, List<String>> comments = Map.of(
            "config_version", List.of("Do not change this value - it is used for automatic config updates."),
            "general_settings.placement_offset", List.of("When machine is placed on a block, it will be offset by this vector.",
                    "This vector is facing the positive Z direction. Rotation will be automatically applied."),
            "general_settings.heavy", List.of("If set to true, structure can be broken only with a hammer.")
    );

    private static final Map<PropertyType<?>, PropertyMapper<?>> mappers = new HashMap<>();
    static {
        map(PropertyType.INT, MemorySection::set);
        map(PropertyType.STRING, MemorySection::set);
    }

    private static <T> void map(PropertyType<T> type, PropertyMapper<T> mapper) {
        mappers.put(type, mapper);
    }

    @SuppressWarnings("all")
    public YamlConfiguration generate(Structure structure) {
        YamlConfiguration config = new YamlConfiguration();
        config.set("config_version", 2);
        config.set("general_settings.enabled", structure.isEnabled);
        config.set("general_settings.placement_offset", List.of(structure.getOffset()[0], structure.getOffset()[1], structure.getOffset()[2]));
        config.set("general_settings.heavy", structure.hasFlag(StructureFlag.HEAVY));
        config.set("general_settings.portable_data", structure.portableData.stream().toList());

        for (Property<?> property : structure.getProperties()) {
            PropertyMapper mapper = mappers.get(property.type);
            if (mapper != null) {
                mapper.map(config, property.name, property.value);
            }
        }

        config.set("info_hologram.offset", List.of(structure.hologramOffset[0], structure.hologramOffset[1], structure.hologramOffset[2]));
        config.set("info_hologram.translation", List.of(structure.hologramTranslation[0], structure.hologramTranslation[1], structure.hologramTranslation[2]));

        comments.forEach(config::setComments);
        return config;
    }

    @FunctionalInterface
    interface PropertyMapper<T> {

        void map(YamlConfiguration config, String data, T value);

    }

}
