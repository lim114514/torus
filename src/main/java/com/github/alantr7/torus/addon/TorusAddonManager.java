package com.github.alantr7.torus.addon;

import com.github.alantr7.torus.api.addon.TorusAddon;
import com.github.alantr7.torus.log.Category;
import com.github.alantr7.torus.log.TorusLogger;

import java.util.HashMap;
import java.util.Map;

public class TorusAddonManager {

    private final Map<String, TorusAddon> addonsByPlugin = new HashMap<>();

    public void registerAddon(TorusAddon addon) {
        addonsByPlugin.put(addon.plugin.getName().toLowerCase(), addon);
        TorusLogger.info(Category.GENERAL, "Registered addon " + addon.name);
    }

}
