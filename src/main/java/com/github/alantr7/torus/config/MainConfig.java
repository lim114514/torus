package com.github.alantr7.torus.config;

import com.github.alantr7.bukkitplugin.annotations.config.Config;
import com.github.alantr7.bukkitplugin.annotations.config.ConfigOption;

@Config("config.yml")
public class MainConfig {

    @ConfigOption(path = "logs.world_save")
    public static boolean LOGS_WORLD_SAVE = true;

    @ConfigOption(path = "logs.recipe_load")
    public static boolean LOGS_RECIPE_LOAD = true;

}
