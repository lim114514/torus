package com.github.alantr7.torus.updater;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class UpdateUtils_0_6_1 {

    public static void updateStructureConfig(File prev, File next) {
        FileConfiguration yaml = YamlConfiguration.loadConfiguration(prev);
        yaml.set("model_controller", null);
        try {
            yaml.save(prev);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Files.copy(prev.toPath(), next.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        prev.delete();
    }

}
