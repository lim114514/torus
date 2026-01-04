package com.github.alantr7.torus.updater;

import com.github.alantr7.torus.TorusPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class UpdateUtils_0_5_3 {

    public static void updateStructureConfigFromV1ToV2(String internalPath, File file, FileConfiguration config) {
        int configVersion = config.getInt("config_version", 1);
        if (configVersion != 1)
            return;

        InputStream is = TorusPlugin.getInstance().getResource(internalPath);
        if (is == null)
            return;

        try (Reader reader = new InputStreamReader(is)) {
            FileConfiguration internal = YamlConfiguration.loadConfiguration(reader);
            config.set("config_version", 2);
            config.set("model_controller", internal.getConfigurationSection("model_controller"));

            try {
                config.save(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
