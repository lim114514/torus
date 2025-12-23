package com.github.alantr7.torus.config;

import com.github.alantr7.bukkitplugin.annotations.config.Config;
import com.github.alantr7.bukkitplugin.annotations.config.ConfigOption;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Config("configs/config.yml")
public class MainConfig {

    @ConfigOption(path = "config_version")
    public static int CONFIG_VERSION = 2;

    @ConfigOption(path = "world_blacklist")
    public static List<String> WORLD_BLACKLIST = new ArrayList<>(Collections.singletonList("world_nether"));

    @ConfigOption(path = "customization.enable_model_editing")
    public static boolean CUSTOMIZATION_ENABLE_MODEL_EDITING = false;

    @ConfigOption(path = "logs.world_save")
    public static boolean LOGS_WORLD_SAVE = true;

    @ConfigOption(path = "logs.recipe_load")
    public static boolean LOGS_RECIPE_LOAD = true;

}
