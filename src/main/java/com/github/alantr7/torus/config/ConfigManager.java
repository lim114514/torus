package com.github.alantr7.torus.config;

import com.github.alantr7.torus.TorusPlugin;

import java.io.File;

public class ConfigManager {

    public void initialize() {
        File directory = new File(TorusPlugin.getInstance().getDataFolder(), "packs");
        if (!directory.exists()) {
            savePresetPack();
        }

        File[] packsFiles = directory.listFiles();
        if (packsFiles == null)
            return;

        for (File file : packsFiles) {
            if (!file.isDirectory())
                continue;

            ConfigPackLoader loader = new ConfigPackLoader(file);
            loader.load();
        }
    }

    private void savePresetPack() {
        TorusPlugin.getInstance().saveResource("packs/torus/items.recipes.yml", false);
        TorusPlugin.getInstance().saveResource("packs/torus/crusher.recipes.yml", false);
    }

}
