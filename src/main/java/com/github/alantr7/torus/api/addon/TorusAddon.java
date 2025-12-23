package com.github.alantr7.torus.api.addon;

import com.github.alantr7.torus.TorusPlugin;
import com.github.alantr7.torus.math.MathUtils;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class TorusAddon {

    public final JavaPlugin plugin;

    public final String id;

    public final String name;

    public final File rootDirectory;

    public final File configsDirectory, modelsDirectory, itemsDirectory, recipesDirectory;

    public int externalConfigsFlags;

    public TorusAddon(JavaPlugin plugin, String id, String name) {
        this.plugin = plugin;
        this.id = id;
        this.name = name;
        this.rootDirectory = new File(new File(TorusPlugin.getInstance().getDataFolder(), "configs"), id);
        this.configsDirectory = new File(rootDirectory, "structures");
        this.modelsDirectory = new File(rootDirectory, "models");
        this.itemsDirectory = new File(rootDirectory, "items");
        this.recipesDirectory = new File(rootDirectory, "recipes");
    }

    public boolean allowsExternalConfig(ConfigType configType) {
        return MathUtils.hasFlag(externalConfigsFlags, (int) Math.pow(2, configType.ordinal()));
    }

}
