package com.github.alantr7.torus;

import com.github.alantr7.bukkitplugin.BukkitPlugin;
import com.github.alantr7.bukkitplugin.annotations.generative.JavaPlugin;
import com.github.alantr7.bukkitplugin.annotations.generative.SoftDepends;
import com.github.alantr7.bukkitplugin.annotations.relocate.Relocate;
import com.github.alantr7.bukkitplugin.annotations.relocate.Relocations;
import com.github.alantr7.torus.addon.DefaultAddonLifecycleAdapter;
import com.github.alantr7.torus.addon.TorusAddonManager;
import com.github.alantr7.torus.api.TorusAPI;
import com.github.alantr7.torus.api.addon.ConfigType;
import com.github.alantr7.torus.api.addon.TorusAddon;
import com.github.alantr7.torus.item.ItemRegistry;
import com.github.alantr7.torus.model.ModelLoader;
import com.github.alantr7.torus.model.ModelManager;
import com.github.alantr7.torus.player.TorusPlayerManager;
import com.github.alantr7.torus.recipe.TorusRecipeManager;
import com.github.alantr7.torus.structure.StructureRegistry;
import com.github.alantr7.torus.world.TorusWorldManager;
import lombok.Getter;
import org.bukkit.Bukkit;

@JavaPlugin(name = "Torus", version = "0.5.2", apiVersion = "1.21")
@Relocations(@Relocate(from = "com.github.alantr7.bukkitplugin", to = "com.github.alantr7.torus.bpf"))
@SoftDepends({"ProtocolLib", "ModelEngine"})
public class TorusPlugin extends BukkitPlugin {

    @Getter
    static TorusPlugin instance;

    public static TorusAddon DEFAULT_ADDON;

    @Getter
    protected final TorusAddonManager addonManager;

    @Getter
    protected ItemRegistry itemRegistry;

    private static boolean usesPaperAPI;

    public TorusPlugin() {
        instance = this;
        addonManager = new TorusAddonManager();

        checkPaperAPI();
    }

    @Override
    protected void onPluginEnable() {
        DEFAULT_ADDON = TorusAPI.newAddon(this, "torus")
          .name("Torus (Default)")
          .allowExternalConfigurations(ConfigType.STRUCTURE, ConfigType.MODEL, ConfigType.ITEMS, ConfigType.RECIPES)
          .register();

        TorusAPI.getAddonLifecycle().subscribe(DEFAULT_ADDON, new DefaultAddonLifecycleAdapter(DEFAULT_ADDON));
        itemRegistry = new ItemRegistry();

        Bukkit.getScheduler().runTaskLater(this, () -> addonManager.getLifecycle().start(), 1L);
    }

    @Override
    protected void onPluginDisable() {
    }

    public StructureRegistry getStructureRegistry() {
        return getSingleton(StructureRegistry.class);
    }

    public TorusRecipeManager getRecipeRegistry() {
        return getSingleton(TorusRecipeManager.class);
    }

    public TorusWorldManager getWorldManager() {
        return getSingleton(TorusWorldManager.class);
    }

    public TorusPlayerManager getPlayerManager() {
        return getSingleton(TorusPlayerManager.class);
    }

    public ModelLoader getModelLoader() {
        return getSingleton(ModelLoader.class);
    }

    public ModelManager getModelManager() {
        return getSingleton(ModelManager.class);
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
