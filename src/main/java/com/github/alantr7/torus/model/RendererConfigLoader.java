package com.github.alantr7.torus.model;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.Map;

public abstract class RendererConfigLoader {

    public final String id;

    public RendererConfigLoader(String id) {
        this.id = id.toLowerCase();
    }

    @Nullable
    public abstract PartModelTemplate load(ConfigurationSection section, String name, Vector3f offset, Map<String, String> variables);

}
