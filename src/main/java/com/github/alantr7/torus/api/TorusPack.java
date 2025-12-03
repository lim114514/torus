package com.github.alantr7.torus.api;

import com.github.alantr7.torus.TorusPlugin;

import java.io.File;

public class TorusPack {

    public final String id;

    public final String name;

    public final File rootDirectory;

    public final File configsDirectory, modelsDirectory, recipesDirectory;

    public TorusPack(String id, String name) {
        this.id = id;
        this.name = name;
        this.rootDirectory = new File(new File(TorusPlugin.getInstance().getDataFolder(), "packs"), id);
        this.configsDirectory = new File(rootDirectory, "configs");
        this.modelsDirectory = new File(rootDirectory, "models");
        this.recipesDirectory = new File(rootDirectory, "recipes");
    }

}
