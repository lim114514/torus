package com.github.alantr7.torus.config;

import com.github.alantr7.torus.TorusPlugin;

import java.io.File;

public class ConfigManager {

    public void initialize() {
        TorusPlugin.getInstance().getRecipeManager().reset();
        File directory = new File(TorusPlugin.getInstance().getDataFolder(), "packs");
        if (!directory.exists() || !new File(directory, "recipes").exists()) {
            savePresetPack();
        }

        File[] packsFiles = directory.listFiles();
        if (packsFiles == null)
            return;

        for (File file : packsFiles) {
            if (!file.isDirectory())
                continue;

            PackLoader loader = new PackLoader(file);
            loader.load();
        }
    }

    private void savePresetPack() {
        TorusPlugin.getInstance().saveResource("packs/torus/recipes/crafting.recipes.yml", false);
        TorusPlugin.getInstance().saveResource("packs/torus/recipes/smelting.recipes.yml", false);
        TorusPlugin.getInstance().saveResource("packs/torus/recipes/blasting.recipes.yml", false);
        TorusPlugin.getInstance().saveResource("packs/torus/recipes/crusher.recipes.yml", false);
        TorusPlugin.getInstance().saveResource("packs/torus/recipes/washer.recipes.yml", false);
    }

}
