package com.github.alantr7.torus.api.resource;

import com.github.alantr7.torus.api.addon.TorusAddon;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Objects;
import java.util.function.Function;

public class Container {

    public final int type;

    public final String root;

    public final Function<String, Resource> resourceGetFunction;

    private Container(int type, String root, Function<String, Resource> resourceGetFunction) {
        this.type = type;
        this.root = root;
        this.resourceGetFunction = resourceGetFunction;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Container container = (Container) o;
        return type == container.type && Objects.equals(root, container.root);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, root);
    }

    public static Container classpath(JavaPlugin plugin) {
        return new Container(0, "~" + plugin.getName(), path -> {
            InputStream stream = plugin.getResource(path);
            return stream != null ? new Resource(null, stream) : null;
        });
    }

    public static Container addonConfigs(TorusAddon addon) {
        return new Container(1, addon.rootDirectory.getPath(), path -> {
            File file = new File(addon.rootDirectory, path);
            if (file.exists()) {
                try {
                    return new Resource(file, new FileInputStream(file));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return new Resource(file, null);
        });
    }

}
