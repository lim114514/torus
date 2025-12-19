package com.github.alantr7.torus.addon;

import com.github.alantr7.torus.api.addon.Lifecycle;
import com.github.alantr7.torus.api.addon.TorusAddon;
import com.github.alantr7.torus.log.Category;
import com.github.alantr7.torus.log.TorusLogger;
import lombok.Getter;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class TorusAddonManager {

    private final Map<String, TorusAddon> addonsByPlugin = new HashMap<>();

    @Getter
    private final Lifecycle lifecycle = new Lifecycle();

    public void registerAddon(TorusAddon addon) {
        addonsByPlugin.put(addon.plugin.getName().toLowerCase(), addon);
        TorusLogger.info(Category.GENERAL, "Registered addon " + addon.name);
    }

    public Collection<TorusAddon> getAddons() {
        return addonsByPlugin.values();
    }

}
