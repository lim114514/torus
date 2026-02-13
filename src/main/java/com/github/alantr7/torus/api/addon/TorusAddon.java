package com.github.alantr7.torus.api.addon;

import com.github.alantr7.torus.TorusPlugin;
import com.github.alantr7.torus.api.resource.Container;
import com.github.alantr7.torus.item.TorusItem;
import com.github.alantr7.torus.structure.Structure;
import com.github.alantr7.torus.utils.MathUtils;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TorusAddon {

    public final JavaPlugin plugin;

    public final String id;

    public final String name;

    public final Container externalContainer;

    public final Container classpathContainer;

    public final File rootDirectory;

    public final File configsDirectory, modelsDirectory, itemsDirectory, recipesDirectory;

    private boolean isContentLocked;

    private final List<TorusItem> registeredItems = new ArrayList<>();

    private final List<Structure> registeredStructures = new ArrayList<>();

    public int externalConfigsFlags;

    public TorusAddon(JavaPlugin plugin, String id, String name) {
        this.plugin = plugin;
        this.id = id;
        this.name = name;
        this.rootDirectory = new File(new File(TorusPlugin.getInstance().getDataFolder(), "configs"), id);
        this.configsDirectory = new File(rootDirectory, "structures");
        this.modelsDirectory = new File(rootDirectory, "models");
        this.itemsDirectory = new File(rootDirectory, "items");
        this.recipesDirectory = new File(rootDirectory, "recipes");
        this.externalContainer = Container.addonConfigs(this);
        this.classpathContainer = Container.classpath(plugin);
    }

    public boolean allowsExternalConfig(ConfigType configType) {
        return MathUtils.hasFlag(externalConfigsFlags, (int) Math.pow(2, configType.ordinal()));
    }

    public List<TorusItem> getRegisteredItems() {
        return Collections.unmodifiableList(registeredItems);
    }

    public List<Structure> getRegisteredStructures() {
        return Collections.unmodifiableList(registeredStructures);
    }

    public void registerContent(TorusItem item) {
        if (isContentLocked)
            return;

        registeredItems.add(item);
    }

    public void registerContent(Structure structure) {
        if (isContentLocked)
            return;

        registeredStructures.add(structure);
    }

}
