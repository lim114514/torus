package com.github.alantr7.torus.api;

import com.github.alantr7.torus.TorusPlugin;
import com.github.alantr7.torus.api.addon.AddonBuilder;
import com.github.alantr7.torus.api.addon.Lifecycle;
import com.github.alantr7.torus.item.TorusItem;
import com.github.alantr7.torus.model.ModelLoader;
import com.github.alantr7.torus.structure.Structure;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class TorusAPI {

    public static AddonBuilder newAddon(JavaPlugin plugin, String namespace) {
        return new AddonBuilder(plugin, namespace);
    }

    public static Lifecycle getAddonLifecycle() {
        return TorusPlugin.getInstance().getAddonManager().getLifecycle();
    }

    public static TorusItem getItemById(String namespacedId) {
        return TorusPlugin.getInstance().getItemRegistry().getItemById(namespacedId);
    }

    public static TorusItem getItemByItemStack(ItemStack item) {
        return TorusPlugin.getInstance().getItemRegistry().getItemByItemStack(item);
    }

    public static Structure getStructureById(String namespacedId) {
        return TorusPlugin.getInstance().getStructureRegistry().getStructure(namespacedId);
    }

    public static ModelLoader getModelLoader() {
        return TorusPlugin.getInstance().getModelLoader();
    }

}
