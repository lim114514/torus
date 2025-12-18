package com.github.alantr7.torus.api.addon;

import com.github.alantr7.torus.TorusPlugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class TorusAddon {

    public final JavaPlugin plugin;

    public final String id;

    public final String name;

    public final File rootDirectory;

    public final File configsDirectory, modelsDirectory, recipesDirectory;

    public TorusAddon(JavaPlugin plugin, String id, String name) {
        this.plugin = plugin;
        this.id = id;
        this.name = name;
        this.rootDirectory = new File(new File(TorusPlugin.getInstance().getDataFolder(), "packs"), id);
        this.configsDirectory = new File(rootDirectory, "configs");
        this.modelsDirectory = new File(rootDirectory, "models");
        this.recipesDirectory = new File(rootDirectory, "recipes");
    }

}
