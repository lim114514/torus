package com.github.alantr7.torus.log;

import com.github.alantr7.torus.TorusPlugin;
import com.github.alantr7.torus.config.MainConfig;

public class TorusLogger {

    public static void info(Category category, String message) {
        TorusPlugin.getInstance().getLogger().info(category.prefix + message);
    }

    public static void error(Category category, String message) {
        TorusPlugin.getInstance().getLogger().warning(category.prefix + message);
    }

}
