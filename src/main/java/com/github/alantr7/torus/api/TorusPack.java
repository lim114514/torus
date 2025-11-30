package com.github.alantr7.torus.api;

import com.github.alantr7.torus.TorusPlugin;

import java.io.File;

public class TorusPack {

    public final String id;

    public final String name;

    public final File configDirectory;

    public TorusPack(String id, String name) {
        this.id = id;
        this.name = name;
        this.configDirectory = new File(new File(TorusPlugin.getInstance().getDataFolder(), "packs"), id);
    }

}
