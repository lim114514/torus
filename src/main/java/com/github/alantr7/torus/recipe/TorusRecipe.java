package com.github.alantr7.torus.recipe;

import lombok.Getter;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;

public abstract class TorusRecipe implements Keyed {

    @Getter
    public final NamespacedKey key;

    public TorusRecipe(String key) {
        int index = key.indexOf(':');
        if (index != -1) {
            this.key = new NamespacedKey(key.substring(0, index), key.substring(index + 1));
        } else {
            this.key = new NamespacedKey("torus", key);
        }
    }

}
