package com.github.alantr7.torus.api.addon;

import com.github.alantr7.torus.TorusPlugin;
import org.bukkit.plugin.java.JavaPlugin;

public class AddonBuilder {

    public final JavaPlugin plugin;

    public final String namespace;

    String name;

    public AddonBuilder(JavaPlugin plugin, String namespace) {
        this.plugin = plugin;
        this.namespace = namespace;
    }

    public AddonBuilder name(String name) {
        this.name = name;
        return this;
    }

    public TorusAddon register() {
        TorusAddon addon = new TorusAddon(plugin, namespace, name);
        TorusPlugin.getInstance().getAddonManager().registerAddon(addon);
        return addon;
    }

}
