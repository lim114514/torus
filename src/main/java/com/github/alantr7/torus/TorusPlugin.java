package com.github.alantr7.torus;

import com.github.alantr7.bukkitplugin.BukkitPlugin;
import com.github.alantr7.bukkitplugin.annotations.generative.JavaPlugin;
import com.github.alantr7.bukkitplugin.annotations.relocate.Relocate;
import com.github.alantr7.bukkitplugin.annotations.relocate.Relocations;
import com.github.alantr7.torus.config.ConfigManager;
import com.github.alantr7.torus.config.ConfigPackLoader;
import com.github.alantr7.torus.item.TorusItemManager;
import com.github.alantr7.torus.recipe.TorusRecipeManager;
import com.github.alantr7.torus.world.TorusWorldManager;
import lombok.Getter;

@JavaPlugin(name = "Torus")
@Relocations(@Relocate(from = "com.github.alantr7.bukkitplugin", to = "com.github.alantr7.torus.bpf"))
public class TorusPlugin extends BukkitPlugin {

    @Getter
    static TorusPlugin instance;

    @Getter
    protected final TorusItemManager itemManager;

    @Getter
    protected final ConfigManager configManager = new ConfigManager();

    public TorusPlugin() {
        instance = this;
        itemManager = new TorusItemManager();
    }

    @Override
    protected void onPluginEnable() {
        configManager.initialize();
    }

    @Override
    protected void onPluginDisable() {
    }

    public TorusRecipeManager getRecipeManager() {
        return getSingleton(TorusRecipeManager.class);
    }

    public TorusWorldManager getWorldManager() {
        return getSingleton(TorusWorldManager.class);
    }

}
