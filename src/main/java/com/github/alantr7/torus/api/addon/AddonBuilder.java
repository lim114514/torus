package com.github.alantr7.torus.api.addon;

import com.github.alantr7.torus.TorusPlugin;
import com.github.alantr7.torus.math.MathUtils;
import org.bukkit.plugin.java.JavaPlugin;

public class AddonBuilder {

    public final JavaPlugin plugin;

    public final String namespace;

    int externalConfigsFlags = 0;

    String name;

    public AddonBuilder(JavaPlugin plugin, String namespace) {
        this.plugin = plugin;
        this.namespace = namespace;
    }

    public AddonBuilder name(String name) {
        this.name = name;
        return this;
    }

    public AddonBuilder allowExternalConfigurations(ConfigType... configTypes) {
        for (ConfigType type : configTypes) {
            externalConfigsFlags = MathUtils.setFlag(externalConfigsFlags, (int) Math.pow(2, type.ordinal()), true);
        }
        return this;
    }

    public TorusAddon register() {
        TorusAddon addon = new TorusAddon(plugin, namespace, name);
        addon.externalConfigsFlags = externalConfigsFlags;

        TorusPlugin.getInstance().getAddonManager().registerAddon(addon);
        return addon;
    }

}
