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

    @ConfigOption(path = "EXPERIMENTAL_virtualization.enabled")
    public static boolean EXPERIMENTAL_VIRTUALIZATION_ENABLED = false;

    @ConfigOption(path = "EXPERIMENTAL_virtualization.allowed_worlds")
    public static List<String> EXPERIMENTAL_VIRTUALIZATION_ALLOWED_WORLDS = new ArrayList<>(Collections.singletonList("world"));

    @ConfigOption(path = "EXPERIMENTAL_virtualization.allowed_structures")
    public static List<String> EXPERIMENTAL_VIRTUALIZATION_STRUCTURES = new ArrayList<>(List.of(
      "torus:solar_generator",
      "torus:coal_generator",
      "torus:energy_cable",
      "torus:item_conduit",
      "torus:fluid_pipe"
    ));

    @ConfigOption(path = "logs.world_save")
    public static boolean LOGS_WORLD_SAVE = true;

    @ConfigOption(path = "logs.recipe_load")
    public static boolean LOGS_RECIPE_LOAD = true;

    @ConfigOption(path = "locale")
    public static String LOCALE = "en";

    @ConfigOption(path = "allow_update_checks")
    public static boolean ALLOW_UPDATE_CHECKS = true;

}
