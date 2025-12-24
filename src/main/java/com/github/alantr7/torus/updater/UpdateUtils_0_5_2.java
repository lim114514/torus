package com.github.alantr7.torus.updater;

import com.github.alantr7.torus.log.Category;
import com.github.alantr7.torus.log.TorusLogger;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.List;

public class UpdateUtils_0_5_2 {

    public static void updateModelFileFormatFromV1ToV2(File file, FileConfiguration config) {
        int modelVersion = config.getInt("model_version", 1);
        if (modelVersion != 1)
            return;

        for (String partName : config.getKeys(false)) {
            List<String> elements = config.getStringList(partName);
            config.set(partName, null);
            config.set(partName + ".renderer", "DISPLAY_ENTITIES");
            config.set(partName + ".elements", elements);
        }

        config.set("model_version", 2);

        try {
            config.save(file);
            TorusLogger.info(Category.MODELS, "Upgraded model file to v2: " + file.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
