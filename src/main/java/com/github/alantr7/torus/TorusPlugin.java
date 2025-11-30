package com.github.alantr7.torus;

import com.github.alantr7.bukkitplugin.BukkitPlugin;
import com.github.alantr7.bukkitplugin.annotations.generative.JavaPlugin;
import com.github.alantr7.bukkitplugin.annotations.generative.SoftDepends;
import com.github.alantr7.bukkitplugin.annotations.relocate.Relocate;
import com.github.alantr7.bukkitplugin.annotations.relocate.Relocations;
import com.github.alantr7.torus.config.ConfigManager;
import com.github.alantr7.torus.item.TorusItemManager;
import com.github.alantr7.torus.log.TorusLogger;
import com.github.alantr7.torus.player.TorusPlayerManager;
import com.github.alantr7.torus.recipe.TorusRecipeManager;
import com.github.alantr7.torus.structure.StructureRegistry;
import com.github.alantr7.torus.world.TorusWorldManager;
import lombok.Getter;
import org.bukkit.Bukkit;

@JavaPlugin(name = "Torus", version = "0.4.2", apiVersion = "1.21")
@Relocations(@Relocate(from = "com.github.alantr7.bukkitplugin", to = "com.github.alantr7.torus.bpf"))
@SoftDepends("ProtocolLib")
public class TorusPlugin extends BukkitPlugin {

    @Getter
    static TorusPlugin instance;

    @Getter
    protected final TorusItemManager itemManager;

    @Getter
    protected final ConfigManager configManager = new ConfigManager();

    private static boolean usesPaperAPI;

    public TorusPlugin() {
        instance = this;
        itemManager = new TorusItemManager();

        checkPaperAPI();
    }

    @Override
    protected void onPluginEnable() {
        configManager.initialize();
    }

    @Override
    protected void onPluginDisable() {
    }

    public StructureRegistry getStructureRegistry() {
        return getSingleton(StructureRegistry.class);
    }

    public TorusRecipeManager getRecipeManager() {
        return getSingleton(TorusRecipeManager.class);
    }

    public TorusWorldManager getWorldManager() {
        return getSingleton(TorusWorldManager.class);
    }

    public TorusPlayerManager getPlayerManager() {
        return getSingleton(TorusPlayerManager.class);
    }

    public static boolean usesPaperAPI() {
        return usesPaperAPI;
    }

    private static void checkPaperAPI() {
        try {
            Class.forName("io.papermc.paper.entity.TeleportFlag");
            usesPaperAPI = true;

            instance.getLogger().info("Using Paper API");
        } catch (Exception | Error e) {
            instance.getLogger().info("Using Spigot API. Some features may not function properly.");
        }
    }

}
